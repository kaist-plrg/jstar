package kr.ac.kaist.jstar

import kr.ac.kaist.jstar.error.NoMode
import kr.ac.kaist.jstar.ir
import kr.ac.kaist.jstar.phase._
import kr.ac.kaist.jstar.analyzer._
import kr.ac.kaist.jstar.spec._
import kr.ac.kaist.jstar.util.ArgParser
import org.jsoup.nodes.Element
import scala.Console._

sealed abstract class Command[Result](
  val name: String,
  val pList: PhaseList[Result]
) {
  def help: String
  def apply(args: List[String]): Result = {
    val jstarConfig = JSTARConfig(this)
    val parser = new ArgParser(this, jstarConfig)
    val runner = pList.getRunner(parser)
    parser(args)
    JSTAR(this, runner(_), jstarConfig)
  }

  def display(res: Result): Unit = ()

  override def toString: String = pList.toString

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = pList >> phase
}

// base command
case object CmdBase extends Command("", PhaseNil) {
  def help: String = "does nothing."
}

// help
case object CmdHelp extends Command("help", CmdBase >> Help) {
  def help: String = "shows help messages."
}

// extract
case object CmdExtract extends Command("extract", CmdBase >> Extract) {
  def help = "extracts ECMAScript model from ecma262/spec.html."
  override def display(spec: ECMAScript): Unit = {
    val ECMAScript(grammar, algos, intrinsics, symbols, aoids, section) = spec
    println(s"* grammar:")
    println(s"  - lexical production: ${grammar.lexProds.length}")
    println(s"  - non-lexical production: ${grammar.prods.length}")
    println(s"* algorithms:")
    println(s"  - incomplete: ${spec.incompletedAlgos.length}")
    println(s"  - complete: ${spec.completedAlgos.length}")
    println(s"  - total: ${algos.length}")
    println(s"* intrinsics: ${intrinsics.size}")
    println(s"* symbols: ${symbols.size}")
    println(s"* aoids: ${aoids.size}")
    println(s"* incompleted steps: ${spec.incompletedAlgos.map(_.todos.length).sum}")
  }
}

// build-cfg
case object CmdBuildCFG extends Command("build-cfg", CmdExtract >> BuildCFG) {
  def help = "builds control flow graph (CFG)."
}

// analyze
case object CmdAnalyze extends Command("analyze", CmdBuildCFG >> Analyze) {
  def help = "performs type analysis for the given ECMAScript."
  override def display(unit: Unit): Unit = {
    println(AbsSemantics.getString(CYAN))
    println(AbsSemantics.getInfo)
  }
}
