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

import controllers.authenticated.routes._
import controllers.routes._
import models.EmployerContribution.SomeContribution
import models.FirstIndustryOptions.{Engineering, FoodAndCatering, Healthcare, Retail, TransportAndDistribution}
import models.FourthIndustryOptions.{Agriculture, FireService, Heating, Leisure, Prisons}
import models.SecondIndustryOptions.{ClothingTextiles, Construction, Council, ManufacturingWarehousing, Police}
import models.ThirdIndustryOptions.{BanksBuildingSocieties, Education, Electrical, Printing, Security}
import models.{Address, CheckMode, TaxYearSelection, UserAnswers}
import pages._
import pages.authenticated._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def removeFRECode: Option[AnswerRow] = userAnswers.get(RemoveFRECodePage) map {
    x =>
      AnswerRow("removeFRECode.checkYourAnswersLabel",
        messages(s"taxYearSelection.$x", TaxYearSelection.getTaxYear(x).toString, (TaxYearSelection.getTaxYear(x) + 1).toString),
        true, Some(RemoveFRECodeController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None)
  }

  def claimAmountAndDeductions: Option[AnswerRow] = userAnswers.get(ClaimAmountAndAnyDeductions) map {
    x =>
      AnswerRow("claimAmount.checkYourAnswersLabel", s"£$x", true, changeUrl = None, editText = None, editTextHidden = None)
  }

  def alreadyClaimingFREDifferentAmounts: Option[AnswerRow] = userAnswers.get(AlreadyClaimingFREDifferentAmountsPage) map {
    x =>
      AnswerRow("alreadyClaimingFREDifferentAmounts.checkYourAnswersLabel", s"alreadyClaimingFREDifferentAmounts.$x", true,
        Some(AlreadyClaimingFREDifferentAmountsController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None)
  }

  def alreadyClaimingFRESameAmount: Option[AnswerRow] = userAnswers.get(AlreadyClaimingFRESameAmountPage) map {
    _ =>
      AnswerRow("alreadyClaimingFRESameAmount.checkYourAnswersLabel", "alreadyClaimingFRESameAmount.altNoText", true,
        Some(AlreadyClaimingFRESameAmountController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None)
  }

  def sameEmployerContributionAllYears: Option[AnswerRow] = userAnswers.get(SameEmployerContributionAllYearsPage) map {
    x =>
      AnswerRow("sameEmployerContributionAllYears.checkYourAnswersLabel", if (x) "site.yes" else "site.no", true,
        Some(SameEmployerContributionAllYearsController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None)
  }

  def changeWhichTaxYears: Option[AnswerRow] = userAnswers.get(ChangeWhichTaxYearsPage) map {
    taxYears =>
      AnswerRow("changeWhichTaxYears.checkYourAnswersLabel",
        taxYears.map {
          taxYear =>
            messages(s"taxYearSelection.$taxYear",
              TaxYearSelection.getTaxYear(taxYear).toString,
              (TaxYearSelection.getTaxYear(taxYear) + 1).toString
            )
        }.mkString("<br>"),
        false,
        Some(ChangeWhichTaxYearsController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None
      )
  }

  def industryAnswerRow(industry: String): Option[AnswerRow] = {
    Some(AnswerRow("industryType.checkYourAnswersLabel", industry, true,
      Some(FirstIndustryOptionsController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None
    ))
  }

  def industryType: Option[AnswerRow] = {
    userAnswers.get(FirstIndustryOptionsPage) match {
      case Some(Healthcare) => industryAnswerRow(s"firstIndustryOptions.${Healthcare.toString}")
      case Some(FoodAndCatering) => industryAnswerRow(s"firstIndustryOptions.${FoodAndCatering.toString}")
      case Some(Retail) => industryAnswerRow(s"firstIndustryOptions.${Retail.toString}")
      case Some(Engineering) => industryAnswerRow(s"firstIndustryOptions.${Engineering.toString}")
      case Some(TransportAndDistribution) => industryAnswerRow(s"firstIndustryOptions.${TransportAndDistribution.toString}")
      case _ => secondaryIndustryList
    }
  }

  def secondaryIndustryList: Option[AnswerRow] = {
    userAnswers.get(SecondIndustryOptionsPage) match {
      case Some(ManufacturingWarehousing) => industryAnswerRow(s"secondIndustryOptions.${ManufacturingWarehousing.toString}")
      case Some(Council) => industryAnswerRow(s"secondIndustryOptions.${Council.toString}")
      case Some(Police) => industryAnswerRow(s"secondIndustryOptions.${Police.toString}")
      case Some(ClothingTextiles) => industryAnswerRow(s"secondIndustryOptions.${ClothingTextiles.toString}")
      case Some(Construction) => industryAnswerRow(s"secondIndustryOptions.${Construction.toString}")
      case _ => thirdIndustryList
    }
  }

  def thirdIndustryList: Option[AnswerRow] = {
    userAnswers.get(ThirdIndustryOptionsPage) match {
      case Some(Electrical) => industryAnswerRow(s"thirdIndustryOptions.${Electrical.toString}")
      case Some(Education) => industryAnswerRow(s"thirdIndustryOptions.${Education.toString}")
      case Some(BanksBuildingSocieties) => industryAnswerRow(s"thirdIndustryOptions.${BanksBuildingSocieties.toString}")
      case Some(Security) => industryAnswerRow(s"thirdIndustryOptions.${Security.toString}")
      case Some(Printing) => industryAnswerRow(s"thirdIndustryOptions.${Printing.toString}")
      case _ => fourthIndustryList
    }
  }

  def fourthIndustryList: Option[AnswerRow] = {
    userAnswers.get(FourthIndustryOptionsPage) match {
      case Some(Agriculture) => industryAnswerRow(s"fourthIndustryOptions.${Agriculture.toString}")
      case Some(FireService) => industryAnswerRow(s"fourthIndustryOptions.${FireService.toString}")
      case Some(Heating) => industryAnswerRow(s"fourthIndustryOptions.${Heating.toString}")
      case Some(Leisure) => industryAnswerRow(s"fourthIndustryOptions.${Leisure.toString}")
      case Some(Prisons) => industryAnswerRow(s"fourthIndustryOptions.${Prisons.toString}")
      case _ => industryAnswerRow("default rate")
    }
  }

  def employerContribution: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) map {
    x =>
      AnswerRow("employerContribution.checkYourAnswersLabel", s"employerContribution.$x", true,
        Some(EmployerContributionController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None)
  }

  def expensesEmployerPaid: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) match {
    case Some(SomeContribution) =>
      userAnswers.get(ExpensesEmployerPaidPage) map {
        x =>
          AnswerRow("expensesEmployerPaid.checkYourAnswersLabel", s"£$x", false,
            Some(ExpensesEmployerPaidController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None)
      }
    case _ => None
  }

  def yourEmployer: Option[AnswerRow] = (userAnswers.get(YourEmployerPage), userAnswers.get(YourEmployerName)) match {
    case (Some(x), Some(employer)) =>
      Some(AnswerRow("yourEmployer.checkYourAnswersLabel",
        if (x) "site.yes" else "site.no", true,
        Some(YourEmployerController.onPageLoad(CheckMode).url),
        Some("checkYourAnswers.editText"),
        Some("checkYourAnswers.editTextHidden"),
        s"<p>$employer</p>"
      ))
    case _ => None
  }

  def taxYearSelection: Option[AnswerRow] = userAnswers.get(TaxYearSelectionPage) map {
    taxYears =>
      AnswerRow("taxYearSelection.checkYourAnswersLabel",
        taxYears.map {
          taxYear =>
            messages(s"taxYearSelection.$taxYear",
              TaxYearSelection.getTaxYear(taxYear).toString,
              (TaxYearSelection.getTaxYear(taxYear) + 1).toString
            )
        }.mkString("<br>"),
        false,
        Some(TaxYearSelectionController.onPageLoad(CheckMode).url), editText = None, editTextHidden = None
      )
  }

  def yourAddress: Option[AnswerRow] = (userAnswers.get(YourAddressPage), userAnswers.get(CitizenDetailsAddress)) match {
    case (Some(x), Some(address)) =>
      Some(AnswerRow("yourAddress.checkYourAnswersLabel",
        if (x) "site.yes" else "site.no", true,
        Some(YourAddressController.onPageLoad(CheckMode).url),
        Some("checkYourAnswers.editText"),
        Some("checkYourAnswers.editTextHidden"),
        Address.asString(address)
      ))
    case _ => None
  }
}
