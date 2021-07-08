package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar.JSTARConfig
import kr.ac.kaist.jstar.util.ArgParser

abstract class Phase {
  val name: String
  val help: String
  def getOptShapes: List[String]
  def getOptDescs: List[(String, String)]
}
abstract class PhaseObj[Input, PhaseConfig <: Config, Output] extends Phase {
  val name: String
  val help: String
  def apply(
    in: Input,
    jstarConfig: JSTARConfig,
    config: PhaseConfig = defaultConfig
  ): Output
  def defaultConfig: PhaseConfig
  val options: List[PhaseOption[PhaseConfig]]

  def getRunner(
    parser: ArgParser
  ): (Input, JSTARConfig) => Output = {
    val config = defaultConfig
    parser.addRule(config, name, options)
    (in, jstarConfig) => {
      println(s"========================================")
      println(s" $name phase")
      println(s"----------------------------------------")
      apply(in, jstarConfig, config)
    }
  }

  def getOptShapes: List[String] = options.map {
    case (opt, kind, _) => s"-$name:${opt}${kind.postfix}"
  }
  def getOptDescs: List[(String, String)] = options.map {
    case (opt, kind, desc) => (s"-$name:${opt}${kind.postfix}", desc)
  }
}

trait Config
