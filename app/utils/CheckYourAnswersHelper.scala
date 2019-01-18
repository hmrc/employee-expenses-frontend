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
import pages.engineering.{AncillaryEngineeringWhichTradePage, ConstructionalEngineeringApprenticePage, ConstructionalEngineeringList1Page, ConstructionalEngineeringList2Page}
import pages.transport.TypeOfTransportPage
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def ancillaryEngineeringWhichTrade: Option[AnswerRow] = userAnswers.get(AncillaryEngineeringWhichTradePage) map {
    x => AnswerRow("ancillaryEngineeringWhichTrade.checkYourAnswersLabel", s"ancillaryEngineeringWhichTrade.$x", true, controllers.engineering.routes.AncillaryEngineeringWhichTradeController.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringApprentice: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringApprenticePage) map {
    x => AnswerRow("constructionalEngineeringApprentice.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, controllers.engineering.routes.ConstructionalEngineeringApprenticeController.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringList2: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList2Page) map {
    x => AnswerRow("constructionalEngineeringList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, controllers.engineering.routes.ConstructionalEngineeringList2Controller.onPageLoad(CheckMode).url)
  }

  def healthcareList1: Option[AnswerRow] = userAnswers.get(HealthcareList1Page) map {
    x => AnswerRow("healthcareList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.HealthcareList1Controller.onPageLoad(CheckMode).url)
  }

  def typeOfTransport: Option[AnswerRow] = userAnswers.get(TypeOfTransportPage) map {
    x => AnswerRow("typeOfTransport.checkYourAnswersLabel", s"typeOfTransport.$x", true, routes.TypeOfTransportController.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringList1: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList1Page) map {
    x => AnswerRow("constructionalEngineeringList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, controllers.engineering.routes.ConstructionalEngineeringList1Controller.onPageLoad(CheckMode).url)
  }

  def typeOfEngineering: Option[AnswerRow] = userAnswers.get(TypeOfEngineeringPage) map {
    x => AnswerRow("typeOfEngineering.checkYourAnswersLabel", s"typeOfEngineering.$x", true, routes.TypeOfEngineeringController.onPageLoad(CheckMode).url)
  }

  def ambulanceStaff: Option[AnswerRow] = userAnswers.get(AmbulanceStaffPage) map {
    x => AnswerRow("ambulanceStaff.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.AmbulanceStaffController.onPageLoad(CheckMode).url)
  }

  def employerContribution: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) map {
    x => AnswerRow("employerContribution.checkYourAnswersLabel", s"employerContribution.$x", true, routes.EmployerContributionController.onPageLoad(CheckMode).url)
  }

  def multipleEmployments: Option[AnswerRow] = userAnswers.get(MultipleEmploymentsPage) map {
    x => AnswerRow("multipleEmployments.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.MultipleEmploymentsController.onPageLoad(CheckMode).url)
  }

  def expensesEmployerPaid: Option[AnswerRow] = userAnswers.get(ExpensesEmployerPaidPage) map {
    x => AnswerRow("expensesEmployerPaid.checkYourAnswersLabel", s"$x", false, routes.ExpensesEmployerPaidController.onPageLoad(CheckMode).url)
  }
}
