package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object ToNumber {
  val func: Func = Func("ToNumber", List(Id("argument")), None, parseInst(
    s"""{
      let atype = (typeof argument)
      if (= atype "Undefined") {
        return NaN
      } else if (= atype "Null") {
        return 0
      } else if (= atype "Boolean") {
        return 0
      } else if (= atype "Number") {
        return argument
      } else if (= atype "String") {
        return arguemnt.getNumber
      } else if (= atype "Symbol") {
        return NaN
      } else if (= atype "Object") {
        return NaN
      } else {
        return NaN
      }
    }"""
  ))
}