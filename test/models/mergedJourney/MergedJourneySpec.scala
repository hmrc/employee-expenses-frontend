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

import base.SpecBase
import play.api.libs.json.{JsObject, JsSuccess, Json}

import java.time.Instant

class MergedJourneySpec extends SpecBase {

  val testId = "testId"
  val testData: MergedJourney = MergedJourney(
    testId,
    JourneySkipped,
    JourneyPending,
    JourneyComplete
  )

  val testJson: JsObject = Json.obj(
    "internalId" -> testId,
    "wfh" -> "JourneySkipped",
    "psubs" -> "JourneyPending",
    "fre" -> "JourneyComplete",
    "lastUpdated" -> Json.obj(
      "$date" -> Json.obj("$numberLong" -> testData.lastUpdated.toEpochMilli.toString)
    )
  )

  "MergedJourney" must {
    "serialise correctly" in {
      Json.toJson(testData) mustBe testJson
    }

    "deserialise correctly" in {
      Json.fromJson[MergedJourney](testJson).map(_.copy(lastUpdated = testData.lastUpdated)) mustBe JsSuccess(testData)
      //there is a minor loss of accuracy when deserialising hence the lastUpdated change
    }
  }
}
