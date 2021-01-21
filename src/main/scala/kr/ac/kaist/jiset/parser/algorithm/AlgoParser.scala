package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

object AlgoParser {
  // get algorithms
  def apply(
    elem: Element,
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region
  ): List[Algo] = {
    if (detail) println(s"--------------------------------------------------")
    val result = try {
      val heads = HeadParser(elem)
      if (detail) heads.foreach(println(_))
      val (start, end) = getRange(elem).get
      if (detail) println(s"Range: (${start + 1}, $end)")
      val code =
        if (elem.tagName == "ul") toArray(elem.children).map(li => "* " + li.text)
        else if (elem.tagName == "emu-table") {
          val rows = toArray(elem.select("tr")).filter(row => row.child(0).text != "Argument Type")
          rows.flatMap(row => {
            val typeText = row.child(0).text
            val doTexts = (getElems(row, "emu-alg").headOption match {
              case Some(emuAlg) => {
                val algs = getRawBody(emuAlg)
                val tabCount = getIndent(algs.head)
                algs.map(line => line.substring(tabCount))
              }
              case None => Array("* " + row.child(1).text)
            }).map("  " + _)
            List(s"* If Type(_argument_) is ${typeText},") ++ doTexts //todo! _argument_ should be handled generally
          })
        } else if (elem.tagName == "emu-eqn") {
          // trim until finding first '=' in each line
          getRawBody(elem).map("1. " + _.span(_ != '=')._2.tail.trim)
        } else getRawBody(elem)
      if (detail) {
        code.foreach(println _)
        println(s"====>")
      }
      var printBody = detail && true
      heads.map(h => {
        val body = getBody(h, code, start)
        if (printBody) {
          println(beautify(body))
          printBody = false
        }
        Algo(h, body)
      })
    } catch {
      case e: Throwable =>
        if (detail) {
          println(s"[Algo] ${e.getMessage}")
          e.getStackTrace.foreach(println _)
        }
        Nil
    }
    if (detail) println(s"--------------------------------------------------")
    result
  }

  // get body instructions
  def getBody(
    head: Head,
    code: Iterable[String],
    start: Int
  )(implicit grammar: Grammar): Inst = {
    // get tokens
    val tokens = TokenParser.getTokens(code)

    // get body
    val body = Compiler(tokens, start)

    // post process
    head match {
      case (head: MethodHead) if head.isLetThisStep(code.head.trim) =>
        popFront(body)
      case (builtin: BuiltinHead) =>
        val prefix = builtin.origParams.zipWithIndex.map {
          case (x, i) => Parser.parseInst(s"app ${x.name} = (GetArgument $ARGS_LIST ${i}i)")
        }
        prepend(prefix, body)
      case _ => body
    }
  }

  // prepend instructions
  def prepend(prefix: List[Inst], inst: Inst): Inst = prefix match {
    case Nil => inst
    case _ => inst match {
      case ISeq(list) => ISeq(prefix ++ list)
      case _ => ISeq(prefix :+ inst)
    }
  }

  // pop an instruction at the front
  def popFront(inst: Inst): Inst = inst match {
    case ISeq(hd :: tl) => ISeq(tl)
    case _ => ISeq(Nil)
  }
}
