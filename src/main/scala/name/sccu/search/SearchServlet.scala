package name.sccu.search

import javax.servlet.ServletConfig
import javax.servlet.http._

import com.typesafe.scalalogging.{Logger, StrictLogging}
import org.apache.solr.client.solrj.impl.LBHttpSolrClient
import org.slf4j.LoggerFactory

object stopwatch {
  def apply[T](block: => T): (T, Long) = {
    val start = System.nanoTime()
    val ret = block
    val end = System.nanoTime()
    (block, (end - start) / 1000000)
  }
}

object alternatives {
  def apply[T](values: T*): T = {
    val itr = values.iterator
    while (itr.hasNext) {
      val value = itr.next()
      if (value != null) {
        return value
      }
    }
    null.asInstanceOf[T]
  }
}

class SearchServlet extends HttpServlet with StrictLogging {
  protected val transactionLog = Logger(LoggerFactory.getLogger("transaction-log"))
  var searchHandler: SearchHandler = _
  var solrClient: LBHttpSolrClient = _

  override def init(config: ServletConfig) = {
    initSearchHandler
    initSolrClient
  }

  def initSearchHandler: Unit = {
    val resourceFilePath = System.getProperty("searchHandler", "/search-handler.sc")
    searchHandler = ScalaInterpreter.interpretResourceFile(resourceFilePath)
  }

  def initSolrClient: Unit = {
    solrClient = new LBHttpSolrClient(searchHandler.solrUrls: _*)
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
    val (_, overallTime) = stopwatch {
      ctx.violations = searchHandler.validateRequest(request)

      if (ctx.violations.isEmpty) {
        ctx.userQuery = request.getParameter("q")
        ctx.escapedQuery = searchHandler.escapeQuery(ctx.userQuery)
        ctx.solrQuery = searchHandler.buildSolrQuery(ctx.escapedQuery, request)
        val (_, searchTime) = stopwatch {
          ctx.solrResponse = searchHandler.search(solrClient, ctx)
        }
        ctx.searchTime = searchTime
      }

      searchHandler.sendResponse(ctx)
    }
    ctx.overallTime = overallTime

    val logString = searchHandler.writeLog(ctx)
    transactionLog.info(logString)
  }
}



