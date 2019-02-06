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

package navigation

import controllers.construction.routes._
import controllers.engineering.routes._
import controllers.foodCatering.routes._
import controllers.healthcare.routes._
import controllers.manufacturing.routes._
import controllers.police.routes._
import controllers.clothing.routes._
import controllers.routes._
import controllers.transport.routes._
import javax.inject.Inject
import models.FirstIndustryOptions._
import models.ThirdIndustryOptions.Education
import models.SecondIndustryOptions._
import models.{CheckMode, EmployerContribution, Mode, NormalMode, UserAnswers}
import pages._
import play.api.mvc.Call

class GenericNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case MultipleEmploymentsPage  => multipleEmployments(NormalMode)
    case FirstIndustryOptionsPage => firstIndustryOptions(NormalMode)
    case SecondIndustryOptionsPage => secondIndustryOptions(NormalMode)
    case ThirdIndustryOptionsPage => thirdIndustryOptions(NormalMode)
    case EmployerContributionPage => employerContribution(NormalMode)
    case ExpensesEmployerPaidPage => expensesEmployerPaid(NormalMode)
    case _ =>                   _ => IndexController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case MultipleEmploymentsPage  => multipleEmployments(CheckMode)
    case FirstIndustryOptionsPage => firstIndustryOptions(CheckMode)
    case EmployerContributionPage => employerContribution(CheckMode)
    case ExpensesEmployerPaidPage => expensesEmployerPaid(CheckMode)
    case _ =>                   _ => CheckYourAnswersController.onPageLoad()
  }

  private def multipleEmployments(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(MultipleEmploymentsPage) match {
      case Some(true)  => ClaimByAlternativeController.onPageLoad()
      case Some(false) => FirstIndustryOptionsController.onPageLoad(mode)
      case _           => SessionExpiredController.onPageLoad()
    }

  private def firstIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(FirstIndustryOptionsPage) match {
      case Some(Engineering)              => TypeOfEngineeringController.onPageLoad(mode)
      case Some(FoodAndCatering)          => CateringStaffNHSController.onPageLoad(mode)
      case Some(Healthcare)               => AmbulanceStaffController.onPageLoad(mode)
      case Some(Retail)                   => EmployerContributionController.onPageLoad(mode)
      case Some(TransportAndDistribution) => TypeOfTransportController.onPageLoad(mode)
      case Some(NoneOfTheAbove)           => SecondIndustryOptionsController.onPageLoad(mode)
      case _                              => SessionExpiredController.onPageLoad()
    }

  private def secondIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(SecondIndustryOptionsPage) match {
      case Some(Construction)             => JoinerCarpenterController.onPageLoad(mode)
      case Some(ManufacturingWarehousing) => TypeOfManufacturingController.onPageLoad(mode)
      case Some(Council)                  => EmployerContributionController.onPageLoad(mode)
      case Some(Police)                   => SpecialConstableController.onPageLoad(mode)
      case Some(ClothingTextiles)         => ClothingController.onPageLoad(mode)
      case Some(NoneOfAbove)              => ThirdIndustryOptionsController.onPageLoad(mode)
      case _                              => SessionExpiredController.onPageLoad()
    }

  private def employerContribution(mode: Mode)(userAnswers: UserAnswers): Call =
   userAnswers.get(EmployerContributionPage) match {
      case Some(EmployerContribution.All)  => CannotClaimController.onPageLoad()
      case Some(EmployerContribution.None) => ClaimAmountController.onPageLoad()
      case Some(EmployerContribution.Some) => ExpensesEmployerPaidController.onPageLoad(mode)
      case _                               => SessionExpiredController.onPageLoad()
    }

  private def expensesEmployerPaid(mode: Mode)(userAnswers: UserAnswers): Call =
    (userAnswers.get(ClaimAmount), userAnswers.get(ExpensesEmployerPaidPage)) match {
      case (Some(claimAmount), Some(expensesPaid)) =>
        if (claimAmount > expensesPaid) ClaimAmountController.onPageLoad() else CannotClaimController.onPageLoad()
      case _ =>
        SessionExpiredController.onPageLoad()
    }

  private def thirdIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(ThirdIndustryOptionsPage) match {
      case Some(Education) => EmployerContributionController.onPageLoad(mode)
      case _ => SessionExpiredController.onPageLoad()
    }
}
