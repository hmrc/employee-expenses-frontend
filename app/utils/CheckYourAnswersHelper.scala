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

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages._
import pages.transport.TypeOfTransportPage
import pages.engineering._
import pages.healthcare._
import pages.transport._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def secondIndustryOptions: Option[AnswerRow] = userAnswers.get(SecondIndustryOptionsPage) map {
    x => AnswerRow("secondIndustryOptions.checkYourAnswersLabel", s"secondIndustryOptions.$x", true, routes.SecondIndustryOptionsController.onPageLoad(CheckMode).url)
  }

  def garageHandOrCleaner: Option[AnswerRow] = userAnswers.get(GarageHandOrCleanerPage) map {
    x => AnswerRow("garageHandOrCleaner.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.transport.routes.GarageHandOrCleanerController.onPageLoad(CheckMode).url)
  }

  def airlineJobListed: Option[AnswerRow] = userAnswers.get(AirlineJobListPage) map {
    x => AnswerRow("airlineJobListed.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.transport.routes.AirlineJobListController.onPageLoad(CheckMode).url
    )
  }

  def typeOfTransport: Option[AnswerRow] = userAnswers.get(TypeOfTransportPage) map {
    x => AnswerRow("typeOfTransport.checkYourAnswersLabel", s"typeOfTransport.$x", true,
      controllers.transport.routes.TypeOfTransportController.onPageLoad(CheckMode).url)
  }

  def employerContribution: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) map {
    x => AnswerRow("employerContribution.checkYourAnswersLabel", s"employerContribution.$x", true,
      routes.EmployerContributionController.onPageLoad(CheckMode).url)
  }

  def multipleEmployments: Option[AnswerRow] = userAnswers.get(MultipleEmploymentsPage) map {
    x => AnswerRow("multipleEmployments.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      routes.MultipleEmploymentsController.onPageLoad(CheckMode).url)
  }

  def expensesEmployerPaid: Option[AnswerRow] = userAnswers.get(ExpensesEmployerPaidPage) map {
    x => AnswerRow("expensesEmployerPaid.checkYourAnswersLabel", s"$x", false,
      routes.ExpensesEmployerPaidController.onPageLoad(CheckMode).url)
  }
  def firstIndustryOptions: Option[AnswerRow] = userAnswers.get(FirstIndustryOptionsPage) map {
    x => AnswerRow("firstIndustryOptions.checkYourAnswersLabel", s"$x", false,
      controllers.routes.FirstIndustryOptionsController.onPageLoad(CheckMode).url)
  }

  //Engineering

  def typeOfEngineering: Option[AnswerRow] = userAnswers.get(TypeOfEngineeringPage) map {
    x => AnswerRow("typeOfEngineering.checkYourAnswersLabel", s"typeOfEngineering.$x", true,
      controllers.engineering.routes.TypeOfEngineeringController.onPageLoad(CheckMode).url)
  }

  def factoryEngineeringList1: Option[AnswerRow] = userAnswers.get(FactoryEngineeringList1Page) map {
    x => AnswerRow("factoryEngineeringList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.engineering.routes.FactoryEngineeringList1Controller.onPageLoad(CheckMode).url)
  }

  def factoryEngineeringList2: Option[AnswerRow] = userAnswers.get(FactoryEngineeringList2Page) map {
    x => AnswerRow("factoryEngineeringList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.engineering.routes.FactoryEngineeringList2Controller.onPageLoad(CheckMode).url)
  }

  def ancillaryEngineeringWhichTrade: Option[AnswerRow] = userAnswers.get(AncillaryEngineeringWhichTradePage) map {
    x => AnswerRow("ancillaryEngineeringWhichTrade.checkYourAnswersLabel", s"ancillaryEngineeringWhichTrade.$x", true,
      controllers.engineering.routes.AncillaryEngineeringWhichTradeController.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringList1: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList1Page) map {
    x => AnswerRow("constructionalEngineeringList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.engineering.routes.ConstructionalEngineeringList1Controller.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringList2: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList2Page) map {
    x => AnswerRow("constructionalEngineeringList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.engineering.routes.ConstructionalEngineeringList2Controller.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringApprentice: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringApprenticePage) map {
    x => AnswerRow("constructionalEngineeringApprentice.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.engineering.routes.ConstructionalEngineeringApprenticeController.onPageLoad(CheckMode).url)
  }

  //Healthcare

  def healthcareList2: Option[AnswerRow] = userAnswers.get(HealthcareList2Page) map {
    x => AnswerRow("healthcareList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.healthcare.routes.HealthcareList2Controller.onPageLoad(CheckMode).url)
  }

  def healthcareList1: Option[AnswerRow] = userAnswers.get(HealthcareList1Page) map {
    x => AnswerRow("healthcareList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.healthcare.routes.HealthcareList1Controller.onPageLoad(CheckMode).url)
  }

  def ambulanceStaff: Option[AnswerRow] = userAnswers.get(AmbulanceStaffPage) map {
    x => AnswerRow("ambulanceStaff.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      controllers.healthcare.routes.AmbulanceStaffController.onPageLoad(CheckMode).url)
  }
}
