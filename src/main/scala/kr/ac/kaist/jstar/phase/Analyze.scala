package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar.JSTARConfig
import kr.ac.kaist.jstar.cfg._
import kr.ac.kaist.jstar.analyzer._
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.util._
import kr.ac.kaist.jstar._
import scala.Console._

// Analyze phase
case object Analyze extends PhaseObj[CFG, AnalyzeConfig, Unit] {
  val name = "analyze"
  val help = "performs static anaysis for specifications."

  def apply(
    cfg: CFG,
    jstarConfig: JSTARConfig,
    config: AnalyzeConfig
  ): Unit = {
    init(cfg)
    Stat.analysisStartTime = System.currentTimeMillis
    AbsTransfer.compute
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("dot", BoolOption(c => DOT = true),
      "dump the analyzed cfg in a dot format"),
    ("pdf", BoolOption(c => { DOT = true; PDF = true }),
      "dump the analyze cfg in a dot and pdf format"),
    ("no-refine", BoolOption(c => REFINE = false),
      "no abstract state refinement"),
    ("insens", BoolOption(c => USE_VIEW = false),
      "not use type sensitivity for parameters"),
    ("check-alarm", BoolOption(c => CHECK_ALARM = true),
      "check alarms"),
    ("target", StrOption((c, s) => TARGET = Some(s)),
      "set the target of analysis"),
    ("repl", BoolOption(c => REPL = true),
      "use analyze-repl"),
  )
}

// Analyze phase config
case class AnalyzeConfig() extends Config
