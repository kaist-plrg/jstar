package kr.ac.kaist.jstar.spec.grammar

import kr.ac.kaist.jstar.LINE_SEP
import kr.ac.kaist.jstar.util.Useful._

// ECMAScript grammar productions
case class Production(
  lhs: Lhs,
  rhsList: List[Rhs]
) {
  // get name
  def name: String = lhs.name

  // get nonterminal names in rhs of lhs
  def getRhsNT: Set[String] = rhsList.flatMap(_.toNTs).map(_.name).toSet

  // get index map
  def getIdxMap: Map[String, (Int, Int)] = (for {
    (rhs, i) <- rhsList.zipWithIndex
    (name, j) <- rhs.allNames.zipWithIndex
  } yield lhs.name + ":" + name -> (i, j)).toMap

  // conversion to string
  override def toString: String =
    (lhs.toString :: rhsList.map("  " + _.toString)).mkString(LINE_SEP)
}
