package name.sccu.search

import javax.servlet.http.HttpServletRequest

case class Validator(cond: HttpServletRequest => Boolean, msg: HttpServletRequest => String)
