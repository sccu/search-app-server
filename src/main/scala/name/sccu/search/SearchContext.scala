package name.sccu.search

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

case class SearchContext(req: HttpServletRequest, resp: HttpServletResponse) {
  var violations: Seq[String] = _

  type SearchResults = Map[String, Any]

  private var _searchResult: SearchResults = _

  def searchResult = _searchResult

  def searchResult_=(value: SearchResults): Unit = _searchResult = value

}
