package name.sccu.search

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.CoreContainer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

import scala.sys.process._

class SearchServletIntegrationTest extends FlatSpec with BeforeAndAfterAll {

  private val jetty: Server = new Server(sys.props.get("test.servlet.port").get.toInt)

  override def beforeAll() = {
    super.beforeAll()
    initializeSearchAppServer()
  }

  override def afterAll() = {
    jetty.stop()
    jetty.join()
  }

  private def initializeSolrServer(solrHome: String, coreName: String): SolrClient = {
    System.setProperty("solr.solr.home", solrHome)
    val index = new File(solrHome + "/" + coreName + "/data/index/")
    if (index.exists()) {
      FileUtils.cleanDirectory(index)
    }
    val tlog = new File(solrHome + "/" + coreName + "/data/tlog/")
    if (tlog.exists()) {
      FileUtils.cleanDirectory(tlog)
    }
    val coreContainer = new CoreContainer(solrHome)
    coreContainer.load()

    val solr = new EmbeddedSolrServer(coreContainer, coreName)
    solr.deleteByQuery("*:*")
    solr.commit()
    solr
  }

  private def initializeSearchAppServer(): Unit = {
    val handler = new ServletHandler
    handler.addServletWithMapping(classOf[SearchServlet], "/*")
    jetty.setHandler(handler)
    jetty.start()
    val solr = initializeSolrServer("solr-it", "poi")
    handler.getServletContext.setAttribute("solr-client", solr);
  }

  "ScalaTest" should "run successfully." in {
    val resp = Seq("curl", "-s", "http://localhost:8086/search/poi?q=함흥+냉면").!!
    val json = JsonMapper.read(resp, classOf[Map[String, Any]])
    val numFound = json.get("response").get.asInstanceOf[Map[String, Any]].
      get("num_found").get.asInstanceOf[Int]
    assert(numFound == 0)
  }

}


