package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.extractor.ECMAScriptParser
import kr.ac.kaist.jstar.spec._
import kr.ac.kaist.jstar.spec.algorithm.Algo
import kr.ac.kaist.jstar.spec.JsonProtocol._
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.util._
import kr.ac.kaist.jstar.analyzer.Stat

// Extract phase
case object Extract extends PhaseObj[Unit, ExtractConfig, ECMAScript] {
  val name = "extract"
  val help = "extracts ECMAScript model from ecma262/spec.html."

  def apply(
    unit: Unit,
    jstarConfig: JSTARConfig,
    config: ExtractConfig
  ): ECMAScript = {
    val (extractTime, spec) = config.load match {
      case Some(filename) =>
        time(s"loading ECMAScript from $filename", {
          readJson[ECMAScript](filename)
        })
      case None =>
        val version = config.version.getOrElse("recent")
        val query = config.query.getOrElse("")
        val proposals = config.proposals.getOrElse(Nil)
        println(s"version: $version (${getRawVersion(version)})")
        proposals.foreach(proposal => println(s"additional proposal: $proposal"))
        if (query != "") println(s"query: $query")
        time(s"extracting spec.html", {
          ECMAScriptParser(version, proposals, query, config.detail)
        })
    }
    Stat.extractTime = extractTime

    // logging
    if (LOG) dumpIncompleteAlgos(spec.incompletedAlgos)

    // Dump JSON
    config.json.map(dumpJson("specification", spec, _))

    spec
  }

  // dump incompleted algorithms and their names
  def dumpIncompleteAlgos(algos: List[Algo]): Unit = {
    // create directory
    val dir = s"$EXTRACT_LOG_DIR/incompleted"
    mkdir(dir)

    // dump incompleted algorithms names
    val names = algos.map(_.name).mkString(LINE_SEP)
    dumpFile("incomplete algorithm names", names, s"$dir/names.log")

    // dump incompleted algorithms
    val app = new Appender
    for (algo <- algos) {
      app >> "========================================" >> LINE_SEP
      app >> algo.name >> " ====>" >> LINE_SEP
      for ((t, i) <- algo.todos.zipWithIndex)
        app >> "[" >> i >> "]" >> t >> LINE_SEP
    }
    dumpFile(
      "incomplete algorithms",
      app.toString,
      s"$dir/algorithms.log"
    )
  }

  def defaultConfig: ExtractConfig = ExtractConfig()
  val options: List[PhaseOption[ExtractConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("proposals", ListOption((c, l) => c.proposals = Some(l)),
      "parse additional proposal ecmarkup files."),
    ("query", StrOption((c, s) => c.query = Some(s)),
      "set target query."),
    ("load", StrOption((c, s) => c.load = Some(s)),
      "load ECMAScript from JSON."),
    ("json", StrOption((c, s) => c.json = Some(s)),
      "dump ECMAScript in a JSON format."),
    ("detail", BoolOption(c => c.detail = true),
      "print log.")
  )
}

// Extract phase config
case class ExtractConfig(
  var version: Option[String] = None,
  var proposals: Option[List[String]] = None,
  var query: Option[String] = None,
  var load: Option[String] = None,
  var json: Option[String] = None,
  var detail: Boolean = false
) extends Config
