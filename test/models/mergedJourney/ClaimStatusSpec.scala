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
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.libs.json.{JsString, JsSuccess, Json}

class ClaimStatusSpec extends SpecBase with ScalaCheckDrivenPropertyChecks with Generators {

  val testId = "testId"

  val testData: Map[ClaimStatus, String] = Map(
    ClaimCompleteCurrent         -> "ClaimCompleteCurrent",
    ClaimCompletePrevious        -> "ClaimCompletePrevious",
    ClaimCompleteCurrentPrevious -> "ClaimCompleteCurrentPrevious",
    ClaimSkipped                 -> "ClaimSkipped",
    ClaimPending                 -> "ClaimPending",
    ClaimStopped                 -> "ClaimStopped",
    ClaimNotChanged              -> "ClaimNotChanged",
    ClaimUnsuccessful            -> "ClaimUnsuccessful"
  )

  "ClaimStatus" must {
    testData.foreach { case (model, string) =>
      s"serialise $model correctly" in {
        Json.toJson(model) mustBe JsString(string)
      }
    }

    testData.foreach { case (model, string) =>
      s"deserialise $string correctly" in {
        Json.fromJson[ClaimStatus](JsString(string)) mustBe JsSuccess(model)
      }
    }

    "throw when given an invalid claim status string" in
      forAll(stringsExceptSpecificValues(testData.values.toSeq)) { invalidStr =>
        (the[IllegalArgumentException] thrownBy {
          Json.fromJson[ClaimStatus](JsString(invalidStr))
        } must have).message("Invalid claim status")
      }
  }

}
