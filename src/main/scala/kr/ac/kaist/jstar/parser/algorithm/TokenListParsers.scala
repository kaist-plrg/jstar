package kr.ac.kaist.jstar.parser.algorithm

import kr.ac.kaist.jstar.ir.Inst
import kr.ac.kaist.jstar.LINE_SEP
import kr.ac.kaist.jstar.spec.algorithm._
import scala.util.parsing.combinator._
import scala.util.parsing.input._

trait TokenListParsers extends PackratParsers {
  type Elem = Token
  case class TokenPosition(
    line: Int,
    column: Int,
    protected val lineContents: String
  ) extends Position
  abstract class TokenReader extends Reader[Token] { outer =>
    val tokens: List[Token]
    val pos: TokenPosition
    val stringList: List[String]

    def first: Token = tokens.head
    def rest: TokenReader = {
      val isNewline = tokens match {
        case In :: _ => true
        case Next(_) :: rest => rest match {
          case Out :: _ => false
          case _ => true
        }
        case _ => false
      }
      val width = 1 + (first match {
        case (t: NormalToken) => t.toString.length
        case _ => 0
      })
      new TokenReader {
        val tokens = outer.tokens.tail
        val stringList =
          if (isNewline) outer.stringList.tail
          else outer.stringList
        val pos = if (isNewline) TokenPosition(
          outer.pos.line + 1,
          {
            val str = stringList.head
            var c = 0
            while (c < str.length && str.charAt(c) == ' ') c += 1
            c + 1
          },
          stringList.head
        )
        else TokenPosition(
          outer.pos.line,
          outer.pos.column + width,
          stringList.head
        )
      }
    }
    def atEnd: Boolean = tokens.isEmpty
  }
  object TokenReader {
    def apply(ts: List[Token]): TokenReader = new TokenReader {
      val tokens = ts
      val stringList = Token.getString(tokens).split(LINE_SEP).toList :+ ""
      val pos = TokenPosition(1, 1, stringList.head)
    }
  }

  private def firstMap[T](in: Input, f: Token => ParseResult[T]): ParseResult[T] = {
    if (in.atEnd) Failure("no more tokens", in)
    else f(in.first)
  }

  private val wordChars = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '_').toSet
  private val numChars = ('0' to '9').toSet

  private def splitText(s: String): List[String] =
    "([a-zA-Z0-9_]+|\\S)".r.findAllIn(s).toList

  implicit def literal(s: String): PackratParser[List[String]] = {
    val texts = splitText(s)
    Parser(in => {
      val init = success[List[String]](Nil)(in)
      texts.foldLeft(init) {
        case (Success(res, in), x) => firstMap(in, t => t match {
          case (_: Id) | (_: Value) | (_: Code) | (_: Const) => Failure(s"`$x` expected but `$t` found", in)
          case (t: NormalToken) if x.toLowerCase == t.getContent.toLowerCase =>
            Success(t.getContent :: res, in.rest)
          case t => Failure(s"`$x` expected but `$t` found", in)
        })
        case (e, _) => e
      }.map(_.reverse)
    })
  }

  lazy val const: Parser[String] = Parser(in => firstMap(in, _ match {
    case Const(x) => Success(x, in.rest)
    case t => Failure(s"`Const(_)` expected but `$t` found", in)
  }))

  lazy val code: Parser[String] = Parser(in => firstMap(in, _ match {
    case Code(x) => Success(x, in.rest)
    case t => Failure(s"`Code(_)` expected but `$t` found", in)
  }))

  lazy val value: Parser[String] = Parser(in => firstMap(in, _ match {
    case Value(x) => Success(x, in.rest)
    case t => Failure(s"`Value(_)` expected but `$t` found", in)
  }))

  lazy val id: Parser[String] = Parser(in => firstMap(in, _ match {
    case Id(x) => Success(x, in.rest)
    case t => Failure(s"`Id(_)` expected but `$t` found", in)
  }))

  lazy val text: Parser[String] = Parser(in => firstMap(in, _ match {
    case Text(x) => Success(x, in.rest)
    case t => Failure(s"`Text(_)` expected but `$t` found", in)
  }))

  lazy val star: Parser[String] = Parser(in => firstMap(in, _ match {
    case Star(x) => Success(x, in.rest)
    case t => Failure(s"`Star(_)` expected but `$t` found", in)
  }))

  lazy val nt: Parser[String] = Parser(in => firstMap(in, _ match {
    case Nt(x) => Success(x, in.rest)
    case t => Failure(s"`Nt(_)` expected but `$t` found", in)
  }))

  lazy val sup: Parser[List[Token]] = Parser(in => firstMap(in, _ match {
    case Sup(list) => Success(list, in.rest)
    case t => Failure(s"`Sup(_)` expected but `$t` found", in)
  }))

  lazy val link: Parser[String] = Parser(in => firstMap(in, _ match {
    case Link(x) => Success(x, in.rest)
    case t => Failure(s"`Link(_)` expected but `$t` found", in)
  }))

  lazy val grammar: Parser[Gr] = Parser(in => firstMap(in, _ match {
    case (g: Gr) => Success(g, in.rest)
    case t => Failure(s"`Gr(_)` expected but `$t` found", in)
  }))

  lazy val sub: Parser[List[Token]] = Parser(in => firstMap(in, _ match {
    case Sub(list) => Success(list, in.rest)
    case t => Failure(s"`Sub(_)` expected but `$t` found", in)
  }))

  lazy val next: Parser[Int] = Parser(in => firstMap(in, _ match {
    case Next(k) => Success(k, in.rest)
    case t => Failure(s"`Next(_)` expected but `$t` found", in)
  }))

  lazy val in: Parser[String] = Parser(in => firstMap(in, _ match {
    case In => Success("", in.rest)
    case t => Failure(s"`In` expected but `$t` found", in)
  }))

  lazy val out: Parser[String] = Parser(in => firstMap(in, _ match {
    case Out => Success("", in.rest)
    case t => Failure(s"`Out` expected but `$t` found", in)
  }))

  lazy val normal: Parser[Token] = Parser(in => firstMap(in, _ match {
    case (t: NormalToken) => Success(t, in.rest)
    case t => Failure(s"NormalToken expected but `$t` found", in)
  }))

  lazy val word: Parser[String] = Parser(in => text(in).mapPartial(_ match {
    case s if wordChars contains s.head => s
  }, s => s"`$s` is not word"))

  lazy val notNumber: Parser[String] = Parser(in => text(in).mapPartial(_ match {
    case s if !(numChars contains s.head) => s
  }, s => s"`$s` is number"))

  lazy val number: Parser[String] = Parser(in => text(in).mapPartial(_ match {
    case s if numChars contains s.head => s
  }, s => s"`$s` is not number"))

  // failed lines
  protected var failed: Map[Int, List[Token]] = Map()

  lazy val token: PackratParser[Token] = normal
  lazy val rest: PackratParser[List[String]] = rep(token ^^ { _.toString })
  lazy val step: PackratParser[List[String]] = rest <~ next

  def parse[T](p: Parser[T], tokenReader: TokenReader): ParseResult[T] =
    p(tokenReader)

  def parse[T](p: Parser[T], tokens: List[Token]): ParseResult[T] =
    parse(p, TokenReader(tokens))

  def parseAll[T](p: Parser[T], tokenReader: TokenReader): ParseResult[T] =
    phrase(p)(tokenReader)

  def parseAll[T](p: Parser[T], tokens: List[Token]): ParseResult[T] =
    parse(phrase(p), tokens)

  // logging
  var keepLog = true
  protected def log[T](p: Parser[T])(name: String): Parser[T] = Parser { in =>
    val stopMsg = s"trying $name at [${in.pos}] \n\n${in.pos.longString}\n"
    if (keepLog) stop(stopMsg) match {
      case "q" =>
        keepLog = false
        p(in)
      case "j" =>
        keepLog = false
        val r = p(in)
        println(name + " --> " + r)
        keepLog = true
        r
      case _ =>
        val r = p(in)
        println(name + " --> " + r)
        r
    }
    else p(in)
  }

  // stop message
  protected def stop(msg: String): String = {
    println(msg)
    scala.io.StdIn.readLine
  }
}
