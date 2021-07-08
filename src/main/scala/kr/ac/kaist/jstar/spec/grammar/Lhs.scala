package kr.ac.kaist.jstar.spec.grammar

// ECMAScript grammar left-hand-sides
case class Lhs(
  name: String,
  params: List[String]
) {
  def isModule: Boolean = Grammar.isModuleNT(name)
  def isSupplemental: Boolean = Grammar.isSupplementalNT(name)
  def isTarget: Boolean = Grammar.isTargetNT(name)
  def isScript: Boolean = name == "Script"

  // conversion to string
  override def toString: String = {
    val paramsStr = if (params.isEmpty) "" else params.mkString("[", ", ", "]")
    s"$name$paramsStr:"
  }
}
