package name.sccu.search

import javax.servlet.ServletConfig
import javax.servlet.http._

import org.apache.solr.client.solrj.{SolrClient, SolrQuery}
import org.apache.solr.client.solrj.impl.LBHttpSolrClient

class SearchServlet extends HttpServlet {
  var searchHandler: SearchHandler = _
  var solrClient: LBHttpSolrClient = _

  override def init(config: ServletConfig) = {
    initSearchHandler
    initSolrClient
  }

  def initSearchHandler: Unit = {
    val resourceFilePath = System.getProperty("searchHandler", "/search_handler.sc")
    searchHandler = ScalaInterpreter.interpretResourceFile(resourceFilePath)
  }

  def initSolrClient: Unit = {
    solrClient = new LBHttpSolrClient(searchHandler.solrServerUrls: _*)
  }

  def handleException(e: SearchException, ctx: SearchContext) = {
    ctx.resp.sendError(e.code, e.msg)
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    if (searchHandler.reloadForEveryRequest) {
      initSearchHandler
      initSolrClient
    }

    val ctx = SearchContext(request, response)
    ctx.violations = searchHandler.validateRequest(request)

    if (ctx.violations.isEmpty) {
      searchHandler.escapeQuery(request.getParameter("q"))
      searchHandler.search(solrClient, ctx)
    }

    searchHandler.sendResult(ctx)
    searchHandler.logResult(ctx)

  }
}



