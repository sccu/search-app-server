package name.sccu.search.analysis

/**
  * Created by sccu on 2016. 3. 22..
  */
trait QueryAnalyzer {

  def analyze(q: String): AnalysisOutput
}
