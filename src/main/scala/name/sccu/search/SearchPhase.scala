package name.sccu.search

object SearchPhase extends Enumeration {
  val VALIDATING_REQUEST,
      ACCEPTING_REQUEST,
      PROCESSING_QUERY,
      ANALYZING_QUERY,
      BUILDING_SEARCH_FORMULA,
      SEARCHING,
      PROCESSING_RESPONSE,
      SENDING_RESULT,
      LOGGING = Value
}
