package kr.ac.kaist.jstar.analyzer

import kr.ac.kaist.jstar.ir._
import kr.ac.kaist.jstar.ir.Beautifier._
import kr.ac.kaist.jstar.util.Useful._
import scala.Console.RED

trait RefineHelper { this: AbsTransfer.Helper =>
  import AbsState.monad._

  // pruning abstract states
  def refine(
    st: AbsState,
    expr: Expr,
    pass: Boolean
  ): Updater = {
    val refineVar = RefineVar(st, pass)
    st => expr match {
      case _ if !REFINE => st
      case refineVar(newSt) => newSt
      case _ => st
    }
  }

  case class RefineVar(st: AbsState, pass: Boolean) {
    // refined variable names
    var refinedVars: Set[String] = Set()

    // escape refined variables
    private def escaped(st: AbsState): AbsState = AbsState(
      reachable = st.reachable,
      map = {
        (st.map.map {
          case (v, atype) if refinedVars contains v => v -> atype.uncheckEscaped
          case (v, atype) => v -> atype
        }).toMap
      }
    )

    // helper
    val refine = this
    def not: RefineVar = RefineVar(st, !pass)
    def unapply(expr: Expr): Option[AbsState] = optional(this(expr))

    // refine state
    private def apply(expr: Expr): AbsState = expr match {
      case EUOp(ONot, expr) => not(expr)
      // refine normal completion
      case EBOp(OEq, rexpr @ ERef(RefProp(ref, EStr("Type"))), ERef(RefId(Id("CONST_normal")))) if ref.isVar =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(ERef(ref), a, check = false))
          newT = refineNormalComp(l, pass)
        } yield (ERef(ref), a, newT)
        update(st, updator)
      case EBOp(OEq, lexpr @ ERef(ref), right) if ref.isVar =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(lexpr, a, check = false))
          r <- transfer(right)
          newT = refineValue(l.escaped(lexpr), r.escaped(right), pass)
        } yield (lexpr, a, newT)
        refinedVars += ref.getId
        update(st, updator)
      case EIsInstanceOf(base @ ERef(ref), name) if ref.isVar =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(base, a, check = false))
          newT = refineInstance(l.escaped(base), name, pass)
        } yield (base, a, newT)
        refinedVars += ref.getId
        update(st, updator)
      case EBOp(OEq, ETypeOf(left @ ERef(ref)), right) if ref.isVar =>
        val updator = for {
          a <- transfer(ref)
          l <- get(_.lookup(left, a, check = false))
          r <- transfer(right)
          newT = refineType(l.escaped(left), r.escaped(right), pass)
        } yield (left, a, newT)
        refinedVars += ref.getId
        update(st, updator)
      case EBOp(OOr, refine(st0), refine(st1)) =>
        val est0 = escaped(st0)
        val est1 = escaped(st1)
        if (pass) est0 ⊔ est1 else est0 ⊓ est1
      case EBOp(OAnd, refine(st0), refine(st1)) =>
        val est0 = escaped(st0)
        val est1 = escaped(st1)
        if (pass) est0 ⊓ est1 else est0 ⊔ est1
      case _ => st
    }
  }

  // update state
  private def update(
    st: AbsState,
    updator: Result[(Expr, AbsRef, AbsType)]
  ): AbsState = {
    val ((rexpr, aref, newT), newSt) = updator(st)
    if (newT.isBottom) AbsState.Bot
    else newSt.update(rexpr, aref, newT)
  }

  // pruning for normal completion
  def refineNormalComp(l: AbsType, pass: Boolean): AbsType =
    if (pass) AbsType(l.compSet) - AbruptT else l ⊓ AbruptT

  // pruning for value checks
  def refineValue(l: AbsType, r: AbsType, pass: Boolean): AbsType = {
    optional[AbsType](if (pass) l ⊓ r else r.set.head match {
      case t: SingleT if r.set.size == 1 => l - t
    }).getOrElse(l)
  }

  // pruning for type checks
  def refineType(l: AbsType, r: AbsType, pass: Boolean): AbsType = {
    optional[AbsType](r.set.head match {
      case Str(name) if r.set.size == 1 =>
        val t: Type = name match {
          case "Object" => NameT("Object")
          case "Reference" => NameT("ReferenceRecord")
          case "Symbol" => SymbolT
          case "Number" => NumT
          case "BigInt" => BigIntT
          case "String" => StrT
          case "Boolean" => BoolT
          case "Undefined" => Undef
          case "Null" => Null
        }
        if (pass) l ⊓ t.abs else l - t
    }).getOrElse(l)
  }

  // pruning for instance checks
  def refineInstance(l: AbsType, name: String, pass: Boolean): AbsType = {
    val nameT = NameT(name)
    val astT = AstT(name)
    val isAst = cfg.spec.grammar.recSubs.keySet contains name
    val prevAstT = AbsType(cfg.spec.grammar.recSubs.getOrElse(name, Set()).map(AstT(_): Type))
    (pass, isAst) match {
      case (false, false) => l - nameT
      case (false, true) => (l - astT) ⊔ (prevAstT - astT)
      case (true, false) => l ⊓ nameT.abs
      case (true, true) => prevAstT ⊓ astT.abs
    }
  }
}
