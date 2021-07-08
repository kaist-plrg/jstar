package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.parser.ECMAScriptParser
import kr.ac.kaist.jstar.spec.ECMAScript
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.util._
import org.jsoup.nodes._

// ExtractTag phase
case object ExtractTag extends PhaseObj[Unit, ExtractTagConfig, List[Element]] {
  val name = "extract-tag"
  val help = "Extract the content of the tag to stdout"

  def apply(
    unit: Unit,
    jstarConfig: JSTARConfig,
    config: ExtractTagConfig
  ): List[Element] = {
    val version = config.version.getOrElse("recent")
    println(s"version: $version (${getRawVersion(version)})")

    implicit val (_, (_, document, _)) = time("preprocess", {
      ECMAScriptParser.preprocess(version)
    })

    val (_, elems) = time("extract contents of tags from spec.html", for {
      tag <- jstarConfig.args
      elem <- toArray(document.getElementsByTag(tag))
    } yield elem)

    elems
  }

  def defaultConfig: ExtractTagConfig = ExtractTagConfig()

  val options: List[PhaseOption[ExtractTagConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
  )
}

// ExtractTag phase config
case class ExtractTagConfig(
  var version: Option[String] = None
) extends Config
