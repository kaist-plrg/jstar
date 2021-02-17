package kr.ac.kaist.jiset.analyzer.domain.inum

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object FlatDomain extends generator.FlatDomain[INum] with inum.Domain {
  // numerical operators
  val neg = alpha(-_)
  val add = alpha(_ + _)
  val sub = alpha(_ - _)
  val mul = alpha(_ * _)
  val div = alpha(_ / _)
  val pow = alpha(lpow(_, _))
  val mod = alpha(_ % _)
  val umod = alpha(unsigned_modulo(_, _))
  val lt = alpha(this, this, AbsBool)(_ < _)

  // bit-wise operators
  val not = alpha(~_)
  val and = alpha(_ & _)
  val or = alpha(_ | _)
  val xor = alpha(_ ^ _)

  // shift operators
  val leftShift = alpha(_ << _)
  val rightShift = alpha(_ >> _)
  val unsignedRightShift = alpha((l, r) => (l & 0xffffffffL) >>> r.toInt)
}