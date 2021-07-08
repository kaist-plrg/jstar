package kr.ac.kaist.jstar.ir

import kr.ac.kaist.jstar.ir.Parser._
import kr.ac.kaist.jstar.util.BasicJsonProtocol
import kr.ac.kaist.jstar.util.Useful.beautify
import spray.json._

object JsonProtocol extends BasicJsonProtocol {
  val beautifier = new Beautifier(index = true, asite = true)
  import beautifier._

  implicit lazy val TyFormat = stringFormat[Ty](parseTy, beautify)
  implicit lazy val RefFormat = stringFormat[Ref](parseRef, beautify)
  implicit lazy val ExprFormat = stringFormat[Expr](parseExpr, beautify)
  implicit lazy val InstFormat = stringFormat[Inst](parseInst, beautify)
}
