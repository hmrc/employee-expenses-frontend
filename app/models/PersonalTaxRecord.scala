/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.libs.functional.syntax._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class TaxCodeRecord(taxCode: String,
                         employerName: String,
                         startDate: LocalDate,
                         endDate: LocalDate,
                         payrollNumber: Option[String],
                         pensionIndicator: Boolean,
                         primary: Boolean)


object TaxCodeRecord {
  implicit val reads: Reads[TaxCodeRecord] = Json.format[TaxCodeRecord]

  implicit val listReads: Reads[Seq[TaxCodeRecord]] =
    (__ \ "data" \ "current").read(Reads.seq[TaxCodeRecord])

  implicit val writes: Writes[TaxCodeRecord] = (
    (__ \ "taxCode").write[String] and
    (__ \ "employerName").write[String] and
    (__ \ "startDate").write[LocalDate] and
    (__ \ "endDate").write[LocalDate] and
    (__ \ "payrollNumber").writeNullable[String] and
    (__ \ "pensionIndicator").write[Boolean] and
    (__ \ "primary").write[Boolean]
  )(unlift(TaxCodeRecord.unapply))

  implicit val writesList: Writes[Seq[TaxCodeRecord]] = Writes.seq[TaxCodeRecord]

  implicit val format: Format[Seq[TaxCodeRecord]] = Format(listReads, writesList)
}

case class PersonalTaxRecord(etag: ETag, taxCodeRecord: Seq[TaxCodeRecord])

object PersonalTaxRecord {
	implicit val format: Format[PersonalTaxRecord] = Json.format[PersonalTaxRecord]
}
