package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.{JsError, JsString, Json}

class ChangeWhichTaxYearsSpec extends WordSpec with MustMatchers with PropertyChecks with OptionValues with ModelGenerators {

  "ChangeWhichTaxYears" must {

    "deserialise valid values" in {

      val gen = arbitrary[ChangeWhichTaxYears]

      forAll(gen) {
        changeWhichTaxYears =>

          JsString(changeWhichTaxYears.toString).validate[ChangeWhichTaxYears].asOpt.value mustEqual changeWhichTaxYears
      }
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!ChangeWhichTaxYears.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[ChangeWhichTaxYears] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {

      val gen = arbitrary[ChangeWhichTaxYears]

      forAll(gen) {
        changeWhichTaxYears =>

          Json.toJson(changeWhichTaxYears) mustEqual JsString(changeWhichTaxYears.toString)
      }
    }
  }
}
