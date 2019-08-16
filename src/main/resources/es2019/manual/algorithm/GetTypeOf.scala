package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object GetTypeOf {
  val func: Func = parseFunc(""""GetTypeOf" (argument) => {
    let t = (typeof argument)
    if (= t "Undefined") return "undefined"
    else if (= t "Null") return "object"
    else if (= t "Boolean") return "boolean"
    else if (= t "Number") return "number"
    else if (= t "String") return "string"
    else if (= t "Symbol") return "symbol"
    else {
      if (= argument.Call absent) return "object"
      else return "function"
    }
  }""")
}