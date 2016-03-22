package name.sccu.search

import javax.servlet.http.HttpServletRequest

import name.sccu.search.analysis.{AnalysisOutput, QueryAnalyzer}
import name.sccu.utils.alternatives
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.{SolrClient, SolrQuery}

trait SearchHandler {
  def solrUrls: Seq[String]

  def coreName: String

  def analysisFieldType: String

  def reloadForEveryRequest = false

  def fieldList: Seq[String] = Seq("*")

  def validations: Seq[Validation] = Seq(
    Validation(
      condition = req => { val p = req.getParameter("start"); p == null || p.matches("[0-9]+")},
      message = req => s"""Invalid 'start' parameter: "${req.getParameter("start")}""""
    ),
    Validation(
      condition = req => { val p = req.getParameter("rows"); p == null || p.matches("[0-9]+")},
      message = req => s"""Invalid 'rows' parameter: "${req.getParameter("rows")}""""
    )
  )

  def validateRequest(request: HttpServletRequest): Seq[String] = {
    for {
      validation <- validations if !validation.condition(request)
    } yield validation.message(request)
  }

  /**
    * Escape and preprocess a query string.
    *
    * @param q query string
    * @return preprocessed query string
    */
  def escapeQuery(q: String): String = {
    alternatives(q, "").
      map {
        case '(' => "\\("
        case ')' => "\\)"
        case '"' => "\\\""
        case '\t' => " "
        case '\r' => " "
        case '\n' => " "
        case c: Char => s"$c"
      }.mkString
  }

  def analyzeQuery(ctx: SearchContext, qa: QueryAnalyzer): AnalysisOutput = {
    qa.analyze(ctx.escapedQuery)
  }

  def buildSolrQuery(ctx: SearchContext): SolrQuery = {
    val q = ctx.escapedQuery
    val solrQuery = new SolrQuery

    val start = alternatives(ctx.req.getParameter("start"), "0").toInt
    solrQuery.setStart(start)
    val rows = alternatives(ctx.req.getParameter("rows"), "10").toInt
    solrQuery.setRows(rows)
    solrQuery.setFields(fieldList: _*)

    solrQuery
  }

  def search(solrClient: SolrClient, ctx: SearchContext): QueryResponse = {
    solrClient.query(coreName, ctx.solrQuery)
  }

  def processResponse(ctx: SearchContext): SearchContext = ctx

  def sendResponse(ctx: SearchContext): Unit = {
    ctx.resp.setContentType("application/json")
    ctx.resp.setCharacterEncoding("UTF-8")
    JsonMapper.write(ctx.resp.getWriter, ctx.searchResponse)
  }

  def additionalLogFields(ctx: SearchContext): Seq[String] = Seq()

  def writeLog(ctx: SearchContext): String = {
    if (ctx.violations.isEmpty) {
      Seq(
        "S",
        s"${ctx.overallTime}(${ctx.searchTime})",
        s"${ctx.solrResponse.getResults.getNumFound}",
        "--",
        s"${ctx.solrQuery.getStart}",
        s"${ctx.solrQuery.getRows}",
        s"${ctx.solrResponse.getResults.size}",
        ctx.escapedQuery match { case null | "" => "*" case x => x },
        "--",
        s"${additionalLogFields(ctx).mkString(" ")}"
      ).mkString(" ")
    }
    else {
      Seq(
        "F",
        s"${ctx.overallTime}",
        0,
        "--",
        "-",
        "-",
        "-",
        ctx.escapedQuery match { case null | "" => "*" case x => x },
        s"${ctx.violations.mkString("|")}"
      ).mkString(" ")
    }
  }

}
