package name.sccu.search

import javax.servlet.http.HttpServletRequest

case class Validation(condition: HttpServletRequest => Boolean, message: HttpServletRequest => String)
