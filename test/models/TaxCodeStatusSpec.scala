/*
 * Copyright 2021 HM Revenue & Customs
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
import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class TaxCodeStatusSpec extends SpecBase with Matchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {


  "TaxCodeStatus" must {

    "deserialise valid values" in {

      val gen = arbitrary[TaxCodeStatus]

      forAll(gen) {
        taxCodeStatus =>

          JsString(taxCodeStatus.toString).validate[TaxCodeStatus].asOpt.value mustEqual taxCodeStatus
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!TaxCodeStatus.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[TaxCodeStatus] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = arbitrary[TaxCodeStatus]

      forAll(gen) {
        taxCodeStatus =>

          Json.toJson(taxCodeStatus) mustEqual JsString(taxCodeStatus.toString)
      }
    }
  }

}
