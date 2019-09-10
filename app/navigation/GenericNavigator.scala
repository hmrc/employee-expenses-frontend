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

import controllers.authenticated.routes._
import controllers.clothing.routes._
import controllers.construction.routes._
import controllers.docks.routes._
import controllers.electrical.routes._
import controllers.engineering.routes._
import controllers.foodCatering.routes._
import controllers.healthcare.routes._
import controllers.heating.routes._
import controllers.manufacturing.routes._
import controllers.police.routes._
import controllers.printing.routes._
import controllers.routes._
import controllers.security.routes._
import controllers.shipyard.routes._
import controllers.textiles.routes._
import controllers.transport.routes._
import javax.inject.Inject
import models.EmployerContribution.{NoEmployerContribution, YesEmployerContribution}
import models.FifthIndustryOptions._
import models.FirstIndustryOptions._
import models.FourthIndustryOptions._
import models.MultipleEmployments._
import models.SecondIndustryOptions._
import models.ThirdIndustryOptions._
import models._
import pages._
import play.api.mvc.Call
import ExperimentalVariant.IndustryTypesVariant

class IndustryVariantGenericNavigator extends GenericNavigator {
  override protected def routeMap = PartialFunction[Page, UserAnswers => Call] {
    case SecondIndustryOptionsPage => secondIndustryOptions(NormalMode)
  }.orElse(super.routeMap)

  private def secondIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(SecondIndustryOptionsPage) match {
      case Some(Construction)                      => ConstructionOccupationsController.onPageLoad(mode)
      case Some(Council)                           => EmployerContributionController.onPageLoad(mode)
      case Some(Education)                         => EmployerContributionController.onPageLoad(mode)
      case Some(ManufacturingWarehousing)          => TypeOfManufacturingController.onPageLoad(mode)
      case Some(Police)                            => SpecialConstableController.onPageLoad(mode)
      case Some(SecondIndustryOptions.NoneOfAbove) => ThirdIndustryOptionsController.onPageLoad(mode)
      case _                                       => SessionExpiredController.onPageLoad()
    }
}

class GenericNavigator @Inject()() extends Navigator {

  override def variant(variant: ExperimentalVariant): Navigator = {variant  match {
      case IndustryTypesVariant => new IndustryVariantGenericNavigator
      case _ => super.variant(variant)
    }
  }

  protected def routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case MultipleEmploymentsPage   => multipleEmployments(NormalMode)
    case FirstIndustryOptionsPage  => firstIndustryOptions(NormalMode)
    case SecondIndustryOptionsPage => secondIndustryOptions(NormalMode)
    case ThirdIndustryOptionsPage  => thirdIndustryOptions(NormalMode)
    case FourthIndustryOptionsPage => fourthIndustryOptions(NormalMode)
    case FifthIndustryOptionsPage  => fifthIndustryOptions(NormalMode)
    case EmployerContributionPage  => employerContribution(NormalMode)
    case ExpensesEmployerPaidPage  => expensesEmployerPaid(NormalMode)
    case SameEmployerContributionAllYearsPage  => sameEmployerContributionAllYears(NormalMode)
    case ClaimAmount               => _ => TaxYearSelectionController.onPageLoad(NormalMode)
    case _ =>                    _ => IndexController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case MultipleEmploymentsPage  => multipleEmployments(CheckMode)
    case FirstIndustryOptionsPage => firstIndustryOptions(CheckMode)
    case SecondIndustryOptionsPage => secondIndustryOptions(CheckMode)
    case ThirdIndustryOptionsPage => thirdIndustryOptions(CheckMode)
    case FourthIndustryOptionsPage => fourthIndustryOptions(CheckMode)
    case FifthIndustryOptionsPage => fifthIndustryOptions(CheckMode)
    case EmployerContributionPage => employerContribution(CheckMode)
    case ExpensesEmployerPaidPage => expensesEmployerPaid(CheckMode)
    case SameEmployerContributionAllYearsPage => sameEmployerContributionAllYears(CheckMode)
    case _ =>                   _ => CheckYourAnswersController.onPageLoad()
  }

  private def multipleEmployments(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(MultipleEmploymentsPage) match {
      case Some(MoreThanOneJob)   => ClaimByAlternativeController.onPageLoad()
      case Some(OneJob)           => FirstIndustryOptionsController.onPageLoad(mode)
      case _                      => SessionExpiredController.onPageLoad()
    }

  private def firstIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(FirstIndustryOptionsPage) match {
      case Some(Engineering)                         => TypeOfEngineeringController.onPageLoad(mode)
      case Some(FoodAndCatering)                     => CateringStaffNHSController.onPageLoad(mode)
      case Some(Healthcare)                          => HealthcareList1Controller.onPageLoad(mode)
      case Some(Retail)                              => EmployerContributionController.onPageLoad(mode)
      case Some(TransportAndDistribution)            => TypeOfTransportController.onPageLoad(mode)
      case Some(FirstIndustryOptions.NoneOfAbove)    => SecondIndustryOptionsController.onPageLoad(mode)
      case _                                         => SessionExpiredController.onPageLoad()
    }

  private def secondIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(SecondIndustryOptionsPage) match {
      case Some(Construction)                      => ???
      case Some(Council)                           => EmployerContributionController.onPageLoad(mode)
      case Some(Education)                         => EmployerContributionController.onPageLoad(mode)
      case Some(ManufacturingWarehousing)          => TypeOfManufacturingController.onPageLoad(mode)
      case Some(Police)                            => SpecialConstableController.onPageLoad(mode)
      case Some(SecondIndustryOptions.NoneOfAbove) => ThirdIndustryOptionsController.onPageLoad(mode)
      case _                                       => SessionExpiredController.onPageLoad()
    }

  private def thirdIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(ThirdIndustryOptionsPage) match {
      case Some(BanksBuildingSocieties)           => EmployerContributionController.onPageLoad(mode)
      case Some(Electrical)                       => ElectricalController.onPageLoad(mode)
      case Some(Leisure)                          => EmployerContributionController.onPageLoad(mode)
      case Some(Prisons)                          => EmployerContributionController.onPageLoad(mode)
      case Some(Security)                         => SecurityGuardNHSController.onPageLoad(mode)
      case Some(ThirdIndustryOptions.NoneOfAbove) => FourthIndustryOptionsController.onPageLoad(mode)
      case _                                      => SessionExpiredController.onPageLoad()
    }

  private def fourthIndustryOptions(mode:Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(FourthIndustryOptionsPage) match {
      case Some(Agriculture)                          => EmployerContributionController.onPageLoad(mode)
      case Some(ClothingTextiles)                     => ClothingController.onPageLoad(mode)
      case Some(FireService)                          => EmployerContributionController.onPageLoad(mode)
      case Some(Heating)                              => HeatingOccupationListController.onPageLoad(mode)
      case Some(Printing)                             => PrintingOccupationList1Controller.onPageLoad(mode)
      case Some(FourthIndustryOptions.NoneOfAbove)    => FifthIndustryOptionsController.onPageLoad(mode)
      case _                                          => SessionExpiredController.onPageLoad()
    }

  private def fifthIndustryOptions(mode:Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(FifthIndustryOptionsPage) match {
      case Some(Armedforces)                           => CannotClaimExpenseController.onPageLoad()
      case Some(Dockswaterways)                        => DocksOccupationList1Controller.onPageLoad(mode)
      case Some(Forestry)                              => EmployerContributionController.onPageLoad(mode)
      case Some(Shipyard)                              => ShipyardApprenticeStorekeeperController.onPageLoad(mode)
      case Some(Textiles)                              => TextilesOccupationList1Controller.onPageLoad(mode)
      case Some(FifthIndustryOptions.NoneOfAbove)      => EmployerContributionController.onPageLoad(mode)
      case _                                           => SessionExpiredController.onPageLoad()
    }

  private def employerContribution(mode: Mode)(userAnswers: UserAnswers): Call =
   userAnswers.get(EmployerContributionPage) match {
     case Some(YesEmployerContribution) => ExpensesEmployerPaidController.onPageLoad(mode)
     case Some(NoEmployerContribution) => ClaimAmountController.onPageLoad(mode)
     case _ => SessionExpiredController.onPageLoad()
    }

  private def expensesEmployerPaid(mode: Mode)(userAnswers: UserAnswers): Call =
    (userAnswers.get(ClaimAmount), userAnswers.get(ExpensesEmployerPaidPage)) match {
      case (Some(claimAmount), Some(expensesPaid)) =>
        if (claimAmount > expensesPaid) SameEmployerContributionAllYearsController.onPageLoad(mode) else CannotClaimController.onPageLoad()
      case _ =>
        SessionExpiredController.onPageLoad()
    }

  private def sameEmployerContributionAllYears(mode: Mode)(userAnswers: UserAnswers): Call =
  userAnswers.get(SameEmployerContributionAllYearsPage) match {
    case Some(true) =>
      ClaimAmountController.onPageLoad(mode)
    case Some(false) =>
      PhoneUsController.onPageLoad()
    case _ =>
      SessionExpiredController.onPageLoad()
  }

}
