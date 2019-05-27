package kr.ac.kaist.ase.generator

import kr.ac.kaist.ase._
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.spec._

object ModelGenerator {
  def apply(version: String, spec: Spec): Unit = {
    val methods = spec.globalMethods
    val consts = spec.consts
    val grammar = spec.grammar
    val tys = spec.tys
    methods.foreach(name => MethodGenerator(version, name))
    GrammarGenerator(version, grammar)
    tys.foreach(ty => TypeGenerator(version, ty))

    val nf = getPrintWriter(s"$MODEL_DIR/Model.scala")
    nf.println(s"""package kr.ac.kaist.ase.model""")
    nf.println(s"""""")
    nf.println(s"""import kr.ac.kaist.ase.core._""")
    nf.println(s"""object Model {""")
    nf.println(s"""  lazy val initState: State = State(""")
    nf.println(s"""    retValue = None,""")
    nf.println(s"""    insts = Nil,""")
    nf.println(s"""    globals = initGlobal,""")
    nf.println(s"""    locals = Map(),""")
    nf.println(s"""    heap = initHeap""")
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initGlobal: Map[Id, Value] = Map(""")
    nf.println(methods.map(i =>
      s"""    Id("$i") -> $i.func""").mkString("," + LINE_SEP))
    nf.println(s"""  ) ++ Map(""")
    nf.println(consts.map(i =>
      s"""    Id("$i") -> NamedAddr("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""  lazy val initHeap: Heap = Heap(Map(""")
    nf.println(consts.map(i =>
      s"""    NamedAddr("$i") -> Singleton("$i")""").mkString("," + LINE_SEP))
    nf.println(s"""  ))""")
    nf.println(s"""  lazy val initType: Map[String, CoreMap] = Map(""")
    nf.println(tys.map(ty =>
      s"""    ("${ty.name}" -> ${ty.name}.map)""").mkString("," + LINE_SEP))
    nf.println(s"""  )""")
    nf.println(s"""}""")
    nf.close()
  }
}
