package kr.ac.kaist.jstar.compile

import java.io._
import kr.ac.kaist.jstar.ir._
import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.extractor.algorithm.Compiler
import kr.ac.kaist.jstar.spec.algorithm._
import kr.ac.kaist.jstar.spec.JsonProtocol._
import kr.ac.kaist.jstar.util.Useful._
import org.scalatest._

class LegacySmallTest extends CompileTest {
  val name: String = "compileLegacyTest"

  // helper
  val json2ir = changeExt("json", "ir")

  // registration
  def init: Unit = check("legacy", {
    for (file <- walkTree(LEGACY_COMPILE_DIR)) {
      val filename = file.getName
      if (jsonFilter(filename)) {
        lazy val jsonName = file.toString
        lazy val irName = json2ir(jsonName)

        lazy val tokens = readJson[List[Token]](jsonName)
        lazy val answer = Parser.parseInst(readFile(irName))
        lazy val result = Compiler(LEGACY_COMPILER_VERSION)(tokens)

        difftest(filename, result, answer)
      }
    }
  })
  init
}
