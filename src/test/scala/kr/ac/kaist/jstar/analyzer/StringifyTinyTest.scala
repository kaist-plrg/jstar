package kr.ac.kaist.jstar.analyzer

import kr.ac.kaist.jstar.ANALYZER_DIR
import kr.ac.kaist.jstar.JSTARTest
import kr.ac.kaist.jstar.util.Useful._

class StringifyTinyTest extends AnalyzerTest {
  val name: String = "analyzerStringifyTest"

  // registration
  def init: Unit = check("stringify", {
    // val sem = JSTARTest.analysisResult
    // val result = sem.toString
    // val answer = readFile(s"$ANALYZER_DIR/stringify")
    // assert(result == answer)
  })
  init
}
