package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.analyzer.domain.obj._

object BasicDomain extends state.Domain {
  // abstraction function
  def alpha(st: State): Elem = Elem(AbsEnv(st.env), AbsHeap(st.heap))

  // bottom value
  val Bot: Elem = Elem(AbsEnv.Bot, AbsHeap.Bot)

  // top value
  val Top: Elem = Elem(AbsEnv.Top, AbsHeap.Top)

  // empty value
  val Empty: Elem = Elem(AbsEnv.Empty, AbsHeap.Empty)

  // constructor
  def apply(env: AbsEnv = AbsEnv.Bot, heap: AbsHeap = AbsHeap.Bot): Elem =
    Elem(env, heap)

  // extractor
  def unapply(elem: Elem): Option[(AbsEnv, AbsHeap)] = Some((elem.env, elem.heap))

  case class Elem(
    env: AbsEnv = AbsEnv.Bot,
    heap: AbsHeap = AbsHeap.Bot
  ) extends ElemTrait {
    // bottom check
    override def isBottom: Boolean = (this eq Bot) || (this == Bot)

    // partial order
    def ⊑(that: Elem): Boolean = (
      (this eq that) ||
      this.isBottom ||
      !that.isBottom && (
        this.env ⊑ that.env &&
        this.heap ⊑ that.heap
      )
    )

    // join operator
    def ⊔(that: Elem): Elem = if (this eq that) this else Elem(
      this.env ⊔ that.env,
      this.heap ⊔ that.heap
    )

    // meet operator
    def ⊓(that: Elem): Elem = if (this eq that) this else Elem(
      this.env ⊓ that.env,
      this.heap ⊓ that.heap
    ).normalized

    // concretization function
    def gamma: concrete.Set[State] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[State] = Many

    private def checkBot(st: => Elem): Elem = if (isBottom) this else st

    // define variable
    def +(pair: (String, AbsValue)): Elem = {
      val (_, v) = pair
      if (v.isBottom) Bot
      else checkBot(copy(env = env + pair))
    }

    // exists
    // def exists(sem: AbsSemantics, ref: AbsRefValue): AbsBool = ref match {
    //   case AbsRefValue.Id(x) =>
    //     val (localV, absent) = env(x)
    //     (localV.isBottom, absent.isTop) match {
    //       case (true, true) => AbsBool(sem.globalEnv.contains(x))
    //       case (true, false) => AbsBool.Bot // infeasible?
    //       case (false, true) => AbsBool.Top
    //       case (false, false) => AT
    //     }
    //   case AbsRefValue.Prop(base, prop) =>
    //     var b: AbsBool = AbsBool.Bot
    //     val pure = base.escaped
    //     val astr = pure.str
    //     // ty
    //     // TODO consider not string ty prop?
    //     for (ty <- pure.ty) b = b ⊔ existsTy(sem, ty.name, prop.str)
    //     // loc
    //     for (loc <- pure.loc) b = b ⊔ existsLoc(sem, loc, prop)
    //     // string
    //     if (!astr.isBottom) b ⊔= existsStr(astr, prop)
    //     // TODO consider other cases?
    //     b
    // }

    // // exists ty property
    // private def existsTy(
    //   sem: AbsSemantics,
    //   ty: String,
    //   prop: String
    // ): AbsBool = sem.typeMap.get(ty) match {
    //   case Some(TyInfo(_, parent, props)) =>
    //     if (props.contains(prop)) AT
    //     else parent.fold(AF)(existsTy(sem, _, prop))
    //   case None if (ty == "SubMap") => ???
    //   case None => ???
    // }
    // private def existsTy(
    //   sem: AbsSemantics,
    //   ty: String,
    //   aprop: AbsStr
    // ): AbsBool =
    //   if (ty == "SubMap") AbsBool.Top // TODO unsound
    //   else if (aprop.isBottom) AbsBool.Bot
    //   else aprop.gamma match {
    //     case Infinite => ???
    //     case Finite(ps) => ps.foldLeft(AbsBool.Bot: AbsBool) {
    //       case (b, Str(prop)) => b ⊔ existsTy(sem, ty, prop)
    //     }
    //   }

    // // exists location property
    // private def existsLoc(
    //   sem: AbsSemantics,
    //   loc: Loc,
    //   aprop: AbsPure
    // ): AbsBool = {
    //   import AbsObj._
    //   // get object
    //   val aobj = loc match {
    //     case (_: NamedAddr) if sem.globalHeap.contains(loc) => sem.globalHeap(loc)
    //     case _ if heap.keySet.contains(loc) => heap(loc)
    //     case _ => ???
    //   }
    //   // check prop existence
    //   aobj match {
    //     case MapElem(Some("SubMap"), _) => AbsBool.Top // TODO unsound
    //     case MapElem(ty, map) => aprop.str.gamma match {
    //       case Infinite => ???
    //       case Finite(props) => props.foldLeft(AbsBool.Bot: AbsBool) {
    //         case (b, Str(prop)) =>
    //           val aopt = map(prop)
    //           (aopt.value.isBottom, aopt.absent.isTop) match {
    //             case (true, true) => ty.fold(AF)(existsTy(sem, _, prop))
    //             case (true, false) => AbsBool.Bot // infeasible?
    //             case (false, true) => AbsBool.Top
    //             case (false, false) => AT
    //           }
    //       }
    //     }
    //     case ListElem(list) =>
    //       val isLength = aprop.str.getSingle match {
    //         case One(Str("length")) => AT
    //         case _ => ???
    //       }
    //       val isNum =
    //         if (aprop.num.isBottom || list.length.isBottom) AbsBool.Bot
    //         else if (aprop.num ⊑ list.length) AT
    //         else if ((aprop.num ⊓ list.length).isBottom) AF
    //         else AbsBool.Top
    //       // other prop
    //       val alistProp = AbsPure(
    //         prim = AbsPrim(str = AbsStr.Top, num = AbsNum.Top)
    //       )
    //       if (aprop !⊑ alistProp) ???
    //       isLength ⊔ isNum
    //     case SymbolElem(desc) => ???
    //     case AbsObj.Top => ???
    //     case AbsObj.Bot => ???
    //   }
    // }

    // // exists string property
    // private def existsStr(astr: AbsStr, prop: AbsPure): AbsBool = {
    //   // number
    //   val isNum =
    //     if (prop.num.isBottom) AbsBool.Bot
    //     else (astr.getSingle, prop.num.getSingle) match {
    //       case (One(Str(str)), One(Num(len))) => AbsBool(len < str.length)
    //       case _ => ???
    //     }
    //   // length
    //   val isLength = prop.str.getSingle match {
    //     case One(Str("length")) => AT
    //     case _ => AbsBool.Top
    //   }
    //   // other prop
    //   val astrProp = AbsPure(
    //     prim = AbsPrim(str = AbsStr.Top, num = AbsNum.Top)
    //   )
    //   if (prop !⊑ astrProp) ???
    //   isNum ⊔ isLength
    // }

    // update references
    def update(sem: AbsSemantics, refv: AbsRefValue, v: AbsValue): Elem =
      checkBot(refv match {
        case AbsRefValue.Id(x) =>
          val (localV, absent) = env(x)
          if (!localV.isBottom) {
            if (absent.isTop) alarm(s"unknown local variable: $x")
            this + (x -> v)
          } else if (absent.isTop) sem.globalEnv.get(x) match {
            case Some(globalV) =>
              if (!(v ⊑ globalV))
                alarm(s"wrong update of global variable $x with ${beautify(v)}")
              this
            case None =>
              alarm(s"unknown variable: $x", error = true)
              this
          }
          else Bot
        case AbsRefValue.Prop(base, prop) =>
          copy(heap = base.escaped.loc.toSet.foldLeft(heap) {
            case (h, l: NamedAddr) => ???
            case (h, l: Loc) =>
              val obj = update(heap(l), prop, v)
              h + (l -> obj)
            case _ => ???
          })
        case _ => ???
      })

    // update
    def update(obj: AbsObj, prop: AbsPure, value: AbsValue): AbsObj = {
      import AbsObj._
      obj match {
        case MapElem(Some("SubMap"), _) => obj // TODO to be more precise
        case MapElem(ty, map) => prop.getSingle match {
          case Zero => AbsObj.Bot
          case One(Str(p)) => MapElem(ty, map + (p -> value))
          case _ => obj // TODO unsound
        }
        case _ => ???
      }
    }

    // update references
    def delete(sem: AbsSemantics, refv: AbsRefValue): Elem = checkBot(???)

    // lookup helper
    def lookup(sem: AbsSemantics, base: String, props: String*): AbsValue = {
      val baseV = lookupVariable(sem, base)
      props.foldLeft(baseV) {
        case (v, prop) =>
          val pureV = v.escaped
          lookupProp(sem, pureV, AbsPure(prop))
      }
    }

    // lookup reference values
    def lookup(sem: AbsSemantics, refv: AbsRefValue): AbsValue = refv match {
      case AbsRefValue.Bot => AbsValue.Bot
      case AbsRefValue.Top => AbsValue.Top
      case AbsRefValue.Id(x) => lookupVariable(sem, x)
      case AbsRefValue.Prop(base, prop) => lookupProp(sem, base, prop)
    }
    private def lookupVariable(sem: AbsSemantics, x: String): AbsValue = {
      val (localV, absent) = env(x)
      val globalV: AbsValue = if (absent.isTop) sem.globalEnv.getOrElse(x, {
        alarm(s"unknown variable: $x", error = true)
        AbsAbsent.Top
      })
      else AbsValue.Bot
      localV ⊔ globalV
    }
    private def lookupProp(
      sem: AbsSemantics,
      base: AbsValue,
      prop: AbsPure
    ): AbsValue = {
      var v = AbsValue.Bot
      val pure = prop.str.getSingle match {
        case One(Str("Type")) =>
          v ⊔= AbsConst(base.comp.map.keySet.map(t => Const(t.toString)))
          base.pure
        case One(Str("Value")) =>
          for ((x, _) <- base.comp.map.values) v ⊔= x
          base.pure
        case One(Str("Target")) =>
          for ((_, t) <- base.comp.map.values) v ⊔= t
          base.pure
        case _ => base.escaped
      }
      for (ast <- pure.ast) v ⊔= lookupAST(sem, ast.name, prop.str)
      for (ty <- pure.ty) v ⊔= lookupTy(sem, ty.name, prop.str)
      for (loc <- pure.loc) v ⊔= lookupLoc(sem, loc, prop)
      if (!pure.str.isBottom) v ⊔= lookupStr(pure.str, prop)
      v
    }

    // lookup AST
    def lookupAST(sem: AbsSemantics, lhs: String, prop: AbsStr): AbsValue =
      if (prop.isBottom) AbsValue.Bot
      else prop.getSingle match {
        case One(Str(rhs)) =>
          val rhsSet = sem.spec.getRhsNT(lhs)
          if (rhsSet contains rhs) AbsAST(ASTVal(rhs))
          else AbsValue.Bot
        case _ => ???
      }

    // lookup strings
    def lookupStr(str: AbsStr, prop: AbsPure): AbsValue = if (!str.isBottom) {
      var v = AbsValue.Bot
      if (!prop.num.isBottom) v ⊔= AbsStr.Top
      if (AbsValue("length") ⊑ prop.str) v ⊔= AbsNum.Top
      v
    } else AbsValue.Bot

    // lookup objects
    def lookupObj(sem: AbsSemantics, obj: AbsObj, prop: AbsPure): AbsValue = {
      import AbsObj._
      obj match {
        case MapElem(Some("SubMap"), _) => AbsAbsent.Top // TODO
        case MapElem(ty, map) => prop.str.gamma.map(s => map(s.str)) match {
          case Finite(set) =>
            val vopt = set.foldLeft[MapD.AbsVOpt](MapD.AbsVOpt.Bot)(_ ⊔ _)
            val typeV = if (vopt.absent.isTop) {
              val typeV = ty.fold(AbsValue.Bot)(lookupTy(sem, _, prop.str))
              if (typeV.isBottom) alarm(s"unknown property: ${beautify(prop)} @ ${beautify(obj)}")
              typeV
            } else AbsValue.Bot
            vopt.value ⊔ typeV
          case Infinite =>
            alarm(s"top string property @ ${beautify(obj)}")
            AbsValue.Bot
        }
        case ListElem(list) =>
          val strV: AbsValue = prop.str.getSingle match {
            case One(Str("length")) | Many => list.length
            case _ => AbsValue.Bot
          }
          val numV =
            if (prop.num.isBottom) AbsValue.Bot
            else list.value
          strV ⊔ numV
        case SymbolElem(desc) =>
          alarm(s"access of the property ${beautify(prop)} for a symbol @${beautify(desc)}")
          AbsValue.Bot
        case AbsObj.Top =>
          alarm(s"access of the property ${beautify(prop)} for the top object")
          AbsValue.Bot
        case AbsObj.Bot =>
          alarm(s"access of the property ${beautify(prop)} for the bottom object")
          AbsValue.Bot
      }
    }

    // lookup type properties
    def lookupTy(sem: AbsSemantics, ty: String, prop: String): AbsValue = {
      sem.typeMap.get(ty) match {
        case Some(info) =>
          val props = info.props
          props.getOrElse(prop, info.parent.fold({
            alarm(s"unknown property: ${prop} @ ${ty}")
            AbsValue.Bot
          })(lookupTy(sem, _, prop)))
        case None if (ty == "SubMap") => AbsAbsent.Top // TODO unsound
        case None =>
          alarm(s"unknown type: $ty")
          AbsValue.Bot
      }
    }
    def lookupTy(sem: AbsSemantics, ty: String, prop: AbsStr): AbsValue = {
      // TODO SubMap types
      if (ty == "SubMap") AbsAbsent.Top /* TODO unsound */ else prop.gamma match {
        case Infinite => AbsValue.Top
        case Finite(ps) => ps.toList.foldLeft(AbsValue.Bot) {
          case (v, Str(prop)) => v ⊔ lookupTy(sem, ty, prop)
        }
      }
    }

    // lookup properties
    def lookupLoc(sem: AbsSemantics, loc: Loc, prop: AbsPure): AbsValue = {
      val obj = lookupLoc(sem, loc)
      lookupObj(sem, obj, prop)
    }

    // lookup locations
    def lookupLoc(sem: AbsSemantics, loc: Loc): AbsObj = heap.lookupLoc(sem, loc)

    // allocate a new symbol
    def allocSymbol(
      fid: Int,
      asite: Int,
      desc: String
    ): (AbsPure, Elem) = {
      import AbsObj._
      val loc = AllocSite(fid, asite)
      val obj: AbsObj = SymbolElem(AbsStr(desc))
      (AbsPure(loc), copy(heap = heap + (loc -> obj)))
    }

    // allocate a new map
    def allocMap(
      fid: Int,
      asite: Int,
      ty: String,
      props: Map[String, AbsValue]
    ): (AbsPure, Elem) = {
      import AbsObj._
      val map: MapD = MapD(props.map {
        case (k, v) => k -> MapD.AbsVOpt(v)
      }, MapD.AbsVOpt(None))
      val loc = AllocSite(fid, asite)
      val obj: AbsObj = MapElem(Some(ty), map)
      (AbsPure(loc), copy(heap = heap + (loc -> obj)))
    }

    // allocate a new list
    def allocList(
      fid: Int,
      asite: Int,
      vs: List[AbsValue]
    ): (AbsPure, Elem) = {
      import AbsObj._
      val list: ListD = ListD(vs.foldLeft(AbsValue.Bot)(_ ⊔ _))
      val loc = AllocSite(fid, asite)
      val obj: AbsObj = ListElem(list)
      (AbsPure(loc), copy(heap = heap + (loc -> obj)))
    }

    def prune(
      sem: AbsSemantics,
      refv: AbsRefValue,
      target: PruneTarget,
      cond: Boolean
    ): Elem = refv match {
      case AbsRefValue.Id(x) =>
        val (localV, absent) = env(x)
        if (!localV.isBottom) {
          val newV: AbsValue = target match {
            case PruneSingle(pv) =>
              if (cond) localV ⊓ AbsValue(AbsPure.alpha(pv), AbsComp.alpha(pv))
              else localV.prune(pv)
            case PruneInstance(name) =>
              val map = groupByInstance(sem, localV.escaped)
              val set = sem.getTypes(name)
              val exclude = if (cond) map.keySet -- set else set
              val pure = (map -- exclude)
                .values.foldLeft(AbsPure.Bot)(_ ⊔ _)
              if (pure.ty.isBottom || !cond) pure
              else pure.copy(ty = AbsTy(name))
          }
          // normalize
          if (newV.escaped.isBottom) Bot else this + (x -> newV)
        } else if (sem.globalEnv contains x) this else Bot
      case AbsRefValue.Prop(base, prop) => this // TODO
      case _ => ???
    }

    // append an element to a list
    def append(sem: AbsSemantics, v: AbsValue, loc: AbsLoc): Elem =
      insert(sem, v, loc)

    // prepend an element to a list
    def prepend(sem: AbsSemantics, v: AbsValue, loc: AbsLoc): Elem =
      insert(sem, v, loc)

    // insert (prepend or append)
    private def insert(sem: AbsSemantics, v: AbsValue, loc: AbsLoc): Elem = {
      import AbsObj._
      copy(heap = loc.toSet.foldLeft(heap) {
        case (heap, loc: NamedAddr) =>
          alarm(s"try to insert ${beautify(v)} to named locess ${beautify(loc)}")
          heap
        case (heap, loc) => heap(loc) match {
          case ListElem(list) => heap + (loc -> ListElem(ListD(list.value ⊔ v)))
          case obj =>
            alarm(s"try to insert ${beautify(v)} to ${beautify(obj)}")
            heap
        }
      })
    }

    // copy an object
    def copyOf(
      sem: AbsSemantics,
      fid: Int,
      asite: Int,
      pure: AbsPure
    ): (AbsPure, Elem) = {
      val objs = pure.loc.toList.map(lookupLoc(sem, _))
      val obj = objs.foldLeft[AbsObj](AbsObj.Bot)(_ ⊔ _)
      val loc = AllocSite(fid, asite)
      (AbsPure(loc), copy(heap = heap + (loc -> obj)))
    }

    // get keys of an object
    def keysOf(v: AbsValue): (AbsValue, Elem) = ???

    // pop a value from a list
    def pop(
      sem: AbsSemantics,
      list: AbsPure,
      idx: AbsValue
    ): (AbsValue, Elem) = {
      val newV = list.loc.toList.foldLeft(AbsValue.Bot) {
        case (value, loc) =>
          val obj = lookupLoc(sem, loc)
          obj match {
            case AbsObj.ListElem(list) => value ⊔ list.value
            case _ =>
              alarm(s"try to pop from a non-list object: ${beautify(obj)}")
              value
          }
      }
      if (newV.isBottom) (AbsValue.Bot, Bot) else (newV, this)
    }

    // get type of pure values
    def typeOf(sem: AbsSemantics, pv: AbsPure): Set[Str] =
      groupByType(sem, pv).keySet.map(Str(_))
    def groupByType(sem: AbsSemantics, pv: AbsPure): Map[String, AbsPure] = {
      import AbsObj._
      var map = Map[String, AbsPure]()
      def add(name: String, pure: AbsPure): Unit =
        map += name -> (map.getOrElse(name, AbsPure.Bot) ⊔ pure)
      if (!pv.loc.isBottom) for (loc <- pv.loc) lookupLoc(sem, loc) match {
        case SymbolElem(_) =>
          add("Symbol", AbsPure(loc))
        case MapElem(Some(parent), _) if parent endsWith "Object" =>
          add("Object", AbsPure(loc))
        case obj =>
          alarm(s"try to get types of object: ${beautify(obj)}")
      }
      if (!pv.ty.isBottom) for (ty <- pv.ty) {
        val name = ty.name
        if (name endsWith "Object") add("Object", AbsTy(ty))
        else alarm(s"try to get types of type: ${beautify(name)}")
      }
      if (!pv.const.isBottom) alarm(s"try to get types of constant: ${beautify(pv.const)}")
      if (!pv.clo.isBottom) alarm(s"try to get types of closure: ${beautify(pv.clo)}")
      if (!pv.cont.isBottom) alarm(s"try to get types of continuation: ${beautify(pv.cont)}")
      if (!pv.ast.isBottom) alarm(s"try to get types of AST: ${beautify(pv.ast)}")
      if (!pv.num.isBottom) add("Number", pv.num)
      if (!pv.bigint.isBottom) add("BigInt", pv.bigint)
      if (!pv.str.isBottom) add("String", pv.str)
      if (!pv.bool.isBottom) add("Boolean", pv.bool)
      if (!pv.undef.isBottom) add("Undefined", pv.undef)
      if (!pv.nullval.isBottom) add("Null", pv.nullval)
      if (!pv.absent.isBottom) add("Absent", pv.absent)
      map
    }

    // check instances
    def isInstanceOf(
      sem: AbsSemantics,
      pv: AbsPure,
      name: String
    ): AbsBool = {
      val set = sem.getTypes(name)
      val names = groupByInstance(sem, pv).keySet
      var bool: AbsBool = AbsBool.Bot
      if (!(set intersect names).isEmpty) bool ⊔= AT
      if (!(names - name).isEmpty) bool ⊔= AF
      bool
    }
    def groupByInstance(
      sem: AbsSemantics,
      pv: AbsPure
    ): Map[String, AbsPure] = {
      var map = Map[String, AbsPure]()
      def add(name: String, pure: AbsPure): Unit =
        map += name -> (map.getOrElse(name, AbsPure.Bot) ⊔ pure)
      if (!pv.loc.isBottom) for (loc <- pv.loc) lookupLoc(sem, loc) match {
        case AbsObj.MapElem(Some(parent), _) => add(parent, AbsPure(loc))
        case obj => add("_", AbsPure(loc))
      }
      if (!pv.ty.isBottom) for (ty <- pv.ty) add(ty.name, AbsTy(ty))
      if (!pv.ast.isBottom) for (ast <- pv.ast) add(ast.name, AbsAST(ast))
      val remain = pv.copy(
        loc = AbsLoc.Bot,
        ty = AbsTy.Bot,
        ast = AbsAST.Bot,
      )
      if (!remain.isBottom) add("_", remain)
      map
    }

    // check whether lists contains elements
    def contains(list: AbsPure, v: AbsPure): AbsValue = AbsBool.Top // TODO
  }
}
