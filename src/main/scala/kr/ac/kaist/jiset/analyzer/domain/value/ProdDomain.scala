package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object ProdDomain extends value.Domain {
  // abstraction functions
  def alpha(v: Value): Elem = v match {
    case addr: Addr => Elem(_addr = AbsAddr(addr))
    case clo: Clo => Elem(_clo = AbsClo.Top)
    case cont: Cont => Elem(_cont = AbsCont.Top)
    case ast: ASTVal => Elem(_ast = AbsAST.Top)
    case prim: Prim => Elem(_prim = AbsPrim(prim))
  }

  // bottom value
  val Bot: Elem = Elem()

  // top value
  val Top: Elem = Elem(
    AbsAddr.Top,
    AbsClo.Top,
    AbsCont.Top,
    AbsAST.Top,
    AbsPrim.Top
  )

  // constructor
  def apply(
    addr: AbsAddr = AbsAddr.Bot,
    clo: AbsClo = AbsClo.Bot,
    cont: AbsCont = AbsCont.Bot,
    ast: AbsAST = AbsAST.Bot,
    prim: AbsPrim = AbsPrim.Bot
  ): Elem = Elem(addr, clo, cont, ast, prim)

  case class Elem(
    _addr: AbsAddr = AbsAddr.Bot,
    _clo: AbsClo = AbsClo.Bot,
    _cont: AbsCont = AbsCont.Bot,
    _ast: AbsAST = AbsAST.Bot,
    _prim: AbsPrim = AbsPrim.Bot
  ) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (
      this._addr ⊑ that._addr &&
      this._clo ⊑ that._clo &&
      this._cont ⊑ that._cont &&
      this._ast ⊑ that._ast &&
      this._prim ⊑ that._prim
    )

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this._addr ⊔ that._addr,
      this._clo ⊔ that._clo,
      this._cont ⊔ that._cont,
      this._ast ⊔ that._ast,
      this._prim ⊔ that._prim
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this._addr ⊓ that._addr,
      this._clo ⊓ that._clo,
      this._cont ⊓ that._cont,
      this._ast ⊓ that._ast,
      this._prim ⊓ that._prim
    )

    // concretization clotion
    def gamma: concrete.Set[Value] = (
      this._addr.gamma ++
      this._clo.gamma ++
      this._cont.gamma ++
      this._ast.gamma ++
      this._prim.gamma
    )

    // conversion to flat domain
    def getSingle: concrete.Flat[Value] = (
      this._addr.getSingle ++
      this._clo.getSingle ++
      this._cont.getSingle ++
      this._ast.getSingle ++
      this._prim.getSingle
    )
  }

  // Members declared in prim.Domain
  def addr(elem: Elem): AbsAddr = elem._addr
  def clo(elem: Elem): AbsClo = elem._clo
  def cont(elem: Elem): AbsCont = elem._cont
  def ast(elem: Elem): AbsAST = elem._ast
  def prim(elem: Elem): AbsPrim = elem._prim
}
