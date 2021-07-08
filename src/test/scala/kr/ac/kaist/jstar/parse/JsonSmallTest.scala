package kr.ac.kaist.jstar.parse

import kr.ac.kaist.jstar.JSTARTest
import kr.ac.kaist.jstar.spec.ECMAScript
import kr.ac.kaist.jstar.parser.ECMAScriptParser
import kr.ac.kaist.jstar.spec.JsonProtocol._
import spray.json._
import org.scalatest._
import kr.ac.kaist.jstar.spec.algorithm.Diff

class JsonSmallTest extends ParseTest {
  val name: String = "parseJsonTest"

  // registration
  def init: Unit = {
    check("ECMAScript (recent)", {
      val spec = getSpec("recent")
      val json = spec.toJson
      val loaded = json.convertTo[ECMAScript]
      val diff = new Diff
      diff.deep = true
      assert(spec == loaded)
      (spec.algos zip loaded.algos).foreach {
        case (l, r) => {
          assert(diff.compare(l.rawBody, r.rawBody))
        }
      }
    })
  }
  init
}
