package kr.ac.kaist.jstar.extractor.algorithm

import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
}
