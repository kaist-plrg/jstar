package kr.ac.kaist.jstar.grammar

import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.extractor.ECMAScriptParser
import kr.ac.kaist.jstar.spec._
import kr.ac.kaist.jstar.util.Useful._
import org.scalatest._

class BasicSmallTest extends GrammarTest {
  val name: String = "grammarBasicTest"

  // registration
  def init: Unit = {
    for (version <- VERSIONS) check(version, {
      val filename = s"$GRAMMAR_DIR/$version.grammar"
      val answer = readFile(filename)
      val spec = getSpec(version)
      val grammar = spec.grammar
      assert(answer == grammar.toString)
    })
  }
  init
}
