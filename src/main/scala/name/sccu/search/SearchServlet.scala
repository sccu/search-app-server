package name.sccu.search

import javax.servlet.ServletConfig
import javax.servlet.http._

import com.typesafe.scalalogging.{Logger, StrictLogging}
import name.sccu.utils.stopwatch
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.LBHttpSolrClient
import org.slf4j.LoggerFactory


class SearchServlet extends HttpServlet with StrictLogging {
  protected val transactionLog = Logger(LoggerFactory.getLogger("transaction-log"))
  var searchHandler: SearchHandler = _
  var solrClient: SolrClient = _

  override def init() = {
    logger.info(classOf[SearchServlet].toString + " started!!")
    initSearchHandler
    initSolrClient
  }

  def initSearchHandler: Unit = {
    val resourceFilePath = System.getProperty("searchHandler", "/search-handler.sc")
    logger.info("searchHandler=" + resourceFilePath)
    searchHandler = ScalaInterpreter.interpretResourceFile(resourceFilePath)
    logger.info("Complete to interpret " + resourceFilePath)
  }

  def initSolrClient: Unit = {
    logger.info("initSolrClient")
    logger.debug("getServletConfig=" + getServletConfig)
    logger.debug("getServletContext=" + getServletContext)
    solrClient = getServletContext.getAttribute("solr-client") match {
      case null => {
        logger.debug("Creating new SolrClient...")
        new LBHttpSolrClient(searchHandler.solrUrls: _*)
      }
      case obj => {
        logger.debug("Get SolrClient from ServletContext.")
        obj.asInstanceOf[SolrClient]
      }
    }
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
      logger.debug("Invoking validateRequet().")
      ctx.violations = searchHandler.validateRequest(request)

      if (ctx.violations.isEmpty) {
        ctx.userQuery = request.getParameter("q")
        logger.debug("Invoking escapeQuery().")
        ctx.escapedQuery = searchHandler.escapeQuery(ctx.userQuery)
        logger.debug("Invoking buildSolrQuery().")
        ctx.solrQuery = searchHandler.buildSolrQuery(ctx.escapedQuery, request)
        val (_, searchTime) = stopwatch {
          logger.debug("Invoking search().")
          ctx.solrResponse = searchHandler.search(solrClient, ctx)
        }
        ctx.searchTime = searchTime
      }

      logger.debug("Invoking sendResponse().")
      searchHandler.sendResponse(ctx)
    }
    ctx.overallTime = overallTime

    logger.debug("Invoking writeLog().")
    val logString = searchHandler.writeLog(ctx)
    transactionLog.info(logString)
  }
}



