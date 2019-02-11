package models

import base.SpecBase
import models.TaxYearSelection._
import uk.gov.hmrc.time.TaxYear
import viewmodels.RadioCheckboxOption

class TaxYearSelectionSpec extends SpecBase {

  "TaxYearSelection getTaxYear" must {
    "return correct tax year in 'YYYY' format " in {
      val taxYear = TaxYearSelection.CurrentYear

      TaxYearSelection.getTaxYear(taxYear) mustBe TaxYear.current.startYear

    }

    "return current year minus 1 tax year in 'YYYY' format " in {
      val taxYear = TaxYearSelection.CurrentYearMinus1

      TaxYearSelection.getTaxYear(taxYear) mustBe TaxYear.current.back(1).startYear
    }

    "return current year minus 2 tax year in 'YYYY' format " in {
      val taxYear = TaxYearSelection.CurrentYearMinus2

      TaxYearSelection.getTaxYear(taxYear) mustBe TaxYear.current.back(2).startYear
    }

    "return current year minus 3 tax year in 'YYYY' format " in {
      val taxYear = TaxYearSelection.CurrentYearMinus3

      TaxYearSelection.getTaxYear(taxYear) mustBe TaxYear.current.back(3).startYear
    }

    "return current year minus 4 tax year in 'YYYY' format " in {
      val taxYear = TaxYearSelection.CurrentYearMinus4

      TaxYearSelection.getTaxYear(taxYear) mustBe TaxYear.current.back(4).startYear
    }

    "return a sequence of RadioCheckboxOption from options" in {
      val taxYearOptions: Seq[RadioCheckboxOption] = TaxYearSelection.options

      taxYearOptions.head.message.string mustBe s"6 April ${TaxYear.current.startYear} to 5 April ${TaxYear.current.finishYear} (the current tax year)"
      taxYearOptions(1).message.string mustBe s"6 April ${TaxYear.current.back(1).startYear} to 5 April ${TaxYear.current.back(1).finishYear}"
      taxYearOptions(2).message.string mustBe s"6 April ${TaxYear.current.back(2).startYear} to 5 April ${TaxYear.current.back(2).finishYear}"
      taxYearOptions(3).message.string mustBe s"6 April ${TaxYear.current.back(3).startYear} to 5 April ${TaxYear.current.back(3).finishYear}"
      taxYearOptions(4).message.string mustBe s"6 April ${TaxYear.current.back(4).startYear} to 5 April ${TaxYear.current.back(4).finishYear}"
    }

    "return the correct values" in {
      val taxYearValues: Seq[TaxYearSelection] = TaxYearSelection.values

      taxYearValues.head mustBe CurrentYear
      taxYearValues(1) mustBe CurrentYearMinus1
      taxYearValues(2) mustBe CurrentYearMinus2
      taxYearValues(3) mustBe CurrentYearMinus3
      taxYearValues(4) mustBe CurrentYearMinus4
    }
  }
}
