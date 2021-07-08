package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.parser.ECMAScriptParser
import kr.ac.kaist.jstar.parser.algorithm.{ CompileREPL => REPL }
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.util._

// CompileREPL phase
case object CompileREPL extends PhaseObj[Unit, CompileREPLConfig, Unit] {
  val name = "compile-repl"
  val help = "REPL for printing compile result of particular step"

  def apply(
    unit: Unit,
    jstarConfig: JSTARConfig,
    config: CompileREPLConfig
  ): Unit = {
    val CompileREPLConfig(versionOpt, detail) = config
    val version = versionOpt.getOrElse("recent")
    println(s"version: $version (${getRawVersion(version)})")

    implicit val (_, (lines, document, region)) =
      time("preprocess", ECMAScriptParser.preprocess(version))
    implicit val (_, (grammar, _)) =
      time("parse ECMAScript grammar", ECMAScriptParser.parseGrammar(version))
    val (_, (secIds, _)) =
      time("parse algorithm heads", ECMAScriptParser.parseHeads())

    REPL.run(version, secIds)
  }

  def defaultConfig: CompileREPLConfig = CompileREPLConfig()
  val options: List[PhaseOption[CompileREPLConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("detail", BoolOption(c => c.detail = true),
      "print log.")
  )
}

// CompileREPL phase config
case class CompileREPLConfig(
  var version: Option[String] = None,
  var detail: Boolean = false
) extends Config
