package kr.ac.kaist.jstar

import kr.ac.kaist.jstar.error._
import kr.ac.kaist.jstar.util._
import kr.ac.kaist.jstar.phase._

object JSTAR {
  ////////////////////////////////////////////////////////////////////////////////
  // Main entry point
  ////////////////////////////////////////////////////////////////////////////////
  def main(tokens: Array[String]): Unit = try tokens.toList match {
    case str :: args => cmdMap.get(str) match {
      case Some(CmdHelp) => println(JSTAR.help)
      case Some(cmd) => cmd(args)
      case None => throw NoCmdError(str)
    }
    case Nil => throw NoInputError
  } catch {
    // JSTARError: print the error message.
    case ex: JSTARError =>
      Console.err.println(ex.getMessage)
    // Unexpected: print the stack trace.
    case ex: Throwable =>
      Console.err.println("* Unexpected error occurred.")
      Console.err.println(ex.toString)
      Console.err.println(ex.getStackTrace.mkString(LINE_SEP))
  }

  def apply[Result](
    command: Command[Result],
    runner: JSTARConfig => Result,
    config: JSTARConfig
  ): Result = {
    // set the start time.
    val startTime = System.currentTimeMillis

    // execute the command.
    val result: Result = runner(config)

    // duration
    val duration = System.currentTimeMillis - startTime

    // display the result.
    if (!config.silent) {
      command.display(result)
    }

    // display the time.
    if (config.time) {
      val name = config.command.name
      println(s"The command '$name' took $duration ms.")
    }

    // return result
    result
  }

  // commands
  val commands: List[Command[_]] = List(
    CmdHelp,
    CmdExtract,
    CmdBuildCFG,
    CmdAnalyze,
  )
  val cmdMap = commands.foldLeft[Map[String, Command[_]]](Map()) {
    case (map, cmd) => map + (cmd.name -> cmd)
  }

  // phases
  var phases: List[Phase] = List(
    Help,
    Extract,
    BuildCFG,
    Analyze,
  )

  // global options
  val options: List[PhaseOption[JSTARConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "do not show final results."),
    ("debug", BoolOption(c => DEBUG = true),
      "turn on the debug mode."),
    ("log", BoolOption(c => LOG = true),
      "turn on the logging mode."),
    ("time", BoolOption(c => c.time = true),
      "display the duration time.")
  )

  // indentation
  private val INDENT = 20

  // print help message.
  val help: String = {
    val s: StringBuilder = new StringBuilder
    s.append("* command list:").append(LINE_SEP)
    s.append("    Each command consists of following phases.").append(LINE_SEP)
    s.append("    format: {command} {phase} [>> {phase}]*").append(LINE_SEP).append(LINE_SEP)
    commands foreach (cmd => {
      s.append(s"    %-${INDENT}s".format(cmd.name))
        .append(cmd.help)
        .append(LINE_SEP)
      s.append("    " + " " * INDENT)
        .append(s"(${cmd.pList.toString})")
        .append(LINE_SEP)
    })
    s.append(LINE_SEP)
    s.append("* phase list:").append(LINE_SEP)
    s.append("    Each phase has following options.").append(LINE_SEP)
    s.append("    format: {phase} [-{phase}:{option}[={input}]]*").append(LINE_SEP).append(LINE_SEP)
    phases foreach (phase => {
      s.append(s"    %-${INDENT}s".format(phase.name))
      Useful.indentation(s, phase.help, INDENT + 4)
      s.append(LINE_SEP)
        .append(LINE_SEP)
      phase.getOptDescs foreach {
        case (name, desc) =>
          s.append(s"%${INDENT + 4}s".format("") + s"If $name is given, $desc").append(LINE_SEP)
      }
      s.append(LINE_SEP)
    })
    s.append("* global option:").append(LINE_SEP).append(LINE_SEP)
    options.foreach {
      case (opt, kind, desc) =>
        val name = s"-${opt}${kind.postfix}"
        s.append(s"    If $name is given, $desc").append(LINE_SEP)
    }
    s.toString
  }
}

case class JSTARConfig(
  var command: Command[_],
  var args: List[String] = Nil,
  var silent: Boolean = false,
  var debug: Boolean = false,
  var time: Boolean = false
) extends Config
