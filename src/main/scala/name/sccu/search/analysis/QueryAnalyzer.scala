package name.sccu.search.analysis

import name.sccu.search.JsonMapper
import org.apache.solr.client.solrj.{SolrClient, SolrQuery}
import org.apache.solr.common.util.NamedList
import collection.JavaConversions._

class QueryAnalyzer(solrClient: SolrClient, coreName: String, fieldType: String) {

  def extractTerms(q: String): Seq[String] = {
    val solrQuery = new SolrQuery
    solrQuery.setRequestHandler("/analysis/field")
    solrQuery.set("analysis.query", q)
    solrQuery.set("analysis.fieldtype", fieldType)

    val resp = solrClient.query(coreName, solrQuery)

    val analysis = resp.getResponse.getVal(1).asInstanceOf[NamedList[AnyRef]]
    val fieldTypes = analysis.getVal(0).asInstanceOf[NamedList[AnyRef]]
    val textKo = fieldTypes.getVal(0).asInstanceOf[NamedList[AnyRef]]
    val indexTerms = textKo.get("query").asInstanceOf[NamedList[AnyRef]]
    val termList = indexTerms.getVal(indexTerms.size - 1).asInstanceOf[java.util.List[NamedList[AnyRef]]]
    termList.toIndexedSeq.map(_.get("text").asInstanceOf[String])
  }

  def analyze(q: String): AnalysisOutput = {
    val terms = extractTerms(q)
    AnalysisOutput(terms)
  }

}
