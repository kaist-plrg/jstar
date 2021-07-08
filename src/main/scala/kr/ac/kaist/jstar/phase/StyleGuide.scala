package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.spec._

// StyleGuide phase
case object StyleGuide extends PhaseObj[ECMAScript, StyleGuideConfig, Unit] {
  val name: String = "style-guide"
  val help: String = "guide notation style of ECMAScript"

  def apply(
    spec: ECMAScript,
    jstarConfig: JSTARConfig,
    config: StyleGuideConfig
  ): Unit = ??? // TODO

  def defaultConfig: StyleGuideConfig = StyleGuideConfig()
  val options: List[PhaseOption[StyleGuideConfig]] = Nil
}

// StyleGuide phase config
case class StyleGuideConfig() extends Config
