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

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json._

class CheckboxSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues with ModelGenerators {

  "Checkbox" must {

    "deserialise valid values" in {

      val gen = arbitrary[Checkbox]

      forAll(gen) {
        checkbox =>

          JsString(checkbox.toString).validate[Checkbox].asOpt.value mustEqual checkbox
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!Checkbox.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[Checkbox] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = arbitrary[Checkbox]

      forAll(gen) {
        checkbox =>

          Json.toJson(checkbox) mustEqual JsString(checkbox.toString)
      }
    }
  }
}
