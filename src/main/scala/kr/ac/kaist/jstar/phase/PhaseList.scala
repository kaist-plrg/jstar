package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar.JSTARConfig
import kr.ac.kaist.jstar.util.ArgParser

sealed abstract class PhaseList[Result] {
  def getRunner(
    parser: ArgParser
  ): JSTARConfig => Result

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = PhaseCons(this, phase)

  val nameList: List[String]
  override def toString: String = nameList.reverse.mkString(" >> ")
}

case object PhaseNil extends PhaseList[Unit] {
  def getRunner(
    parser: ArgParser
  ): JSTARConfig => Unit = x => {}

  val nameList: List[String] = Nil
}

case class PhaseCons[P, C <: Config, R](
  prev: PhaseList[P],
  phase: PhaseObj[P, C, R]
) extends PhaseList[R] {
  def getRunner(
    parser: ArgParser
  ): JSTARConfig => R = {
    val prevRunner = prev.getRunner(parser)
    val phaseRunner = phase.getRunner(parser)
    jstarConfig => phaseRunner(prevRunner(jstarConfig), jstarConfig)
  }

  val nameList: List[String] = phase.name :: prev.nameList
}

