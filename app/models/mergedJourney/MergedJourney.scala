/*
 * Copyright 2024 HM Revenue & Customs
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

package models.mergedJourney

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class MergedJourney(
    internalId: String,
    wfh: ClaimStatus,
    psubs: ClaimStatus,
    fre: ClaimStatus,
    lastUpdated: Instant = Instant.now()
) {
  lazy val claimList: Seq[ClaimStatus] = Seq(wfh, psubs, fre).filterNot(_.equals(ClaimSkipped))
}

object MergedJourney {
  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat
  implicit val format: Format[MergedJourney]  = Json.format[MergedJourney]
}
