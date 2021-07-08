package kr.ac.kaist.jstar.util

import java.io.{ Reader, File, PrintWriter }
import java.nio.file.{ Files, StandardCopyOption }
import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.error._
import org.apache.commons.text.StringEscapeUtils
import org.jsoup._
import org.jsoup.nodes._
import org.jsoup.select._
import scala.Console.RESET
import scala.collection.mutable
import scala.concurrent._
import scala.io.Source
import scala.sys.process._
import scala.util.Random.shuffle
import spray.json._

object Useful {
  val ENC = "utf8"

  // file reader
  def fileReader(filename: String): Reader =
    Source.fromFile(filename, ENC).bufferedReader

  // indentation
  def indentation(s: StringBuilder, str: String, indent: Int): Unit = {
    str.split(LINE_SEP) match {
      case Array(str, rest @ _*) => {
        s.append(str)
        rest.foreach(rStr => {
          s.append(LINE_SEP)
          for (i <- 0 until indent) { s.append(" ") }
          s.append(rStr)
        })
      }
      case _ =>
    }
  }

  // walk directory
  def shuffle[T](seq: Iterable[T]): Iterable[T] = shuffle(seq)
  def walkTree(filename: String): Iterable[File] = walkTree(new File(filename))
  def walkTree(file: File): Iterable[File] = {
    val children = new Iterable[File] {
      def iterator: Iterator[File] =
        if (file.isDirectory) file.listFiles.iterator
        else Iterator.empty
    }
    Seq(file) ++ children.flatMap(walkTree(_))
  }

  // extension filter
  def extFilter(ext: String): String => Boolean = _.endsWith(s".$ext")
  lazy val irFilter = extFilter("ir")
  lazy val jsFilter = extFilter("js")
  lazy val specFilter = extFilter("spec")
  lazy val jsonFilter = extFilter("json")
  lazy val scalaFilter = extFilter("scala")
  lazy val grammarFilter = extFilter("grammar")
  lazy val dotFilter = extFilter("dot")

  // file writer
  def getPrintWriter(filename: String): PrintWriter =
    new PrintWriter(new File(filename))

  // dump given data to a file
  def dumpFile(data: Any, filename: String): Unit = {
    val nf = getPrintWriter(filename)
    nf.print(data)
    nf.close()
  }
  def dumpFile(name: String, data: Any, filename: String): Unit = {
    val res = dumpFile(data, filename)
    println(s"dumped $name to $filename.")
  }

  // dump given data as JSON
  def dumpJson[T](
    data: T,
    filename: String
  )(implicit writer: JsonWriter[T]): Unit = {
    val json = data.toJson
    dumpFile(json.prettyPrint, filename)
  }
  def dumpJson[T](
    name: String,
    data: T,
    filename: String
  )(implicit writer: JsonWriter[T]): Unit = {
    dumpJson(data, filename)
    println(s"dumped $name to $filename in a JSON format.")
  }

  // read file
  def readFile(filename: String): String =
    Source.fromFile(filename, ENC).mkString

  // read JSON
  def readJson[T](filename: String)(implicit reader: JsonReader[T]): T =
    readFile(filename).parseJson.convertTo[T]

  // read HTML
  def readHtml(filename: String): Document = Jsoup.parse(readFile(filename))

  // delete files
  def deleteFile(filename: String): Unit = new File(filename).delete

  // change extension
  def changeExt(from: String, to: String): String => String =
    filename => filename.substring(0, filename.length - from.length) + to

  // get name without extension
  def removedExt(filename: String): String =
    filename.split('.').dropRight(1).mkString(".")

  // beautify
  def beautify[T](t: T)(implicit app: Appender.App[T]): String =
    app(new Appender, t).toString

  // get extension
  def getExt(filename: String): String =
    filename.split('.').last

  // renamed filename
  def renameFile(from: String, to: String): Unit =
    new File(from).renameTo(new File(to))

  // copy file
  def copyFile(from: String, to: String): Unit = Files.copy(
    new File(from).toPath,
    new File(to).toPath,
    StandardCopyOption.REPLACE_EXISTING
  )

  // create directories
  def mkdir(name: String): Unit = new File(name).mkdirs

  // file existence check
  def exists(name: String): Boolean = new File(name).exists

  // colored println
  def setColor(color: String): Any => String =
    if (color == "") x => x.toString else x => color + x.toString + RESET
  def printColor(color: String): Any => Unit = x => print(setColor(color)(x))
  def printlnColor(color: String): Any => Unit = x => println(setColor(color)(x))

  // get name that could be used in Scala identifiers
  private val symbolRegex = "@@([^@]+)".r
  private val intrinsicRegex = "%([^%]+)%".r
  def getScalaName(str: String): String = {
    val replaces = Map(
      "\\." -> "DOT",
      ":" -> "COLON"
    )
    replaces.foldLeft(str) {
      case (str, (from, to)) => str.replaceAll(from, to)
    } match {
      case intrinsicRegex(x) => "INTRINSIC_" + x
      case symbolRegex(x) => "SYMBOL_" + x
      case x => x
    }
  }

  // cache for function
  def cached[A, B](f: A => B): A => B = {
    val cache = mutable.Map.empty[A, B]
    arg => cache.getOrElse(arg, {
      val res = f(arg)
      cache.update(arg, res)
      res
    })
  }

  // throw a simple error
  def error(msg: String): Nothing = throw new JSTARError(msg)

  // get duration time
  def time[T](f: => T): (Long, T) = {
    val start = System.currentTimeMillis
    val result = f
    val end = System.currentTimeMillis
    (end - start, result)
  }

  // print duration time with loading message
  def time[T](msg: String, f: => T): (Long, T) = {
    lazy val f0 = f
    print(s"$msg...")
    val (interval, res) = time(f0)
    println(f" ($interval%,d ms)")
    (interval, res)
  }

  // catch exceptions with Option[_]
  def optional[T](f: => T): Option[T] = try Some(f) catch {
    case e: Throwable => if (DEBUG) println(e); None
  }

  // get indentation
  def getIndent(str: String): Int =
    "[ ]+".r.findFirstIn(str).fold(-1)(_.length)

  // revert entity name to character
  val unescapeHtml = StringEscapeUtils.unescapeHtml4(_)

  // revert character to entity name
  val escapeHtml = StringEscapeUtils.escapeHtml4(_)

  def isNormalExit(given: String): Boolean =
    optional(executeCmd(given)).isDefined

  // execute shell command with given dir, default to CUR_DIR
  def executeCmd(given: String, dir: String = CUR_DIR): String = {
    var cmd =
      if (DEBUG) { println(s"[SHELL] $given"); given }
      else s"$given 2> /dev/null"
    var directory = new File(dir)
    var process = Process(Seq("sh", "-c", cmd), directory)
    process !!
  }

  // change git version
  def changeVersion(target: String, dir: String = CUR_DIR): Unit =
    executeCmd(s"git checkout $target", dir)

  // get git current version
  def currentVersion(dir: String = CUR_DIR): String =
    executeCmd(s"git rev-parse HEAD", dir).trim

  // get Element array using queries
  def getElems(elem: Element, query: String): Array[Element] =
    toArray(elem.select(query))
  def toArray(elems: Elements): Array[Element] =
    elems.toArray(Array[Element]())

  // get range of element
  def getRange(elem: Element): Option[(Int, Int)] = {
    val s = elem.attr("s")
    val e = elem.attr("e")
    if (s == "") None else Some((s.toInt, e.toInt))
  }

  // get raw body of element
  def getRawBody(elem: Element)(implicit lines: Array[String]): Array[String] = {
    getRange(elem) match {
      case Some((s, e)) if s + 1 < e => lines.slice(s + 1, e - 1)
      case _ => Array(elem.html.replaceAll(LINE_SEP, " "))
    }
  }

  // split lists by a separator
  def splitBy[T](list: List[T], sep: T): List[List[T]] = {
    @annotation.tailrec
    def aux(xs: List[T], revAcc: List[List[T]]): List[List[T]] = xs match {
      case Nil => revAcc.reverse
      case h :: t =>
        val (pref, suff) = (if (h == sep) xs.tail else xs).span(_ != sep)
        aux(suff, pref :: revAcc)
    }
    aux(list, Nil)
  }

  // trim only right
  def trimRight(str: String): String =
    str.reverse.span(_ == ' ')._2.reverse

  // error log
  def errorLog[T](f: => T)(msg: String): T = try f catch {
    case e: Throwable => println(msg); throw e
  }

  // get version
  def getVersion(version: String): String =
    if (version == RECENT_VERSION) "recent" else version

  // get raw version
  def getRawVersion(version: String): String =
    if (version == "recent") RECENT_VERSION else version
}
