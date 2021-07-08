package kr.ac.kaist.jstar.ir

// IR Unary Operators
sealed trait UOp extends IRNode
case object ONeg extends UOp
case object ONot extends UOp
case object OBNot extends UOp
