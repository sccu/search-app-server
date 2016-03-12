import javax.servlet.http.HttpServletRequest

import name.sccu.search.SearchHandler
import org.apache.solr.client.solrj.SolrQuery

object MySearchHandler extends SearchHandler {
  // CAUTION: DO NOT set true in production.
  override val reloadForEveryRequest = true

  val solrUrls = Seq("http://localhost:16101/solr/")

  val coreName = "poi"

  override val fieldList: Seq[String] = Seq(
    "id",
    "name1",
    "name4",
    "nav_type",
    "nav",
    "phone_data",
    "srch_addr"
  )

  //  override val validations: Seq[Validation] = super.validations ++: Seq(
  //    Validation(_.getParameter("q") != null, _ => "'q' parameter not found"),
  //    Validation(_.getParameter("q").nonEmpty, _ => "Empty 'q' parameter")
  //  )

  override def buildSolrQuery(query: String, req: HttpServletRequest): SolrQuery = {
    val q = query match {
      case null | "" => "*"
      case x => x
    }
    val solrQuery = super.buildSolrQuery(q, req)

    val formula =
      s"""srch_nm:($q) OR
          |srch_addr:($q) OR
          |srch_phone:($q) OR
          |srch_cate:($q)""".
        stripMargin
    solrQuery.setQuery(formula)
    solrQuery.set("q.op", "AND")
    solrQuery
  }

  //  override def additionalLogFields(ctx: SearchContext): Seq[String] = Seq( )

}

