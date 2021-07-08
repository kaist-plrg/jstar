package kr.ac.kaist.jstar.ir

import kr.ac.kaist.jstar.cfg._
import kr.ac.kaist.jstar.spec.algorithm._
import kr.ac.kaist.jstar.ir.Parser

// parser for ir with algorithm definitions
object IRParser extends Parser {
  // parse a file
  def fileToIR(f: String): (List[Algo], Inst) = fromFile(f, ir)
  def fileToAlgo(f: String): Algo = fromFile(f, algo)

  // parse a string
  def parseIR(str: String): (List[Algo], Inst) = errHandle(parseAll(ir, str))
  def parseAlgo(str: String): Algo = errHandle(parseAll(algo, str))

  // IR
  lazy val ir: Parser[(List[Algo], Inst)] = rep(irElem) ^^ {
    case elems => elems.foldLeft((Vector[Algo](), Vector[Inst]())) {
      case ((a, i), Left(algo)) => (a :+ algo, i)
      case ((a, i), Right(inst)) => (a, i :+ inst)
    }
  } ^^ {
    case (algos, insts) => (algos.toList, ISeq(insts.toList))
  }
  lazy val irElem: Parser[Either[Algo, Inst]] =
    algo ^^ { Left(_) } | inst ^^ { Right(_) }

  // algorithm
  lazy val algo: Parser[Algo] = "def" ~> head ~ ("=" ~> inst) ^^ {
    case h ~ body => Algo(h, Nil, body, Nil)
  }

  // head
  lazy val head: Parser[Head] = "\\S+".r ~ { "(" ~> repsep(ident ~ opt("?"), ",") <~ ")" } ^^ {
    case name ~ params => NormalHead(name, params.map {
      case name ~ Some(_) => Param(name, Param.Kind.Optional)
      case name ~ None => Param(name, Param.Kind.Normal)
    })
  }
}
