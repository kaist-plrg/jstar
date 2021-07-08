package kr.ac.kaist.jstar.analyzer

case class Result[+T](elem: T, st: AbsState) {
  def toPair: (T, AbsState) = (elem, st)
}
