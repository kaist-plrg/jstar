package kr.ac.kaist.jstar.checker

import kr.ac.kaist.jstar.spec.ECMAScript
import kr.ac.kaist.jstar.spec.algorithm.Algo

trait Checker {
  type Result <: Bug
  def apply(spec: ECMAScript, targets: List[Algo]): List[Result]
}
