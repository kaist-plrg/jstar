package kr.ac.kaist.jstar.spec.algorithm

import kr.ac.kaist.jstar.ir
import kr.ac.kaist.jstar.ir.Beautifier._

// built-in algorithm heads
case class BuiltinHead(
  ref: ir.Ref,
  origParams: List[Param]
) extends Head {
  // name from base and fields
  val name: String = ref.beautified

  // fixed parameters for built-in algorithms
  val params: List[Param] =
    List(THIS_PARAM, ARGS_LIST, NEW_TARGET).map(Param(_))
}
