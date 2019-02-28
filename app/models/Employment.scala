package models

import org.joda.time.LocalDate
import play.api.libs.json._

case class Employment(name: String,
                      startDate: LocalDate,
                      endDate: Option[LocalDate])

object Employment {
  implicit val format: Format[Employment] = Json.format[Employment]
}
