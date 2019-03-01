/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import org.joda.time.LocalDate
import play.api.libs.json._

case class Employment(name: String,
                      startDate: LocalDate,
                      endDate: Option[LocalDate])

object Employment {

  val dateFormat: String = "yyyy-MM-dd"

  implicit val dateTimeWriter: Writes[LocalDate] =
    JodaWrites.jodaLocalDateWrites(dateFormat)

  implicit val dateTimeJsReader: Reads[LocalDate] =
    JodaReads.jodaLocalDateReads(dateFormat)

  implicit val reads: Reads[Employment] =
    Json.format[Employment]

  implicit val listReads: Reads[Seq[Employment]] =
    (__ \ "data" \ "employments").read(Reads.seq[Employment])
}
