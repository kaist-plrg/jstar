package kr.ac.kaist.jstar.analyzer

import kr.ac.kaist.jstar.LINE_SEP
import kr.ac.kaist.jstar.cfg._
import kr.ac.kaist.jstar.ir._
import kr.ac.kaist.jstar.ir.Beautifier._
import kr.ac.kaist.jstar.spec._
import kr.ac.kaist.jstar.spec.algorithm._
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.util.Appender
import scala.Console._
import scala.util.matching.Regex
import scala.annotation.tailrec

object AbsSemantics {
  // ECMAScript
  type NP = NodePoint[_ <: Node]

  // internal map from control points to abstract states
  var npMap: Map[NP, AbsState] = initNpMap
  var rpMap: Map[ReturnPoint, AbsType] = Map()

  // internal map for return edges
  var retEdges: Map[ReturnPoint, Set[(NodePoint[Call], String)]] = Map()

  // internal set of unknown variables with control points
  var unknownVars: Set[(ControlPoint, String)] = Set()

  // assertion control points
  var assertions: Map[ControlPoint, (AbsType, Expr)] = Map()

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get node points by id
  def getNodePointsById(uid: Int): Set[NP] =
    npMap.keySet.filter(x => (x.node: Node).uid == uid)

  // get return points by function name
  def getReturnPointByName(fname: String): Set[ReturnPoint] =
    rpMap.keySet.filter(_.func.name == fname)

  // get views by function name
  def getRpsForREPLByName(fname: String): Set[ReturnPoint] =
    npMap.keySet.flatMap {
      case NodePoint(entry: Entry, view) =>
        val func = cfg.funcOf(entry)
        if (func.name == fname) Some(ReturnPoint(func, view))
        else None
      case _ => None
    }

  // lookup
  def apply(np: NP): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): AbsType = rpMap.getOrElse(rp, AbsType.Bot)

  // get return edges
  def getRetEdges(rp: ReturnPoint): Set[(NodePoint[Call], String)] =
    retEdges.getOrElse(rp, Set())

  // get all control points
  def getAllControlPoints: Set[ControlPoint] =
    npMap.keySet ++ rpMap.keySet

  // no return check
  def noReturnCheck: Unit = {
    val set = npMap.keySet.map {
      case NodePoint(node, view) => ReturnPoint(cfg.funcOf(node), view)
    } -- rpMap.keySet
    for (rp <- set) warning(s"no return: ${getString(rp, "", false)}")
  }

  // reference check
  def referenceCheck: Unit = Stat.doCheck({
    for ((cp, x) <- unknownVars) {
      val cpStr = getString(cp, "", false)
      alarm(s"unknown variable: $x", cp = cp, cpStr = cpStr)
    }
  })

  // assertions check
  def assertionCheck: Unit = Stat.doCheck({
    assertions.foreach {
      case (cp, (t, expr)) =>
        val cpStr = getString(cp, "", false)
        if (!(AT ⊑ t)) alarm(s"assertion failed: ${expr.beautified}", cp = cp, cpStr = cpStr)
        else if (AF ⊑ t) warning(s"maybe assertion failed: ${expr.beautified}", cp = cp, cpStr = cpStr)
    }
  })

  // get size
  def size: Int = npMap.size + rpMap.size

  // update internal map
  def +=[T <: Node](pair: (NodePoint[T], AbsState)): Boolean = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += Stat.inc(np)
      true
    }
    false
  }

  // handle parameters
  def getEntryState(
    call: Call,
    params: List[Param],
    args: List[Type]
  ): AbsState = {
    var st = AbsState.Empty
    import Param.Kind._
    @tailrec
    def aux(ps: List[Param], as: List[Type]): Unit = (ps, as) match {
      case (Param(_, Normal) :: pl, Absent :: al) =>
        st = AbsState.Bot
      case (param :: pl, arg :: al) =>
        st = st.define(param.name, arg.abs, param = true)
        aux(pl, al)
      case (Param(name, kind) :: tl, Nil) =>
        if (kind == Normal) {
          Stat.doCheck(alarm(s"remaining parameter: $name"))
          st = AbsState.Bot
        } else st = st.define(name, Absent.abs, param = true)
        aux(tl, Nil)
      case (Nil, Nil) =>
      case (Nil, args) =>
        Stat.doCheck(alarm(s"remaining arguments: ${args.mkString(", ")}"))
      case _ =>
        warning(s"consider variadic: (${params.mkString(", ")}) and (${args.mkString(", ")}) @ $call")
    }
    aux(params, args)
    st
  }

  // pruning this value for method algorithms
  def pruneThis(func: Function, args: List[AbsType]): List[AbsType] = {
    (func.algo.head, args) match {
      case (head: MethodHead, thisT :: args) => NameT(head.base) :: args
      case _ => args
    }
  }

  // handle calls
  def doCall(
    call: Call,
    callView: View,
    func: Function,
    args: List[AbsType],
    retVar: String
  ): Unit = for {
    tys <- getTypes(pruneThis(func, args))
    view = View(tys)
  } {
    val params = func.algo.params
    val entrySt = getEntryState(call, params, tys)
    val np = NodePoint(func.entry, view)
    this += np -> entrySt

    val rp = ReturnPoint(func, view)
    val callNP = NodePoint(call, callView)
    val set = retEdges.getOrElse(rp, Set()) + ((callNP, retVar))
    retEdges += rp -> set

    val retT = this(rp)
    if (!retT.isBottom) worklist += rp
  }

  // update return points
  def doReturn(rp: ReturnPoint, t: AbsType): Unit = {
    val newT = t.noAbsent
    val oldT = this(rp)
    if (newT !⊑ oldT) {
      rpMap += rp -> (oldT ⊔ newT)
      worklist += Stat.inc(rp)
    }
  }

  // get function of given control points
  def funcOf(cp: ControlPoint): Function = cp match {
    case NodePoint(node, _) => cfg.funcOf(node)
    case ReturnPoint(func, _) => func
  }

  // conversion to string
  override def toString: String = getString("")
  def getInfo: String = {
    val app = new Appender
    val (numFunc, numAlgo, numRp) = numOfFuncAlgoRp
    app >> numFunc >> " out of " >> numAlgo >> " functions analyzed with "
    app >> numRp >> " return points" >> LINE_SEP
    app >> "# of iterations: " >> Stat.iter
    app.toString
  }
  def numOfFuncAlgoRp: (Int, Int, Int) = (
    rpMap.keySet.map(_.func).toSet.size,
    cfg.spec.algos.length,
    rpMap.size
  )

  // get string for result of control points
  def getString(color: String): String = rpMap.keySet.toList.map(rp => {
    val ReturnPoint(func, view) = rp
    val entryCP = NodePoint(func.entry, view)
    val from = this(entryCP)
    val to = this(rp)
    setColor(color)(s"${func.name}:$view:") + s" $from ---> $to"
  }).sorted.mkString(LINE_SEP)
  def getString(cp: ControlPoint): String = getString(cp, "", true)
  def getString(cp: ControlPoint, color: String, detail: Boolean): String = {
    val func = funcOf(cp).name
    val k = setColor(color)(s"$func:$cp")
    if (detail) {
      val v = cp match {
        case (np: NP) => this(np).toString
        case (rp: ReturnPoint) => this(rp).toString
      }
      s"$k -> $v"
    } else k
  }

  // get arguments
  def getArgs(head: SyntaxDirectedHead): List[AbsType] = head.types.map {
    case (name, astName) =>
      val ty = AstT(astName).abs
      if (head.optional contains name) ty ⊔ Absent.abs
      else ty
  }

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // initialization of node points with abstract states
  private def initNpMap: Map[NodePoint[_ <: Node], AbsState] = (for {
    func <- cfg.funcs.toList
    (tys, st) <- getAlgoTypes(func.algo)
    view = View(tys)
    cp = NodePoint(func.entry, view)
  } yield cp -> st).toMap

  private def isTarget(algo: Algo): Boolean = (
    isTargetHead(algo.head) &&
    !isRegex(algo) &&
    // !isEarlyErrors(algo) &&
    isTargetPattern(algo.name)
  )

  private def isTargetHead(head: Head): Boolean = head match {
    case (head: SyntaxDirectedHead) => head.withParams.isEmpty
    case (head: BuiltinHead) => (
      TARGET_BUILTIN.contains(head.ref.base) &&
      !NON_TARGET_BUILTIN.contains(head.ref.beautified)
    )
    case _ => false
  }

  private def isTargetPattern(name: String): Boolean = TARGET match {
    case Some(pattern) => pattern.r.matches(name)
    case None => true
  }

  private def isRegex(algo: Algo): Boolean =
    algo.isParent("sec-regexp-regular-expression-objects") // 22.2 RegExp

  private def isEarlyErrors(algo: Algo): Boolean =
    algo.name.endsWith("EarlyErrors")

  // initial abstract state for syntax-directed algorithms
  private def getAlgoTypes(algo: Algo): List[(List[Type], AbsState)] = algo.head match {
    case (head: SyntaxDirectedHead) if isTarget(algo) =>
      head.optional.subsets.map(opt => {
        var st = AbsState.Empty
        val tys = head.types.map {
          case (name, _) if opt contains name =>
            st = st.define(name, Absent.abs)
            Absent
          case (name, astName) =>
            st = st.define(name, AstT(astName).abs)
            AstT(astName)
        }
        (tys, st)
      }).toList
    case (head: BuiltinHead) if isTarget(algo) =>
      val tys = Nil
      var st = AbsState.Empty
      st = st.define(THIS_PARAM, ESValueT)
      st = st.define(ARGS_LIST, ListT(ESValueT))
      st = st.define(NEW_TARGET, AbsType(NameT("Object"), Undef))
      List((tys, st))
    case _ => Nil
  }

  // get types from abstract values
  private def getTypes(args: List[AbsType]): List[List[Type]] = {
    args.foldRight(List(List[Type]())) {
      case (aty, tysList) => for {
        tys <- tysList
        ty <- aty.set
      } yield ty.upcast :: tys
    }
  }
}
