package kr.ac.kaist.jiset.generator

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.BugPatch._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.algorithm.Algorithm
import kr.ac.kaist.jiset.model.AlgoCompiler

object MethodGenerator {
  def apply(packageName: String, modelDir: String, name: String): Unit = {
    val scalaName = getScalaName(name)
    val algo = Algorithm(s"$RESOURCE_DIR/$VERSION/auto/algorithm/$name.json")
    if (VERSION == "es2019") name match {
      case "ForInOfHeadEvaluation" if assertForAsyncIterator => patchAssertForAsyncIterator(algo)
      case "StringGetOwnProperty" if numberEqual => patchNumberEqual(algo)
      case "AbstractEqualityComparison" if completionInAbstractEquality => patchCompletionInAbstractEquality(algo)
      case "EqualityExpression2Evaluation0" if completionInEqualityExpr => patchCompletionInEqualityExpr(algo)
      case "AsyncGeneratorResumeNext" | "AsyncFromSyncIteratorContinuation" | "Await" if wrongArgsInPromiseResolve => patchWrongArgsInPromiseResolve(name, algo)
      case "IterationStatement12VarScopedDeclarations0" if duplicatedVarScopedDecl => patchDuplicatedVarScopedDecl(algo)
      case _ =>
    }
    generate(name, scalaName, algo)
    def generate(name: String, scalaName: String, algo: Algorithm): Unit = {
      val len = algo.length
      val lang = algo.lang
      val (func, _) = AlgoCompiler(name, algo).result

      val nf = getPrintWriter(s"$modelDir/algorithm/$scalaName.scala")
      val TRIPLE = "\"\"\""
      nf.println(s"""package $packageName.model""")
      nf.println(s"""""")
      nf.println(s"""import $packageName.Algorithm""")
      nf.println(s"""import $packageName.ir._""")
      nf.println(s"""import $packageName.ir.Parser._""")
      nf.println(s"""""")
      nf.println(s"""object $scalaName extends Algorithm {""")
      nf.println(s"""  val length: Int = $len""")
      nf.println(s"""  val lang: Boolean = $lang""")
      nf.println(s"""  val func: Func = parseFunc($TRIPLE${beautify(func, "  ")}$TRIPLE)""")
      nf.println(s"""}""")
      nf.close()
    }
  }
}
