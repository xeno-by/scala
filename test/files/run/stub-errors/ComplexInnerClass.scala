object Test extends scala.tools.partest.StubErrorMessageTest {
  def codeA = """
    package stuberrors
    class A
  """

  def codeB = """
    package stuberrors
    class B {
      def foo: String = ???

      // unused and should fail, but not loaded
      def unsafeFoo: A = ???
      // used, B.info -> BB.info -> unpickling A -> stub error
      class BB extends A
    }
  """

  def userCode = """
    package stuberrors
    class C {
      def aloha = {
        val b = new B
        println(b.foo)
        new b.BB
      }
    }
  """

  def removeFromClasspath(): Unit = {
    removeClasses("stuberrors", List("A"))
  }
}
