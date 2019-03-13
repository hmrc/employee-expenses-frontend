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

import base.SpecBase
import play.api.libs.json.{JsResultException, Json}

class IabdEditDataRequestSpec extends SpecBase {

  "IabdEditDataRequest" must {
    "parse json correctly" in {
      val iabdEditDataRequestJson = Json.parse(
        """
          |{
          |  "version": 1,
          |  "expensesData": {
          |     "employmentSequenceNumber": 0,
          |     "grossAmount": 100
          |  }
          |}
        """.stripMargin)

      val iabdEditDataRequest = iabdEditDataRequestJson.as[IabdEditDataRequest]

      iabdEditDataRequest.version mustBe 1
      iabdEditDataRequest.expensesData mustBe IabdUpdateData(0, 100)
    }

    "fail to bind when given invalid json" in {
      val iabdEditDataRequestJson = Json.parse(
        """
          |{
          |  "version": 1
          |}
        """.stripMargin)

      val parseError = intercept[JsResultException] {
        iabdEditDataRequestJson.as[IabdEditDataRequest]
      }

      parseError mustBe an[JsResultException]
    }
  }

}
