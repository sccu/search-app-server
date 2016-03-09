import javax.servlet.http.HttpServletRequest

import name.sccu.search.{SearchHandler, Validator}

object MySearchHandler extends SearchHandler {
  // CAUTION: DO NOT set true in production.
  override val reloadForEveryRequest = true

  val solrServerUrls = Seq("http://localhost:16101/solr/")

  val coreName = "poi"

  def buildSearchFormula(req: HttpServletRequest) = {
    val q = escapeQuery(Some(req.getParameter("q")).getOrElse("*"))
    s"""srch_nm:($q) OR
        |srch_addr:($q) OR
        |srch_phone:($q) OR
        |srch_cate:($q)""".
      stripMargin
  }

  override val fieldList: Seq[String] = Seq(
    "id",
    "name1",
    "name4",
    "nav_type",
    "nav",
    "phone_data",
    "srch_addr"
  )

  val validators: Seq[Validator] = Seq(
    Validator(_.getParameter("q") != null, _ => "'q' paramter not found"),
    Validator(_.getParameter("q").nonEmpty, _ => "Empty 'q' paramter")
  )

}

