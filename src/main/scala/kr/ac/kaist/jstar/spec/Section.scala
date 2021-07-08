package kr.ac.kaist.jstar.spec

import org.jsoup.nodes._
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.extractor.SectionParser

case class Section(id: String, subs: List[Section])
object Section {
  def apply(elem: Element): Section = SectionParser(elem)
}
