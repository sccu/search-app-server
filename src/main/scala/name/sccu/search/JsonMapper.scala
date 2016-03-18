package name.sccu.search

import java.io.Writer

object JsonMapper {
  import com.fasterxml.jackson.databind.ObjectMapper
  import com.fasterxml.jackson.module.scala.DefaultScalaModule

  private val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def write(obj: AnyRef): String = mapper.writeValueAsString(obj)
  def write(w: Writer, obj:AnyRef): Unit = mapper.writeValue(w, obj)
  def read[T](jsonString: String, cls: Class[T]): T = mapper.readValue(jsonString, cls)
}
