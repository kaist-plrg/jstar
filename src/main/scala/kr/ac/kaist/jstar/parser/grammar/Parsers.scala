package kr.ac.kaist.jstar.parser.grammar

import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
  lazy val params: Parser[List[String]] = "[" ~> repsep(pWord, ",") <~ "]"
  lazy val pWord = "[?|\\+|~]*\\w+".r
}
