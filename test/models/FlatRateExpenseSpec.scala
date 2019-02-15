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
import play.api.libs.json.{JsValue, Json}

class FlatRateExpenseSpec extends SpecBase {


  "FlatRateExpense" must {
    "return a flatRateExpense when passed valid Json" in {
      val flatRateExpense = validFlatRateJson.as[FlatRateExpense]

      flatRateExpense mustBe FlatRateExpense(
        nino = Some("EM329013"),
        taxYear = Some(2016),
        grossAmount = Some(60),
        netAmount = None
      )
    }
  }

  val validFlatRateJson: JsValue = Json.parse(
    """
      |   {
      |        "nino": "EM329013",
      |        "sequenceNumber": 201600003,
      |        "taxYear": 2016,
      |        "type": 56,
      |        "source": 26,
      |        "grossAmount": 60,
      |        "receiptDate": null,
      |        "captureDate": null,
      |        "typeDescription": "Flat Rate Job Expenses",
      |        "netAmount": null
      |   }
      |""".stripMargin)

}