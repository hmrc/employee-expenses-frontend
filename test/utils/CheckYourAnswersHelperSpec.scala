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
import models.AlreadyClaimingFREDifferentAmounts._
import models._
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import pages._
import pages.authenticated._

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

    "fifth industry list" must {
      "display the correct label and answer" in {
        forAll(Gen.oneOf(FifthIndustryOptions.values)) {
          industry =>
            if (industry != FifthIndustryOptions.NoneOfAbove) {
              val ua = emptyUserAnswers.set(FifthIndustryOptionsPage, industry).success.value
              helper(ua).fifthIndustryList.get.label mustBe "industryType.checkYourAnswersLabel"
              helper(ua).fifthIndustryList.get.answer mustBe s"fifthIndustryOptions.${industry.toString}"
            }
        }
      }
    }
  }

  "expensesEmployerPaid" must {
    "display the correct label and answer" in {
      forAll(Gen.posNum[Int]) {
        amount =>
          val ua = emptyUserAnswers.set(EmployerContributionPage, EmployerContribution.YesEmployerContribution).success.value
          val ua2 = ua.set(ExpensesEmployerPaidPage, amount).success.value
          helper(ua2).expensesEmployerPaid.get.label mustBe "expensesEmployerPaid.checkYourAnswersLabel"
          helper(ua2).expensesEmployerPaid.get.answer mustBe s"£$amount"
      }
    }
  }
  
  "alreadyClaimingFRESameAmount" must {
    "display the correct label and answer for no change" in {
      val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.NoChange).success.value
      helper(ua).alreadyClaimingFRESameAmount.get.label mustBe "alreadyClaimingFRESameAmount.checkYourAnswersLabel"
      helper(ua).alreadyClaimingFRESameAmount.get.answer mustBe s"alreadyClaimingFRESameAmount.noChange"
    }

    "display the correct label and answer for remove" in {
      val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.Remove).success.value
      helper(ua).alreadyClaimingFRESameAmount.get.label mustBe "alreadyClaimingFRESameAmount.checkYourAnswersLabel"
      helper(ua).alreadyClaimingFRESameAmount.get.answer mustBe s"alreadyClaimingFRESameAmount.remove"
    }
  }

  "alreadyClaimingFREDifferentAmounts" when {
    "Answered as 'change'" must {
      "display the correct label and answer" in {
        val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, Change).success.value
        helper(ua).alreadyClaimingFREDifferentAmounts.get.label mustBe "alreadyClaimingFREDifferentAmounts.checkYourAnswersLabel"
        helper(ua).alreadyClaimingFREDifferentAmounts.get.answer mustBe s"alreadyClaimingFREDifferentAmounts.change"
      }
    }

    "Answered as 'noChange'" must {
      "display the correct label and answer" in {
        val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, NoChange).success.value
        helper(ua).alreadyClaimingFREDifferentAmounts.get.label mustBe "alreadyClaimingFREDifferentAmounts.checkYourAnswersLabel"
        helper(ua).alreadyClaimingFREDifferentAmounts.get.answer mustBe s"alreadyClaimingFREDifferentAmounts.noChange"
      }
    }

    "Answered as 'remove'" must {
      "display the correct label and answer" in {
        val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, Remove).success.value
        helper(ua).alreadyClaimingFREDifferentAmounts.get.label mustBe "alreadyClaimingFREDifferentAmounts.checkYourAnswersLabel"
        helper(ua).alreadyClaimingFREDifferentAmounts.get.answer mustBe s"alreadyClaimingFREDifferentAmounts.remove"
      }
    }
  }

  "changeWhichTaxYears" must {
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
          val ua = emptyUserAnswers.set(ChangeWhichTaxYearsPage, taxYearSeq).success.value
          helper(ua).changeWhichTaxYears.get.label mustBe "changeWhichTaxYears.checkYourAnswersLabel"
          helper(ua).changeWhichTaxYears.get.answer mustBe taxYearSeq.map {
            taxYear =>
              messages(s"taxYearSelection.$taxYear",
                TaxYearSelection.getTaxYear(taxYear).toString,
                (TaxYearSelection.getTaxYear(taxYear) + 1).toString
              )
          }.mkString("<br>")
      }
    }
  }

  "removeFRECode" must {
    "display the correct label and answer" in {
      forAll(Gen.oneOf(TaxYearSelection.values)) {
        taxYear =>
          val ua = emptyUserAnswers.set(RemoveFRECodePage, taxYear).success.value
          helper(ua).removeFRECode.get.label mustBe "removeFRECode.checkYourAnswersLabel"
          helper(ua).removeFRECode.get.answer mustBe messages(s"taxYearSelection.$taxYear",
            TaxYearSelection.getTaxYear(taxYear).toString,
            (TaxYearSelection.getTaxYear(taxYear) + 1).toString
          )
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

  "employerContribution" when {
    "display the correct label and answer when 'True'" in {
          val ua = emptyUserAnswers.set(EmployerContributionPage, EmployerContribution.YesEmployerContribution).success.value
          helper(ua).employerContribution.get.label mustBe "employerContribution.checkYourAnswersLabel"
          helper(ua).employerContribution.get.answer mustBe "employerContribution.yesEmployerContribution"
    }

    "display the correct label and answer when 'False'" in {
      val ua = emptyUserAnswers.set(EmployerContributionPage, EmployerContribution.NoEmployerContribution).success.value
      helper(ua).employerContribution.get.label mustBe "employerContribution.checkYourAnswersLabel"
      helper(ua).employerContribution.get.answer mustBe "employerContribution.noEmployerContribution"
    }
  }

  "yourAddress" when {
    "correct" must {
      "display the correct label, answer, and message args" in {
        val ua = emptyUserAnswers.set(YourAddressPage, true).success.value
        val ua2 = ua.set(CitizenDetailsAddress, address).success.value
        helper(ua2).yourAddress.get.label mustBe "yourAddress.checkYourAnswersLabel"
        helper(ua2).yourAddress.get.answer mustBe "site.yes"
        helper(ua2).yourAddress.get.labelArgs.head mustBe Address.asString(address)
      }
    }

    "incorrect" must {
      "display the correct label, answer, and message args" in {
        val ua = emptyUserAnswers.set(YourAddressPage, false).success.value
        val ua2 = ua.set(CitizenDetailsAddress, address).success.value
        helper(ua2).yourAddress.get.label mustBe "yourAddress.checkYourAnswersLabel"
        helper(ua2).yourAddress.get.answer mustBe "site.no"
        helper(ua2).yourAddress.get.labelArgs.head mustBe Address.asString(address)
      }
    }
  }

  "yourEmployer" when {
    "correct" must {
      "display the correct label, answer, and message args" in {
        val ua1 = emptyUserAnswers.set(YourEmployerPage, true).success.value
        val ua2 = ua1.set(YourEmployerName, taiEmployment.head.name).success.value
        helper(ua2).yourEmployer.get.label mustBe "yourEmployer.checkYourAnswersLabel"
        helper(ua2).yourEmployer.get.answer mustBe "site.yes"
        helper(ua2).yourEmployer.get.labelArgs.head mustBe s"<p>${taiEmployment.head.name}</p>"
      }
    }

    "incorrect" must {
      "display the correct label, answer, and message args" in {
        val ua1 = emptyUserAnswers.set(YourEmployerPage, false).success.value
        val ua2 = ua1.set(YourEmployerName, taiEmployment.head.name).success.value
        helper(ua2).yourEmployer.get.label mustBe "yourEmployer.checkYourAnswersLabel"
        helper(ua2).yourEmployer.get.answer mustBe "site.no"
        helper(ua2).yourEmployer.get.labelArgs.head mustBe s"<p>${taiEmployment.head.name}</p>"
      }
    }
  }
}
