package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammar productions
case class Production(
    lhs: Lhs,
    rhsList: List[Rhs]
) {
  def getIdxMap: Map[String, (Int, Int)] = (for {
    (rhs, i) <- rhsList.zipWithIndex
    names = rhs.tokens.foldLeft(List[String](lhs.name + ":")) {
      case (names, Terminal(term)) => names.map(_ + term)
      case (names, NonTerminal(name, _, optional)) => names.flatMap(x => {
        if (optional) List(x, x + name) else List(x + name)
      })
      case (names, ButNot(NonTerminal(base, _, _), cases)) =>
        val butnot = cases.flatMap(_ match {
          case NonTerminal(name, _, _) => Some(name)
          case _ => None
        }).mkString
        names.map(_ + s"${base}butnot$butnot")
      case (names, _) => names
    }
    (name, j) <- names.zipWithIndex
  } yield norm(name) -> (i, j)).toMap

  // conversion to string
  override def toString: String =
    (lhs.toString :: rhsList.map("  " + _.toString)).mkString(LINE_SEP)
}
object Production extends ProductionParsers {
  def apply(lines: List[String]): Production = {
    val prod = parse(lines.map(revertSpecialCodes))
    println("--------------------------------------------------")
    lines.foreach(println _)
    println("--------------------------------------------------")
    println(prod)
    prod
  }
}
