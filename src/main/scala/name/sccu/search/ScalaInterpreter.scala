package name.sccu.search

import java.io.{InputStreamReader, FileReader, Reader}

import java.net.URLClassLoader
import scala.tools.nsc.Global
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.IMain

object ScalaInterpreter {
  private val defaultSettings = {
    val s = new Settings()
    s.usejavacp.value = true
    getClass.getClassLoader.asInstanceOf[URLClassLoader].getURLs.map(_.getPath).foreach(s.classpath.append)
    s
  }

  private val interpreter = new IMain(defaultSettings)

  def interpretCode[T](code: String): T = {
    val obj = interpreter.eval(code)
    assert(obj != null)
    obj.asInstanceOf[T]
  }

  def interpretResourceFile[T](path: String): T = {
    val file = getClass.getResourceAsStream(path)
    val reader = new InputStreamReader(file)
    val obj = interpreter.eval(reader)
    assert(obj != null)
    obj.asInstanceOf[T]
  }
}
