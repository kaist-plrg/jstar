package kr.ac.kaist.jstar.checker

import kr.ac.kaist.jstar.spec._
import kr.ac.kaist.jstar.spec.algorithm.Algo
import kr.ac.kaist.jstar.ir

trait Bug {
  // bug name
  val name: String

  // bug message
  val msg: String

  // conversion to string
  override def toString: String = s"[$name] $msg"
}
