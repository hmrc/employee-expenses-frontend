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
import models.FifthIndustryOptions
import models.FifthIndustryOptions._
import models.FirstIndustryOptions._
import models.FourthIndustryOptions._
import models.SecondIndustryOptions._
import models.ThirdIndustryOptions._
import models._
import pages._
import pages.authenticated._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def removeFRECode: Option[AnswerRow] = userAnswers.get(RemoveFRECodePage) map {
    x =>
      AnswerRow("removeFRECode.checkYourAnswersLabel",
        messages(s"taxYearSelection.$x", TaxYearSelection.getTaxYear(x).toString, (TaxYearSelection.getTaxYear(x) + 1).toString),
        true, Some(RemoveFRECodeController.onPageLoad(CheckMode).url), editText = None)
  }

  def alreadyClaimingFREDifferentAmounts: Option[AnswerRow] = userAnswers.get(AlreadyClaimingFREDifferentAmountsPage) map {
    x =>
      AnswerRow("alreadyClaimingFREDifferentAmounts.checkYourAnswersLabel", s"alreadyClaimingFREDifferentAmounts.$x", true,
        Some(AlreadyClaimingFREDifferentAmountsController.onPageLoad(CheckMode).url), editText = None)
  }

  def alreadyClaimingFRESameAmount: Option[AnswerRow] = userAnswers.get(AlreadyClaimingFRESameAmountPage) map {
    x =>
      AnswerRow("alreadyClaimingFRESameAmount.checkYourAnswersLabel", s"alreadyClaimingFRESameAmount.$x", true,
        Some(AlreadyClaimingFRESameAmountController.onPageLoad(CheckMode).url), editText = None)
  }

  def sameEmployerContributionAllYears: Option[AnswerRow] = userAnswers.get(SameEmployerContributionAllYearsPage) map {
    x =>
      AnswerRow("sameEmployerContributionAllYears.checkYourAnswersLabel", if (x) "site.yes" else "site.no", true,
        Some(SameEmployerContributionAllYearsController.onPageLoad(CheckMode).url), editText = None)
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
        Some(ChangeWhichTaxYearsController.onPageLoad(CheckMode).url), editText = None
      )
  }

  def industryAnswerRow(industry: String): Option[AnswerRow] = {
    Some(AnswerRow("industryType.checkYourAnswersLabel", industry, true,
      Some(FirstIndustryOptionsController.onPageLoad(CheckMode).url), editText = None
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
      case Some(Construction) => industryAnswerRow(s"secondIndustryOptions.${Construction.toString}")
      case Some(Council) => industryAnswerRow(s"secondIndustryOptions.${Council.toString}")
      case Some(Education) => industryAnswerRow(s"secondIndustryOptions.${Education.toString}")
      case Some(ManufacturingWarehousing) => industryAnswerRow(s"secondIndustryOptions.${ManufacturingWarehousing.toString}")
      case Some(Police) => industryAnswerRow(s"secondIndustryOptions.${Police.toString}")
      case _ => thirdIndustryList
    }
  }

  def thirdIndustryList: Option[AnswerRow] = {
    userAnswers.get(ThirdIndustryOptionsPage) match {
      case Some(BanksBuildingSocieties) => industryAnswerRow(s"thirdIndustryOptions.${BanksBuildingSocieties.toString}")
      case Some(Electrical) => industryAnswerRow(s"thirdIndustryOptions.${Electrical.toString}")
      case Some(Leisure) => industryAnswerRow(s"thirdIndustryOptions.${Leisure.toString}")
      case Some(Prisons) => industryAnswerRow(s"thirdIndustryOptions.${Prisons.toString}")
      case Some(Security) => industryAnswerRow(s"thirdIndustryOptions.${Security.toString}")
      case _ => fourthIndustryList
    }
  }

  def fourthIndustryList: Option[AnswerRow] = {
    userAnswers.get(FourthIndustryOptionsPage) match {
      case Some(Agriculture) => industryAnswerRow(s"fourthIndustryOptions.${Agriculture.toString}")
      case Some(ClothingTextiles) => industryAnswerRow(s"fourthIndustryOptions.${ClothingTextiles.toString}")
      case Some(FireService) => industryAnswerRow(s"fourthIndustryOptions.${FireService.toString}")
      case Some(Heating) => industryAnswerRow(s"fourthIndustryOptions.${Heating.toString}")
      case Some(Printing) => industryAnswerRow(s"fourthIndustryOptions.${Printing.toString}")
      case _ => fifthIndustryList
    }
  }

  def fifthIndustryList: Option[AnswerRow] = {
    userAnswers.get(FifthIndustryOptionsPage) match {
      case Some(Armedforces) => industryAnswerRow(s"fifthIndustryOptions.${Armedforces.toString}")
      case Some(Dockswaterways) => industryAnswerRow(s"fifthIndustryOptions.${Dockswaterways.toString}")
      case Some(Forestry) => industryAnswerRow(s"fifthIndustryOptions.${Forestry.toString}")
      case Some(Shipyard) => industryAnswerRow(s"fifthIndustryOptions.${Shipyard.toString}")
      case Some(Textiles) => industryAnswerRow(s"fifthIndustryOptions.${Textiles.toString}")
      case Some(FifthIndustryOptions.NoneOfAbove) => industryAnswerRow(s"fifthIndustryOptions.${FifthIndustryOptions.NoneOfAbove.toString}")
      case _ => industryAnswerRow("default rate")
    }
  }

  def employerContribution: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) map {
    employerContributionOption =>
            AnswerRow(
              "employerContribution.checkYourAnswersLabel",
              s"employerContribution.$employerContributionOption",
              true,
              Some(EmployerContributionController.onPageLoad(CheckMode).url),
              None
            )
  }

  def expensesEmployerPaid: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) match {
    case Some(EmployerContribution.YesEmployerContribution) =>
      userAnswers.get(ExpensesEmployerPaidPage) map {
        x =>
          AnswerRow("expensesEmployerPaid.checkYourAnswersLabel", s"£$x", false,
            Some(ExpensesEmployerPaidController.onPageLoad(CheckMode).url), editText = None)
      }
    case _ => None
  }

  def yourEmployer: Option[AnswerRow] = (userAnswers.get(YourEmployerPage), userAnswers.get(YourEmployerNames)) match {
    case (Some(x), Some(employers)) =>
      Some(AnswerRow("yourEmployer.checkYourAnswersLabel",
        if (x) "site.yes" else "site.no", true,
        Some(YourEmployerController.onPageLoad(CheckMode).url),
        Some("checkYourAnswers.editText"),
        Employment.asLabel(employers)
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
        Some(TaxYearSelectionController.onPageLoad(CheckMode).url), editText = None
      )
  }

  def yourAddress: Option[AnswerRow] = (userAnswers.get(YourAddressPage), userAnswers.get(CitizenDetailsAddress)) match {
    case (Some(x), Some(address)) =>
      Some(AnswerRow("yourAddress.checkYourAnswersLabel",
        if (x) "site.yes" else "site.no", true,
        Some(YourAddressController.onPageLoad(CheckMode).url),
        Some("checkYourAnswers.editText"),
        Address.asString(address)
      ))
    case _ => None
  }
}
