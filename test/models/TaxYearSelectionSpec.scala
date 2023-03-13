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
import models.TaxYearSelection._
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.time.TaxYear
import viewmodels.RadioCheckboxOption

class TaxYearSelectionSpec extends SpecBase with MockitoSugar {

  "TaxYearSelection getTaxYear" must {
    "return correct tax year in 'YYYY' format " in {
      val taxYear = TaxYearSelection.CurrentYear

      TaxYearSelection.getTaxYear(taxYear) mustBe TaxYear.current.startYear
    }

    "return next years tax year in YYYY format" in {
      val taxYear = TaxYearSelection.NextYear

      TaxYearSelection.getTaxYear(taxYear) mustBe TaxYear.current.next.startYear
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

    "throw exception when getTaxYear is given an invalid tax year" in {
      val thrown = intercept[IllegalArgumentException] {
        TaxYearSelection.getTaxYear(mock[TaxYearSelection])
      }
      thrown.getMessage mustBe "Invalid tax year selected"
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

    "return correct taxYearPeriod" in {
      TaxYearSelection.getTaxYearPeriod(TaxYear.current.startYear) mustBe CurrentYear
      TaxYearSelection.getTaxYearPeriod(TaxYear.current.back(1).startYear) mustBe CurrentYearMinus1
      TaxYearSelection.getTaxYearPeriod(TaxYear.current.back(2).startYear) mustBe CurrentYearMinus2
      TaxYearSelection.getTaxYearPeriod(TaxYear.current.back(3).startYear) mustBe CurrentYearMinus3
      TaxYearSelection.getTaxYearPeriod(TaxYear.current.back(4).startYear) mustBe CurrentYearMinus4
    }

    "throw exception when getTaxYearPeriod is given an invalid tax year" in {
      val thrown = intercept[IllegalArgumentException] {
        TaxYearSelection.getTaxYearPeriod(0)
      }
      thrown.getMessage mustBe "Invalid tax year selected"
    }
  }

  "taxYearStartString" must {
    "return the year the tax year started as a String" in {
      val currentTaxYear = TaxYear.current.startYear
      for (yearsBack <- 0 to 4) {
        taxYearStartString(yearsBack) mustBe (currentTaxYear - yearsBack).toString
      }
    }
  }

  "taxYearEndString" must {
    "return the year the tax year ended as a String" in {
      val currentTaxYear = TaxYear.current.finishYear
      for (yearsBack <- 0 to 4) {
        taxYearEndString(yearsBack) mustBe (currentTaxYear - yearsBack).toString
      }
    }
  }
}
