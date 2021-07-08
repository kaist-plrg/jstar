package kr.ac.kaist.jstar.ir

// IR Types
case class Ty(name: String) extends IRNode {
  override def toString: String = s"Ty($TRIPLE$name$TRIPLE)"
}
