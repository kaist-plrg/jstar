package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.algorithm.NormalHead
import kr.ac.kaist.jiset.util.UId

// control flow graph
class CFG(val spec: ECMAScript) {
  val funcs: Set[Function] = spec.algos.map(Translator(_)).toSet
  val nodes: Set[Node] = funcs.flatMap(_.nodes)
  val edges: Set[Edge] = funcs.flatMap(_.edges)
  val funcOf: Map[Node, Function] = funcs.flatMap(f => f.nodes.map(_ -> f)).toMap
  val next: Map[Linear, Node] =
    (edges.collect { case LinearEdge(x, y) => x -> y }).toMap
  val thenNext: Map[Branch, Node] =
    (edges.collect { case BranchEdge(x, y, _) => x -> y }).toMap
  val elseNext: Map[Branch, Node] =
    (edges.collect { case BranchEdge(x, _, y) => x -> y }).toMap
  val nexts: Map[Node, Set[Node]] = (edges.map {
    case LinearEdge(x, y) => x -> Set(y)
    case BranchEdge(x, y, z) => x -> Set(y, z)
  }).toMap
  val fidMap: Map[Int, Function] = (for (f <- funcs) yield f.uid -> f).toMap
  val algo2fid: Map[String, Int] = (for (f <- funcs) yield f.name -> f.uid).toMap

  //////////////////////////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // initial global variables and heaps
  def getGlobal: (Map[String, Value], Map[Addr, Obj]) = {
    val globalMethods = getGlobalMethods
    val (consts, intrinsics) = getNames
    val globalVars = (
      globalMethods ++
      consts.map(x => x -> Const(x.substring("CONST_".length))) ++
      intrinsics.map(x => x -> NamedAddr(x.substring("INTRINSIC_".length).replaceAll("_", ".")))
    )
    val heaps = Map[Addr, Obj]()
    (globalVars, heaps)
  }

  // get fids of syntax-directed algorithms
  def getSyntaxFids(lhs: String, method: String): Set[Int] =
    spec.getSyntaxAlgo(lhs, method).map(algo2fid(_))

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get global methods
  private def getGlobalMethods: Map[String, Value] = (for {
    func <- funcs
    name <- func.algo.head match {
      case (head: NormalHead) => Some(head.name)
      case _ => None
    }
  } yield name -> Clo(func.uid)).toMap

  // get constant names
  private def getNames: (Set[String], Set[String]) = {
    var consts: Set[String] = Set()
    var intrinsics: Set[String] = Set()
    object ConstExtractor extends UnitWalker {
      override def walk(id: Id) = {
        if (id.name startsWith "CONST_") consts += id.name
        if (id.name startsWith "INTRINSIC_") intrinsics += id.name
      }
    }
    for (algo <- spec.algos) ConstExtractor.walk(algo.rawBody)
    (consts, intrinsics)
  }
}
