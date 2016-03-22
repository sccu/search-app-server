package name.sccu.search

import org.scalatest.FlatSpec

class ScalaInterpreterTest extends FlatSpec {

  behavior of "ScalaInterpreter"

  it should "interpret simple script." in {
    val str = ScalaInterpreter.interpretCode[String](""" "localhost" """)
    assert(str != null)
    assert(str equals "localhost")
  }

  it should "interpret SerchHandler implementations." in {
    val handler = ScalaInterpreter.interpretCode[SearchHandler](
      """
        |import name.sccu.search.SearchHandler
        |
        |object MySearchHandler extends SearchHandler {
        |  val solrUrls = Seq("http://localhost:8086")
        |  val coreName = "poi"
        |  val analysisFieldType = "text_ko"
        |}
      """.stripMargin
    )

    assert(handler.solrUrls != null)
    assert(handler.solrUrls equals Seq("http://localhost:8086"))
  }

}
