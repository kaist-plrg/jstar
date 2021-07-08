package kr.ac.kaist.jstar.spec.algorithm

import kr.ac.kaist.jstar.LINE_SEP

// tokens
abstract class Token(name: String, content: String) {
  // get content
  def getContent: String = content

  // conversion to string
  override def toString: String = this match {
    case Text(t) => t
    case _ => s"$name:{$content}"
  }
}
object Token {
  def getString(tokens: List[Token]): String = {
    val sb = new StringBuilder
    val TAB = 2
    var indent = 0
    def newline: Unit = sb.append(LINE_SEP).append(" " * indent)
    def deleteRight(n: Int) = sb.delete(sb.length - n, sb.length)
    def deleteTab: Unit = deleteRight(TAB)
    def deleteNewLine: Unit = deleteRight(LINE_SEP.length)
    def t(token: Token): Unit = token match {
      case (_: NormalToken) => sb.append(token).append(" ")
      case Next(_) => newline
      case Out =>
      case In =>
        indent += TAB; newline
    }
    def ts(tokens: List[Token]): Unit = tokens match {
      case Out :: Next(_) :: Nil =>
        indent -= TAB; deleteTab; deleteNewLine;
      case Out :: Next(_) :: rest =>
        indent -= TAB; deleteTab; ts(rest)
      case v :: rest =>
        t(v); ts(rest)
      case Nil =>
    }
    ts(tokens)
    sb.toString
  }
}

// normal tokens
abstract class NormalToken(name: String, content: String)
  extends Token(name, content)
case class Const(const: String) extends NormalToken("const", const)
case class Code(code: String) extends NormalToken("code", code)
case class Value(value: String) extends NormalToken("value", value)
case class Id(id: String) extends NormalToken("id", id)
case class Text(text: String) extends NormalToken("text", text)
case class Star(text: String) extends NormalToken("star", text)
case class Nt(nt: String) extends NormalToken("nt", nt)
case class Sup(sup: List[Token]) extends NormalToken("sup", sup.mkString(" "))
case class Link(link: String) extends NormalToken("link", link)
case class Gr(grammar: String, subs: List[String])
  extends NormalToken("grammar", grammar + ", " + subs.mkString("[", ", ", "]"))
case class Sub(sub: List[Token]) extends NormalToken("sub", sub.mkString(" "))

// special tokens only in steps
case class Next(k: Int) extends Token("next", k.toString)
case object In extends Token("in", "")
case object Out extends Token("out", "")
