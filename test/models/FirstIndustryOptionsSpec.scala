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

import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}
import org.scalacheck.Arbitrary.arbitrary

class FirstIndustryOptionsSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with OptionValues {

  "FirstIndustryOptionsPage" must {

    "Deserialise valid values" in {

      val gen = Gen.oneOf(FirstIndustryOptions.values.toSeq)
      forAll(gen) {
        firstIndustryOptionsPage =>
          JsString(firstIndustryOptionsPage.toString).validate[FirstIndustryOptions].asOpt.value mustEqual firstIndustryOptionsPage
      }
    }

    "Fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!FirstIndustryOptions.values.map(_.toString).contains(_))
      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[FirstIndustryOptions] mustEqual JsError("error.invalid")
      }

    }

    "Serialise" in {

      val gen = Gen.oneOf(FirstIndustryOptions.values.toSeq)
      forAll(gen) {
        firstIndustryOptionsPage =>
          Json.toJson(firstIndustryOptionsPage) mustEqual JsString(firstIndustryOptionsPage.toString)

      }
    }
  }

}
