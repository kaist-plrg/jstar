package kr.ac.kaist.jstar.checker

import kr.ac.kaist.jstar.spec._
import kr.ac.kaist.jstar.spec.algorithm.Algo
import kr.ac.kaist.jstar.util.MayMust
import kr.ac.kaist.jstar.ir._

object MissingRetChecker extends Checker {
  // for specifications
  def apply(
    spec: ECMAScript,
    targets: List[Algo]
  ): List[Result] = targets.flatMap(apply(_))

  // for algorithms
  def apply(algo: Algo): List[Result] = {
    def walkBranch(inst: Inst): Boolean = inst match {
      case ISeq(is) =>
        if (!is.isEmpty) walkBranch(is.last)
        else false
      case IIf(c, ti, ei) =>
        val exhaustiveThen = walkBranch(ti)
        val exhaustiveElse = walkBranch(ei)
        exhaustiveThen && exhaustiveElse
      case IWhile(c, b) => false
      case IReturn(_) | IThrow(_) => true
      case _ => false
    }
    val res = walkBranch(algo.getBody)
    if (!res) List(Result(algo))
    else List[Result]()
  }

  // results
  case class Result(algo: Algo) extends Bug {
    val name: String = "MissingReturn"
    val msg: String = s"${algo.name}"
  }
}
