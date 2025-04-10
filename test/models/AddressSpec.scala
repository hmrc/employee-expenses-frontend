/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalatest.OptionValues
import play.api.libs.json.Json

class AddressSpec extends SpecBase with OptionValues {

  "Address" must {
    "serialize correctly" in {
      Json.toJson[Address](address) mustBe validAddressJson
    }

    "deserialize correctly" in {
      val address = Json.parse(validAddressJson.toString())

      address.as[Address].line1.value mustBe "6 Howsell Road"
      address.as[Address].line2.value mustBe "Llanddew"
      address.as[Address].line3.value mustBe "Line 3"
      address.as[Address].line4.value mustBe "Line 4"
      address.as[Address].line5.value mustBe "Line 5"
      address.as[Address].postcode.value mustBe "DN16 3FB"
      address.as[Address].country.value mustBe "GREAT BRITAIN"
    }

    "build label with one value" in {

      val address = Address(Some("Test"), None, None, None, None, None, None)

      Address.asLabel(address) mustBe "Test"
    }

    "build label with two values" in {

      val address = Address(Some("Test"), None, None, None, None, Some("AB12CD"), None)

      Address.asLabel(address) mustBe "Test, AB12CD"
    }

    "build label with all values" in {

      val address = Address(
        Some("Test Line 1"),
        Some("Test Line 2"),
        Some("Test Line 3"),
        Some("Test Line 4"),
        Some("Test Line 5"),
        Some("AB12CD"),
        Some("GREAT BRITAIN")
      )

      Address.asLabel(
        address
      ) mustBe "Test Line 1, Test Line 2, Test Line 3, Test Line 4, Test Line 5, AB12CD, GREAT BRITAIN"

    }
  }

}
