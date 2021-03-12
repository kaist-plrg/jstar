package kr.ac.kaist.jiset

import scala.Console.RED
import kr.ac.kaist.jiset.CHECK_ALARM
import kr.ac.kaist.jiset.cfg.DotPrinter
import kr.ac.kaist.jiset.util.Useful._

package object analyzer {
  // initialize
  mkdir(ANALYZE_LOG_DIR)
  val nfAlarms = getPrintWriter(s"$ANALYZE_LOG_DIR/alarms")

  // transfer
  var transfer: AbsTransfer = null

  // alarm
  var alarmCP: ControlPoint = null
  var alarmCPStr: String = ""

  private var alarmMap: Map[ControlPoint, Set[String]] = Map()
  def alarm(msg: String): Unit = if (!TEST_MODE) {
    val set = alarmMap.getOrElse(alarmCP, Set())
    if (!(set contains msg)) {
      alarmMap += alarmCP -> (set + msg)
      val errMsg = s"[Bug] $msg @ $alarmCPStr"
      Console.err.println(setColor(RED)(errMsg))
      if (LOG) nfAlarms.println(errMsg)
      if (CHECK_ALARM) transfer.REPL.run(alarmCP)
    }
  }

  // dump CFG in DOT/PDF format
  def dumpCFG(
    sem: AbsSemantics,
    cp: Option[ControlPoint] = None,
    pdf: Boolean = true,
    depth: Option[Int] = None
  ): Unit = try {
    val dot = (new DotPrinter)(sem, cp, depth).toString
    dumpFile(dot, s"$CFG_DIR.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_DIR}_trans.dot $CFG_DIR.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_DIR}_trans.dot" -o "$CFG_DIR.pdf"""")
      println(s"Dumped CFG to $CFG_DIR.pdf")
    } else println(s"Dumped CFG to $CFG_DIR.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
  }

}
