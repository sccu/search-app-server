package name.sccu.search

import java.io.FileReader

import org.scalatest.FlatSpec

import scala.io.Source

class ScalaInterpreterTest extends FlatSpec {

  behavior of "ScalaInterpreter"

  it should "interpret simple script." in {
    val str = ScalaInterpreter.interpretCode[String](""" "localhost" """)
    assert(str != null)
    assert(str equals "localhost")
  }

  it should "interpret SerchServletConfig implementations." in {
    val config = ScalaInterpreter.interpretCode[SearchHandler](
      """
        |import name.sccu.search.SearchServletConfig
        |
        |class Config extends SearchServletConfig {
        |  val solrServerUrls = Seq("http://localhost:8086")
        |}
        |new Config
      """.stripMargin
    )

    assert(config.solrServerUrls != null)
    assert(config.solrServerUrls equals Seq("http://localhost:8086"))
  }

  it should "interpret SerchServletConfig implementations in a resource file." in {
    val config = ScalaInterpreter.interpretResourceFile[SearchHandler]("/config.sc")

    assert(config.solrServerUrls != null)
    assert(config.solrServerUrls equals Seq("http://localhost:16101/solr/"))
  }
}
