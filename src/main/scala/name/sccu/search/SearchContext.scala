package name.sccu.search

import scala.collection._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import name.sccu.search.analysis.AnalysisOutput
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse

case class SearchContext(req: HttpServletRequest, resp: HttpServletResponse) {
  var analysisOutput: AnalysisOutput = _

  var userQuery: String = _

  var overallTime: Long = _

  /**
    * search time in milliseconds
    */
  var searchTime: Long = _

  def searchResponse: Map[String, Any] = {
    val map = mutable.LinkedHashMap[String, Any]()
    val header = mutable.LinkedHashMap[String, Any]()
    header += ("uri" -> Seq(req.getRequestURI, req.getQueryString).filter(null !=).mkString("?"))

    if (solrQuery != null) {
      header += ("search_formula" -> solrQuery.getQuery)
    }
    map += ("header" -> header)

    if (violations.isEmpty) {
      map += ("response" -> Map(
        "num_found" -> solrResponse.getResults.getNumFound,
        "docs" -> solrResponse.getResults))
    } else {
      map += ("violations" -> violations)
    }

    map
  }

  var escapedQuery: String = _

  var solrQuery: SolrQuery = _

  var violations: Seq[String] = _

  var solrResponse: QueryResponse = _

}
