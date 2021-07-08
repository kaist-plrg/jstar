package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar.{ JSTAR, JSTARConfig }

// Help phase
case object Help extends PhaseObj[Unit, HelpConfig, Unit] {
  val name = "help"
  val help: String = "shows help messages."

  def apply(
    unit: Unit,
    jstarConfig: JSTARConfig,
    config: HelpConfig
  ): Unit = println(JSTAR.help)
  def defaultConfig: HelpConfig = HelpConfig()
  val options: List[PhaseOption[HelpConfig]] = Nil
}

case class HelpConfig() extends Config
