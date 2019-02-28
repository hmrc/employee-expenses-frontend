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
import models.{Address, CheckMode, FirstIndustryOptions, FourthIndustryOptions, SecondIndustryOptions, TaxYearSelection, ThirdIndustryOptions, UserAnswers}
import pages._
import pages.authenticated._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def sameEmployerContributionAllYears: Option[AnswerRow] = userAnswers.get(SameEmployerContributionAllYearsPage) map {
    x => AnswerRow("sameEmployerContributionAllYears.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, SameEmployerContributionAllYearsController.onPageLoad(CheckMode).url)
  }

  def industryAnswerRow(industry: String): Option[AnswerRow] = {
    Some(AnswerRow("industryType.checkYourAnswersLabel", industry, true,
      FirstIndustryOptionsController.onPageLoad(CheckMode).url
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
        EmployerContributionController.onPageLoad(CheckMode).url)
  }

  def expensesEmployerPaid: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) match {
    case Some(SomeContribution) =>
      userAnswers.get(ExpensesEmployerPaidPage) map {
        x =>
          AnswerRow("expensesEmployerPaid.checkYourAnswersLabel", s"$x", false,
            ExpensesEmployerPaidController.onPageLoad(CheckMode).url)
      }
    case _ => None
  }

  def yourEmployer: Option[AnswerRow] = userAnswers.get(YourEmployerPage) map {
    x => AnswerRow("yourEmployer.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      YourEmployerController.onPageLoad(CheckMode).url)
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
        TaxYearSelectionController.onPageLoad(CheckMode).url)
  }

  def yourAddress: Option[AnswerRow] = (userAnswers.get(YourAddressPage), userAnswers.get(CitizenDetailsAddress)) match {
    case (Some(x), Some(address)) =>
      Some(AnswerRow("yourAddress.checkYourAnswersLabel",
        if (x) "site.yes" else "site.no", true,
        YourAddressController.onPageLoad(CheckMode).url,
        Address.asString(address)))
    case _ => None
  }
}
