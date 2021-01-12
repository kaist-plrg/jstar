package kr.ac.kaist.jiset.spec

import kr.ac.kaist.ires.ir
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup._
import org.jsoup.nodes._

// ECMASCript specifications
case class ECMAScript(algos: List[Algo])

object ECMAScript {
  def isComplete(inst: ir.Inst): Boolean = {
    var complete = true
    object Walker extends ir.UnitWalker {
      override def walk(expr: ir.Expr): Unit = expr match {
        case ir.ENotYetModeled(_) | ir.ENotSupported(_) => complete = false
        case _ => super.walk(expr)
      }
    }
    Walker.walk(inst)
    complete
  }

  def apply(filename: String): ECMAScript = {
    val src = readFile(filename)

    // source lines
    val lines = src.split(LINE_SEP)

    // HTML elements with `emu-alg` tags
    val document = Jsoup.parse(src)
    val elems = document.getElementsByTag("emu-alg").toArray(Array[Element]())

    // codes for `emu-alg` tagged elements
    val rngs = getRanges(lines)
    val codes = rngs.map { case (s, e) => lines.slice(s, e).toList }

    println(s"# total: ${codes.size}")

    // algorithms
    val (atime, algos) = time((for {
      (elem, code) <- elems zip codes
      algo <- Algo(elem, code)
      if (isComplete(algo.body))
    } yield algo).toList)
    println(s"# algos: ${algos.length} ($atime ms)")

    ECMAScript(algos)
  }

  // get ranges of each `emu-alg` tagged elements
  val entryPattern = "[ ]*<emu-alg.*>".r
  val exitPattern = "[ ]*</emu-alg.*>".r
  def getRanges(lines: Array[String]): Array[(Int, Int)] = {
    var rngs = Vector[(Int, Int)]()
    var entries = List[Int]()
    for ((line, i) <- lines.zipWithIndex) line match {
      case entryPattern() => entries ::= i + 1
      case exitPattern() =>
        rngs :+= (entries.head, i)
        entries = entries.tail
      case _ =>
    }
    rngs.toArray
  }
}