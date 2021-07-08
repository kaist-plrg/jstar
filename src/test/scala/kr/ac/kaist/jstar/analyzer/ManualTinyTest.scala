package kr.ac.kaist.jstar.analyzer

import kr.ac.kaist.jstar.JSTARTest
import kr.ac.kaist.jstar.spec.ECMAScript
import kr.ac.kaist.jstar.parser.ECMAScriptParser
import kr.ac.kaist.jstar.cfg.{ CFG, Function }
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.ir._
import org.scalatest._

class ManualTinyTest extends AnalyzerTest {
  val name: String = "analyzerManualTest"

  // val answerMap: Map[(String, View), (AbsHeap, AbsValue)] = Map(
  //   ("PrimaryExpression[0,0].IsIdentifierRef", View(List(AstT("PrimaryExpression"))))
  //     -> ((AbsHeap.Bot, AbsComp(CompNormal -> (AF: AbsPure, emptyConst))))
  // )

  // def getFunctionByName(cfg: CFG, fname: String): Option[Function] =
  //   cfg.algo2fid.get(fname).flatMap(uid => cfg.fidMap.get(uid))

  // def getString(h: AbsHeap, v: AbsValue): String = beautify(v) + (
  //   if (h.isBottom) ""
  //   else s" @ ${beautify(h)}"
  // )

  // registration
  def init: Unit = {
    // // analyze results in sem: AbsSemantics
    // val spec = getSpec("recent")
    // val cfg = new CFG(spec)
    // val sem = new AbsSemantics(cfg)
    // val transfer = new AbsTransfer(sem, false)
    // transfer.compute

    // // find testcases
    // for (((fname, view), (ansH, ansV)) <- answerMap) check(fname, {
    //   val func = getFunctionByName(cfg, fname).getOrElse(error("Answer Function not found"))
    //   val rp = ReturnPoint(func, view)
    //   val (resH, resV) = sem(rp)
    //   val comparison = resH === ansH && resV === ansV
    //   if (!comparison) {
    //     println(s"FAILED: Function $fname, View $view")
    //     println(s"expected: ${getString(ansH, ansV)}")
    //     println(s"result: ${getString(resH, resV)}")
    //     assert(comparison)
    //   }
    // })
  }
  init
}
