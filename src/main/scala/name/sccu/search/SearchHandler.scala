package name.sccu.search

import javax.servlet.http.HttpServletRequest

import name.sccu.utils.alternatives
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.{SolrQuery, SolrClient}

trait SearchHandler {
  def solrUrls: Seq[String]

  def coreName: String

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

  def acceptRequest(ctx: SearchContext): SearchContext = ???

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

  def analyzeQuery(ctx: SearchContext): SearchContext = ???

  def buildSolrQuery(q: String, req: HttpServletRequest): SolrQuery = {
    val params = req.getParameterMap
    val solrQuery = new SolrQuery

    val start = params.getOrDefault("start", Array("0"))(0).toInt
    solrQuery.setStart(start)
    val rows = params.getOrDefault("rows", Array("10"))(0).toInt
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
