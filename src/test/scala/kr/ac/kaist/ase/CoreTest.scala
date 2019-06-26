package kr.ac.kaist.ase

import java.io._
import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.phase._
import org.scalatest._
import scala.util.Random.shuffle

abstract class CoreTest extends ASETest {
  // tests for core-parser
  def parseCoreTest(pgm: => Program): Unit = {
    val newPgm = Parser.parseProgram(beautify(pgm))
    assert(pgm == newPgm)
  }
  def parseCoreFuncTest(func: => Func): Unit = {
    val newFunc = Parser.parseFunc(beautify(func))
    assert(func == newFunc)
  }

  // tests for core-interpreter
  def evalCoreTest(st: => State): Unit = st
}
