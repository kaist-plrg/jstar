package kr.ac.kaist.jiset.compile

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.parser.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import org.scalatest._
import kr.ac.kaist.jiset.spec.algorithm.Token
import kr.ac.kaist.jiset.parser.ECMAScriptParser

class BasicMiddleTest extends CompileTest {
  // helper
  val spec2json = changeExt("spec", "json")
  val spec2ir = changeExt("spec", "ir")

  // registration
  def init: Unit = {
    for (version <- VERSIONS) {
      val baseDir = s"$BASIC_COMPILE_DIR/$version"

      // get grammar and document
      implicit val (lines, document, region) = getInput(version)
      implicit val grammar = ECMAScriptParser.parseGrammar
      val secIds = ECMAScriptParser.parseHeads()._1

      for (file <- walkTree(baseDir)) {
        val filename = file.getName
        if (specFilter(filename)) {
          val name = s"${removedExt(filename)} @ $version"
          check(name, {
            val specName = file.toString
            val (jsonName, irName) = (spec2json(specName), spec2ir(specName))

            // check token parsing
            val tokens = readJson[List[Token]](jsonName)

            val code = readFile(specName).split(LINE_SEP)
            val resultTokens = TokenParser.getTokens(code, secIds)
            assert(tokens == resultTokens, "tokens are changed")

            // check compile
            val answer = Parser.fileToInst(irName)
            val result = Compiler(tokens)
            difftest(name, result, answer)
          })
        }
      }
    }
  }
  init
}
