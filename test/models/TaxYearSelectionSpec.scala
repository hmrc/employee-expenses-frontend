package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class TaxYearSelectionSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues with ModelGenerators {

  "TaxYearSelection" must {

    "deserialise valid values" in {

      val gen = arbitrary[TaxYearSelection]

      forAll(gen) {
        taxYearSelection =>

          JsString(taxYearSelection.toString).validate[TaxYearSelection].asOpt.value mustEqual taxYearSelection
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!TaxYearSelection.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[TaxYearSelection] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = arbitrary[TaxYearSelection]

      forAll(gen) {
        taxYearSelection =>

          Json.toJson(taxYearSelection) mustEqual JsString(taxYearSelection.toString)
      }
    }
  }
}
