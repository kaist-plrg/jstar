package kr.ac.kaist.jstar.extractor.grammar

import kr.ac.kaist.jstar.spec.grammar._

// Rhs parsers
object RhsParser extends RhsParsers {
  def apply(str: String): Rhs = parseAll(rhs, str).get
}
trait RhsParsers extends TokenParsers {
  lazy val rhs: Parser[Rhs] = opt(cond) ~ rep1(token) <~ opt(tag) ^^ {
    case cond ~ tokens => Rhs(tokens, cond)
  }
  lazy val cond = "[" ~> "[+~]".r ~ word <~ "]" ^^ {
    case s ~ c => RhsCond(c, s == "+")
  }
}
