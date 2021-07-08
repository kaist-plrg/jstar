package kr.ac.kaist.jstar.analyzer

import kr.ac.kaist.jstar.ir
import kr.ac.kaist.jstar.util.Appender._
import kr.ac.kaist.jstar.util.Useful._

class BeautifierTinyTest extends AnalyzerTest {
  val name: String = "analyzerBeautifierTest"

  def test[T](desc: String)(cases: (T, String)*)(
    implicit
    tApp: App[T]
  ): Unit = check(desc, cases.foreach {
    case (given, expected) =>
      val result = given.toString
      if (result != expected) {
        println(s"FAILED: $result != $expected")
        assert(result == expected)
      }
  })

  // registration
  def init: Unit = {
    import ir._

    // test("Primitive Values")(
    //   Num(42.34) -> "42.34",
    //   INum(23) -> "23i",
    //   BigINum(BigInt(2).pow(100)) -> "1267650600228229401496703205376n",
    //   Str("hello") -> "\"hello\"",
    //   Bool(true) -> s"true",
    //   Bool(false) -> s"false",
    //   Undef -> "undefined",
    //   Null -> "null",
    //   Absent -> "absent",
    // )

    // test("AST values")(ASTVal("Literal") -> "☊(Literal)")

    // test("Locations")(
    //   NamedAddr("Global") -> "#Global",
    //   AllocSite(432, 42) -> "#432:42",
    //   CallSite(432, 42, LocNameType("A")) -> "#432:42:A",
    // )

    // test("Abstract Values")(
    //   AbsValue(42.34, BigInt(24), true) -> "42.34 | 24n | true",
    //   AbsValue(123, "abc", Undef, Null, Absent) -> "123.0 | \"abc\" | undef | null | ?",
    //   AbsValue(1.2, 2.3, 3, 4, BigInt(2), BigInt(3)) -> "num | bigint",
    //   AbsValue("a", "b", true, false) -> "str | bool",
    //   AbsValue(42, NamedAddr("Global"), AllocSite(432, 42)) -> "(#Global | #432:42) | 42.0",
    //   (AbsValue(42, Const("empty")) ⊔ AbsValue(Ty("Object"))) -> "Object | ~empty~ | 42.0",
    //   (AbsValue(true, Cont()) ⊔ AbsClo(Clo(42))) -> "λ(42) | κ | true",
    //   AbsValue(Clo(42, Env("x" -> Bool(true), "y" -> Num(42)))) -> """λ(42)[{
    //   |  x -> ! true
    //   |  y -> ! 42.0
    //   |}]""".stripMargin,
    //   AbsValue(ASTVal("Literal"), ASTVal("Identifier")) -> "(☊(Identifier) | ☊(Literal))",
    //   AbsValue(Const("invalid"), Const("empty")) -> "(~empty~ | ~invalid~)",
    //   AbsValue(Completion(CompNormal, 42, Const("empty"))) -> "N(42.0)",
    //   AbsValue(
    //     Completion(CompNormal, 42, Const("empty")),
    //     Completion(CompNormal, true, Const("empty")),
    //   ) -> "N(42.0 | true)",
    //   AbsValue(
    //     Completion(CompThrow, 42, Const("empty")),
    //     Completion(CompNormal, true, Const("empty")),
    //   ) -> "N(true) | T(42.0)",
    // )

    // val id = RefValueId("x")
    // val prop = RefValueProp(NamedAddr("Global"), "p")
    // val string = RefValueString("abc", "length")
    // test("Abstract Reference Values")(
    //   AbsRefValue.Bot -> "⊥",
    //   AbsRefValue(id) -> "x",
    //   AbsRefValue.Prop(AbsTy("Object"), AbsStr("p")) -> """(Object)["p"]""",
    //   AbsRefValue.Prop(
    //     AbsValue(AllocSite(1, 2)) ⊔ AbsTy("Object"),
    //     AbsStr("p")
    //   ) -> """(#1:2 | Object)["p"]""",
    //   AbsRefValue(prop) -> """(#Global)["p"]""",
    //   AbsRefValue(string) -> """("abc")["length"]""",
    //   AbsRefValue(id, prop, string) -> "⊤",
    // )

    // test("Abstract Objects")(
    //   AbsObj(SymbolObj("has")) -> """@"has"""",
    //   AbsObj(
    //     MapObj(Ty("Object"), "x" -> true, "y" -> 2),
    //     MapObj(Ty("Object"), "x" -> "a", "z" -> Null),
    //   ) -> """Object {
    //   |  x -> ! "a" | true
    //   |  y -> ? 2.0
    //   |  z -> ? null
    //   |}""".stripMargin,
    //   AbsObj(ListObj()) -> "[]",
    //   AbsObj(ListObj(Undef, true, 42)) -> "[42.0 | true | undef]",
    //   AbsObj(ListObj(0, 1, 2, 3)) -> "[num]",
    //   AbsObj.Top -> "⊤",
    // )

    // val heap = AbsHeap(Heap(
    //   NamedAddr("Global") -> SymbolObj("has"),
    //   NamedAddr("A") -> MapObj(Ty("Record")),
    // ))
    // test("Abstract Heaps")(
    //   heap -> """{
    //   |  #A -> Record {}
    //   |  #Global -> @"has"
    //   |}""".stripMargin,
    // )

    // val env = AbsEnv(Env(
    //   "x" -> 42,
    //   "y" -> true,
    // ), Env(
    //   "x" -> 42,
    //   "z" -> Null,
    // ))
    // test("Abstract Environments")(
    //   env -> """{
    //   |  x -> ! 42.0
    //   |  y -> ? true
    //   |  z -> ? null
    //   |}""".stripMargin,
    // )

    // val st = AbsState.Elem(env, heap)
    // test("Abstract State")(
    //   st -> """{
    //   |  env: {
    //   |    x -> ! 42.0
    //   |    y -> ? true
    //   |    z -> ? null
    //   |  }
    //   |  heap: {
    //   |    #A -> Record {}
    //   |    #Global -> @"has"
    //   |  }
    //   |}""".stripMargin,
    //   AbsState.Empty.copy(env = env) -> """{
    //   |  env: {
    //   |    x -> ! 42.0
    //   |    y -> ? true
    //   |    z -> ? null
    //   |  }
    //   |}""".stripMargin,
    // )
  }
  init
}
