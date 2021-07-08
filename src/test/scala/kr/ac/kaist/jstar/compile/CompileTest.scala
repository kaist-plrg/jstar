package kr.ac.kaist.jstar.compile

import kr.ac.kaist.jstar.ir._
import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.parser.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jstar.spec.JsonProtocol._
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.spec.algorithm.Diff
import org.scalatest._

trait CompileTest extends JSTARTest {
  val category: String = "compile"

  def difftest(filename: String, result: IRNode, answer: IRNode, deep: Boolean = false): Unit = {
    val diff = new Diff
    diff.deep = deep
    diff(result, answer) match {
      case Some(diff.Missing(missing)) =>
        println(s"==================================================")
        println(s"[$filename] MISS: ${missing.beautified}")
        println(s"--------------------------------------------------")
        val answerStr = answer.beautified(index = true, asite = true)
        val resultStr = result.beautified(index = true, asite = true)
        println(s"- result: $resultStr")
        println(s"- answer: $answerStr")
        fail(s"$answerStr is different with $resultStr")
      case None =>
    }
  }
}
