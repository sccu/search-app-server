package name.sccu.search

sealed trait Status {
  def code: Int
  def msg: String
}

object Success extends Status {
  val code: Int = 0
  val msg: String = "Success"
}

trait SearchException extends Throwable with Status

case class BadRequestException(msg: String) extends SearchException {
  val code: Int = 200
}

