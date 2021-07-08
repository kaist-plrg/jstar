package kr.ac.kaist.jstar.parser.algorithm

import kr.ac.kaist.jstar.ir._
import kr.ac.kaist.jstar.LINE_SEP
import kr.ac.kaist.jstar.spec.{ ECMAScript, Region }
import kr.ac.kaist.jstar.spec.algorithm._
import kr.ac.kaist.jstar.spec.grammar.{ Grammar, NonTerminal, Terminal }
import kr.ac.kaist.jstar.util.Useful._
import org.jsoup.nodes._

object AlgoParser {
  // get algorithms
  def apply(
    version: String,
    parsedHead: (Element, List[Head]),
    secIds: Map[String, Name],
    detail: Boolean
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region,
    document: Document
  ): List[Algo] = {
    val (elem, heads) = parsedHead
    val ids: List[String] = getIds(elem)
    val result: List[Algo] = try {
      val (start, end) = getRange(elem).get
      // get code
      var code = getRawBody(elem)

      // old bitwise cases
      if (("Bitwise.*Expression.*Evaluation.*".r matches heads.head.name) &&
        (code.mkString contains "_A_")) heads.map {
        case (head: SyntaxDirectedHead) => head.rhs.tokens match {
          case List(l: NonTerminal, op: Terminal, r: NonTerminal) =>
            val newCode = code.map(_
              .replaceAll("_A_", s"_${l.name}_")
              .replaceAll("_B_", s"_${r.name}_")
              .replaceAll("@", s"$op"))
            val rawBody = getBody(version, newCode, secIds, start)
            Algo(head, ids, rawBody, code)
          case _ => error("impossible")
        }
      }
      else {
        val rawBody = getBody(version, code, secIds, start)
        heads.map(Algo(_, ids, rawBody, code))
      }
    } catch {
      case e: Throwable =>
        Nil
    }
    result.foreach(algo => (new LocWalker).walk(algo.rawBody))
    result
  }

  // get algorithms from codes
  def getBody(
    version: String,
    code: Array[String],
    secIds: Map[String, Name],
    start: Int
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region,
    document: Document
  ): Inst = {
    // get tokens
    val tokens = TokenParser.getTokens(code, secIds)

    // get body
    val rawBody = Compiler(version)(tokens, start)

    rawBody
  }

  // get ancestor ids
  def getIds(elem: Element): List[String] = {
    val ids =
      if (elem.parent == null) Nil
      else getIds(elem.parent)
    if (elem.id == "") ids
    else elem.id :: ids
  }
}
