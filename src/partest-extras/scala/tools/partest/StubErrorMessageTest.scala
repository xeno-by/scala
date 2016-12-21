package scala.tools.partest

import java.io.File

trait StubErrorMessageTest extends StoreReporterDirectTest {
  // Stub to feed to partest, unused
  def code = throw new Error("Use `userCode` instead of `code`.")

  def compileCode(code: String) = {
    val classpath = List(sys.props("partest.lib"), testOutput.path)
      .mkString(sys.props("path.separator"))
    compileString(newCompiler("-cp", classpath, "-d", testOutput.path))(code)
  }

  def removeClasses(inPackage: String, classNames: Seq[String]): Unit = {
    val pkg = new File(testOutput.path, inPackage)
    classNames.foreach { className =>
      val classFile = new File(pkg, s"$className.class")
      assert(classFile.exists)
      assert(classFile.delete())
    }
  }

  def removeFromClasspath(): Unit
  def codeA: String
  def codeB: String
  def userCode: String

  def show(): Unit = {
    compileCode(codeA)
    assert(filteredInfos.isEmpty, filteredInfos)

    compileCode(codeB)
    assert(filteredInfos.isEmpty, filteredInfos)
    removeFromClasspath()

    compileCode(userCode)
    import scala.reflect.internal.util.Position
    filteredInfos.map { report =>
      print(if (report.severity == storeReporter.ERROR) "error: " else "")
      println(Position.formatMessage(report.pos, report.msg, true))
    }
  }
}
