package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.cfg._
import kr.ac.kaist.jstar.JSTARConfig
import kr.ac.kaist.jstar.spec.ECMAScript
import kr.ac.kaist.jstar.util._
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.analyzer.Stat

// BuildCFG phase
case object BuildCFG extends PhaseObj[ECMAScript, BuildCFGConfig, CFG] {
  val name = "build-cfg"
  val help = "builds control flow graph (CFG)."

  def apply(
    spec: ECMAScript,
    jstarConfig: JSTARConfig,
    config: BuildCFGConfig
  ): CFG = {
    val (cfgTime, cfg) = time("build CFG", new CFG(spec))
    Stat.cfgTime = cfgTime

    if (config.dot) {
      mkdir(CFG_DIR)
      val format = if (config.pdf) "DOT/PDF" else "DOT"
      ProgressBar(s"dump CFG in a $format format", cfg.funcs).foreach(f => {
        val name = s"${CFG_DIR}/${f.name}"
        dumpFile(f.toDot, s"$name.dot")
        if (config.pdf) {
          // check whether dot is available
          if (isNormalExit("dot -V")) {
            try executeCmd(s"dot -Tpdf $name.dot -o $name.pdf") catch {
              case ex: Exception => println(s"[ERROR] $name: exception occur while converting to pdf")
            }
          } else println("Dot is not installed!")
        }
      })
    }

    cfg
  }

  def defaultConfig: BuildCFGConfig = BuildCFGConfig()
  val options: List[PhaseOption[BuildCFGConfig]] = List(
    ("dot", BoolOption(c => c.dot = true),
      "dump the cfg in a dot format"),
    ("pdf", BoolOption(c => { c.dot = true; c.pdf = true }),
      "dump the cfg in a dot and pdf format")
  )
}

// BuildCFG config
case class BuildCFGConfig(
  var dot: Boolean = false,
  var pdf: Boolean = false
) extends Config
