package name.sccu.search

import javax.servlet.http.HttpServletRequest

import org.apache.solr.client.solrj.{SolrQuery, SolrClient}

/**
* Created by sccu on 2016. 3. 9..
*/
trait SearchHandler {
  def solrServerUrls: Seq[String]

  def coreName: String

  def buildSearchFormula(req: HttpServletRequest): String

  def reloadForEveryRequest = false

  def fieldList: Seq[String] = Seq("*")

  def validators: Seq[Validator]

  def validateRequest(request: HttpServletRequest): Seq[String] = {
    for {
      validator <- validators if !validator.cond(request)
    } yield validator.msg(request)
  }

  def acceptRequest(ctx: SearchContext): SearchContext = ???

  def escapeQuery(q: String): String = {
    q.map {
      case '(' => "\\("
      case ')' => "\\)"
      case c: Char => s"$c"
    }.mkString
  }

  def analyzeQuery(ctx: SearchContext): SearchContext = ???

  def search(solrClient: SolrClient, ctx: SearchContext): Unit = {
    val solrQuery = new SolrQuery(buildSearchFormula(ctx.req))
    solrQuery.set("q.op", "AND")
    solrQuery.setFields(fieldList: _*)

    val resp = solrClient.query(coreName, solrQuery)

    val result = Map(
      "header" -> Map(
        "uri" -> Seq(ctx.req.getRequestURI, ctx.req.getQueryString).filter(null !=).mkString("?"),
        "search_formula" -> solrQuery.getQuery),
      "response" -> Map("num_found" -> resp.getResults.getNumFound, "docs" -> resp.getResults))
    ctx.searchResult = result
  }

  def processResponse(ctx: SearchContext): SearchContext = ctx

  def sendResult(ctx: SearchContext): Unit = {
    ctx.resp.setContentType("application/json")
    ctx.resp.setCharacterEncoding("UTF-8")
    JsonMapper.write(ctx.resp.getWriter, ctx.searchResult)
  }

  def logResult(ctx: SearchContext): SearchContext = ctx

}
