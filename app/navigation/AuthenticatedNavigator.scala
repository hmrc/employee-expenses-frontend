/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.confirmation.routes._
import controllers.routes._
import javax.inject.Inject
import models.AlreadyClaimingFREDifferentAmounts._
import models.FlatRateExpenseOptions._
import models.TaxYearSelection.CurrentYear
import models._
import pages.authenticated._
import pages.{FREResponse, Page}
import play.api.mvc.Call

class AuthenticatedNavigator @Inject()() extends Navigator {
  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case TaxYearSelectionPage => taxYearSelection(NormalMode)
    case AlreadyClaimingFRESameAmountPage => alreadyClaimingFRESameAmount(NormalMode)
    case AlreadyClaimingFREDifferentAmountsPage => alreadyClaimingFREDifferentAmount(NormalMode)
    case UpdateYourEmployerInformationPage => _ => HowYouWillGetYourExpensesController.onPageLoad()
    case RemoveFRECodePage => _ => CheckYourAnswersController.onPageLoad()
    case ChangeWhichTaxYearsPage => _ => CheckYourAnswersController.onPageLoad()
    case CheckYourAnswersPage => _ => YourAddressController.onPageLoad(NormalMode)
    case YourAddressPage => yourAddress
    case YourEmployerPage => yourEmployer
    case HowYouWillGetYourExpensesPage => _ => SubmissionController.onSubmit()
    case Submission => submission
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case TaxYearSelectionPage => taxYearSelection(CheckMode)
    case AlreadyClaimingFREDifferentAmountsPage => alreadyClaimingFREDifferentAmount(CheckMode)
    case AlreadyClaimingFRESameAmountPage => alreadyClaimingFRESameAmount(CheckMode)
    case UpdateYourEmployerInformationPage => _ => HowYouWillGetYourExpensesController.onPageLoad()
    case ChangeWhichTaxYearsPage => _ => CheckYourAnswersController.onPageLoad()
    case _ => _ => CheckYourAnswersController.onPageLoad()
  }

  private def yourAddress(userAnswers: UserAnswers): Call = {
    val routeIfRemoveDifferent = userAnswers.get(AlreadyClaimingFREDifferentAmountsPage) flatMap {
      case AlreadyClaimingFREDifferentAmounts.Remove => Some(SubmissionController.onSubmit())
      case _                                         => None
    }

    val routeIfRemoveSame = userAnswers.get(AlreadyClaimingFRESameAmountPage) flatMap {
      case AlreadyClaimingFRESameAmount.Remove => Some(SubmissionController.onSubmit())
      case _                                   => None
    }

    val routeIfCurrentYear = (userAnswers.get(TaxYearSelectionPage), userAnswers.get(ChangeWhichTaxYearsPage)) match {
      case (Some(selectedYears), None) if selectedYears.contains(CurrentYear) => Some(YourEmployerController.onPageLoad())
      case (Some(_), Some(changeYears)) if changeYears.contains(CurrentYear)  => Some(YourEmployerController.onPageLoad())
      case (None, _) => Some(TechnicalDifficultiesController.onPageLoad())
      case _         => None
    }

    (routeIfRemoveDifferent orElse routeIfRemoveSame orElse routeIfCurrentYear)
      .getOrElse(HowYouWillGetYourExpensesController.onPageLoad())
  }

  private def taxYearSelection(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(FREResponse) match {
    case Some(FRENoYears) => CheckYourAnswersController.onPageLoad()
    case Some(FREAllYearsAllAmountsSameAsClaimAmount) =>
      AlreadyClaimingFRESameAmountController.onPageLoad(mode)
    case Some(FRESomeYears) =>
      AlreadyClaimingFREDifferentAmountsController.onPageLoad(mode)
    case Some(TechnicalDifficulties) =>
      TechnicalDifficultiesController.onPageLoad()
    case _ =>
      SessionExpiredController.onPageLoad()
  }

  private def alreadyClaimingFRESameAmount(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(AlreadyClaimingFRESameAmountPage) match {
      case Some(AlreadyClaimingFRESameAmount.NoChange) => NoCodeChangeController.onPageLoad()
      case Some(AlreadyClaimingFRESameAmount.Remove) => RemoveFRECodeController.onPageLoad(mode)
      case _ => SessionExpiredController.onPageLoad()
    }

  private def alreadyClaimingFREDifferentAmount(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(AlreadyClaimingFREDifferentAmountsPage) match {
      case Some(NoChange) => NoCodeChangeController.onPageLoad()
      case Some(Change) => ChangeWhichTaxYearsController.onPageLoad(mode)
      case Some(Remove) => RemoveFRECodeController.onPageLoad(mode)
      case _ => SessionExpiredController.onPageLoad()
    }

  private def yourEmployer(userAnswers: UserAnswers): Call = {
    userAnswers.get(YourEmployerPage) match {
      case Some(true)   => HowYouWillGetYourExpensesController.onPageLoad()
      case Some(false)  => UpdateEmployerInformationController.onPageLoad(NormalMode)
      case            _ => SessionExpiredController.onPageLoad()
    }
  }

  private def submission(userAnswers: UserAnswers): Call = {
    (
      userAnswers.get(TaxYearSelectionPage),
      userAnswers.get(RemoveFRECodePage),
      userAnswers.get(ChangeWhichTaxYearsPage)
    ) match {
      case (Some(_), Some(_), None) =>
        ConfirmationClaimStoppedController.onPageLoad()
      case (Some(taxYearsSelection), None, changeYears) =>

        val taxYears = changeYears match {
          case Some(changeYear) => changeYear
          case _ => taxYearsSelection
        }

        if (taxYears.forall(_ == TaxYearSelection.CurrentYear)) {
          ConfirmationCurrentYearOnlyController.onPageLoad()
        } else if (!taxYears.contains(TaxYearSelection.CurrentYear)) {
          ConfirmationPreviousYearsOnlyController.onPageLoad()
        } else {
          ConfirmationCurrentAndPreviousYearsController.onPageLoad()
        }
      case _ =>
        SessionExpiredController.onPageLoad()
    }
  }

}
