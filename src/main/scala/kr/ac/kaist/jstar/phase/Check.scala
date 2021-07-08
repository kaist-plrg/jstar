package kr.ac.kaist.jstar.phase

import kr.ac.kaist.jstar._
import kr.ac.kaist.jstar.checker._
import kr.ac.kaist.jstar.spec._
import kr.ac.kaist.jstar.util.Useful._
import kr.ac.kaist.jstar.util._
import kr.ac.kaist.jstar.spec.algorithm.Algo
import scala.annotation.unused

// Check phase
case object Check extends PhaseObj[ECMAScript, CheckConfig, List[Bug]] {
  val name = "check"
  val help = "performs static checkers for specifications."

  def apply(
    spec: ECMAScript,
    jstarConfig: JSTARConfig,
    config: CheckConfig
  ): List[Bug] = {
    val completeAlgos = spec.completedAlgos
    val targets =
      if (config.target.isEmpty) completeAlgos
      else completeAlgos.filter(config.target contains _.name)
    println(s"checking ${targets.size} algorithms...")

    println
    val (_, refErrors) = time(s"check variable reference", {
      ReferenceChecker(spec, targets)
    })
    refErrors.foreach(println _)
    println(s"${refErrors.length} algorithms have reference errors.")

    println
    val (_, missingRets) = time(s"check missing return branch", {
      MissingRetChecker(spec, targets)
    })
    missingRets.foreach(println _)
    println(s"${missingRets.length} algorithms have missing return branch errors.")

    println
    val (_, arityErrors) = time(s"check arity", {
      ArityChecker(spec, targets)
    })
    arityErrors.foreach(println _)
    println(s"# of arity mismatch : ${arityErrors.length}")

    println
    val bugs = refErrors ++ missingRets ++ arityErrors
    println(s"Total ${bugs.length} bugs detected.")
    bugs
  }

  def defaultConfig: CheckConfig = CheckConfig()
  val options: List[PhaseOption[CheckConfig]] = List(
    ("target", ListOption((c, l) => c.target = l),
      "target algorithms to check")
  )
}

// Check phase config
case class CheckConfig(
  var target: List[String] = Nil
) extends Config
