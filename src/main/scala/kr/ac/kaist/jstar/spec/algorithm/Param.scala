package kr.ac.kaist.jstar.spec.algorithm

import kr.ac.kaist.jstar.ir._
import kr.ac.kaist.jstar.util.{ InfNum, PInf }

case class Param(name: String, kind: Param.Kind = Param.Kind.Normal) {
  import Param.Kind._

  def toOptional: Param = Param(name, Optional)

  // count arity
  lazy val count: (InfNum, InfNum) = kind match {
    case Normal => (1, 1)
    case Optional => (0, 1)
    case Variadic => (0, PInf)
  }

  // conversion to string
  override def toString: String = {
    kind match {
      case Normal => name
      case Optional => name + "?"
      case Variadic => "..." + name
    }
  }
}
object Param {
  type Kind = Kind.Value
  object Kind extends Enumeration {
    val Normal, Optional, Variadic = Value
  }
}
