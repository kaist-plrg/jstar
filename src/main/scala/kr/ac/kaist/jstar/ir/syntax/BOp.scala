package kr.ac.kaist.jstar.ir

// IR Binary Operators
sealed trait BOp extends IRNode
case object OPlus extends BOp
case object OSub extends BOp
case object OMul extends BOp
case object OPow extends BOp
case object ODiv extends BOp
case object OUMod extends BOp
case object OMod extends BOp
case object OLt extends BOp
case object OEq extends BOp
case object OEqual extends BOp
case object OAnd extends BOp
case object OOr extends BOp
case object OXor extends BOp
case object OBAnd extends BOp
case object OBOr extends BOp
case object OBXOr extends BOp
case object OLShift extends BOp
case object OSRShift extends BOp
case object OURShift extends BOp
