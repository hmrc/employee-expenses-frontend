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

package utils

import base.SpecBase
import models.EmployerContribution.SomeContribution
import models.{Address, EmployerContribution, FirstIndustryOptions, FourthIndustryOptions, SecondIndustryOptions, TaxYearSelection, ThirdIndustryOptions, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import pages._
import pages.authenticated.{TaxYearSelectionPage, YourAddressPage}

class CheckYourAnswersHelperSpec extends SpecBase with PropertyChecks {

  private def helper(ua: UserAnswers) = new CheckYourAnswersHelper(ua)

  "Industry type" when {
    "first industry list" must {
      "display the correct label and answer" in {
        forAll(Gen.oneOf(FirstIndustryOptions.values)) {
          industry =>
            if (industry != FirstIndustryOptions.NoneOfAbove) {
              val ua = emptyUserAnswers.set(FirstIndustryOptionsPage, industry).success.value
              helper(ua).industryType.get.label mustBe "industryType.checkYourAnswersLabel"
              helper(ua).industryType.get.answer mustBe s"firstIndustryOptions.${industry.toString}"
            }
        }
      }
    }

    "second industry list" must {
      "display the correct label and answer" in {
        forAll(Gen.oneOf(SecondIndustryOptions.values)) {
          industry =>
            if (industry != SecondIndustryOptions.NoneOfAbove) {
              val ua = emptyUserAnswers.set(SecondIndustryOptionsPage, industry).success.value
              helper(ua).secondaryIndustryList.get.label mustBe "industryType.checkYourAnswersLabel"
              helper(ua).secondaryIndustryList.get.answer mustBe s"secondIndustryOptions.${industry.toString}"
            }
        }
      }
    }

    "third industry list" must {
      "display the correct label and answer" in {
        forAll(Gen.oneOf(ThirdIndustryOptions.values)) {
          industry =>
            if (industry != ThirdIndustryOptions.NoneOfAbove) {
              val ua = emptyUserAnswers.set(ThirdIndustryOptionsPage, industry).success.value
              helper(ua).thirdIndustryList.get.label mustBe "industryType.checkYourAnswersLabel"
              helper(ua).thirdIndustryList.get.answer mustBe s"thirdIndustryOptions.${industry.toString}"
            }
        }
      }
    }

    "fourth industry list" must {
      "display the correct label and answer" in {
        forAll(Gen.oneOf(FourthIndustryOptions.values)) {
          industry =>
            if (industry != FourthIndustryOptions.NoneOfAbove) {
              val ua = emptyUserAnswers.set(FourthIndustryOptionsPage, industry).success.value
              helper(ua).fourthIndustryList.get.label mustBe "industryType.checkYourAnswersLabel"
              helper(ua).fourthIndustryList.get.answer mustBe s"fourthIndustryOptions.${industry.toString}"
            }
        }
      }
    }
  }

  "employerContribution" must {
    "display the correct label and answer" in {
      forAll(Gen.oneOf(EmployerContribution.values)) {
        contribution =>
          val ua = emptyUserAnswers.set(EmployerContributionPage, contribution).success.value
          helper(ua).employerContribution.get.label mustBe "employerContribution.checkYourAnswersLabel"
          helper(ua).employerContribution.get.answer mustBe s"employerContribution.${contribution.toString}"
      }
    }
  }

  "expensesEmployerPaid" must {
    "display the correct label and answer" in {
      forAll(Gen.posNum[Int]) {
        amount =>
          val ua = emptyUserAnswers.set(EmployerContributionPage, SomeContribution).success.value
          val ua2 = ua.set(ExpensesEmployerPaidPage, amount).success.value
          helper(ua2).expensesEmployerPaid.get.label mustBe "expensesEmployerPaid.checkYourAnswersLabel"
          helper(ua2).expensesEmployerPaid.get.answer mustBe s"$amount"
      }
    }
  }

  "taxYearSelection" must {
    "display the correct label and answer" in {
      val taxYears = Gen.nonEmptyContainerOf[Seq, TaxYearSelection](Gen.oneOf(
        TaxYearSelection.CurrentYear,
        TaxYearSelection.CurrentYearMinus1,
        TaxYearSelection.CurrentYearMinus2,
        TaxYearSelection.CurrentYearMinus3,
        TaxYearSelection.CurrentYearMinus4
      ))

      forAll(taxYears) {
        taxYearSeq =>
          val ua = emptyUserAnswers.set(TaxYearSelectionPage, taxYearSeq).success.value
          helper(ua).taxYearSelection.get.label mustBe "taxYearSelection.checkYourAnswersLabel"
          helper(ua).taxYearSelection.get.answer mustBe taxYearSeq.map {
            taxYear =>
              messages(s"taxYearSelection.$taxYear",
                TaxYearSelection.getTaxYear(taxYear).toString,
                (TaxYearSelection.getTaxYear(taxYear) + 1).toString
              )
          }.mkString("<br>")
      }
    }
  }

  "yourAddress" when {
    "correct" must {
      "display the correct label, answer, and message args" in {
        val ua = emptyUserAnswers.set(YourAddressPage, true).success.value
        val ua2 = ua.set(CitizenDetailsAddress, address).success.value
        helper(ua2).yourAddress.get.label mustBe "yourAddress.checkYourAnswersLabel"
        helper(ua2).yourAddress.get.answer mustBe "site.yes"
        helper(ua2).yourAddress.get.messageArgs.head mustBe Address.asString(address)
      }
    }

    "incorrect" must {
      "display the correct label, answer, and message args" in {
        val ua = emptyUserAnswers.set(YourAddressPage, false).success.value
        val ua2 = ua.set(CitizenDetailsAddress, address).success.value
        helper(ua2).yourAddress.get.label mustBe "yourAddress.checkYourAnswersLabel"
        helper(ua2).yourAddress.get.answer mustBe "site.no"
        helper(ua2).yourAddress.get.messageArgs.head mustBe Address.asString(address)
      }
    }
  }


}
