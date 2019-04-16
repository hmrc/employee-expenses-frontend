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
import controllers.confirmation.routes._
import controllers.routes._
import javax.inject.Inject
import models.AlreadyClaimingFREDifferentAmounts._
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
    case YourAddressPage => yourAddress(NormalMode)
    case UpdateYourAddressPage => _ => CheckYourAnswersController.onPageLoad()
    case YourEmployerPage => yourEmployer(NormalMode)
    case UpdateYourEmployerInformationPage => updateEmployerInformation(NormalMode)
    case RemoveFRECodePage => _ => CheckYourAnswersController.onPageLoad()
    case ChangeWhichTaxYearsPage => changeWhichTaxYears(NormalMode)
    case CheckYourAnswersPage => checkYourAnswers()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case TaxYearSelectionPage => taxYearSelection(CheckMode)
    case AlreadyClaimingFREDifferentAmountsPage => alreadyClaimingFREDifferentAmount(CheckMode)
    case AlreadyClaimingFRESameAmountPage => alreadyClaimingFRESameAmount(CheckMode)
    case YourAddressPage => yourAddress(CheckMode)
    case YourEmployerPage => yourEmployer(CheckMode)
    case UpdateYourEmployerInformationPage => updateEmployerInformation(CheckMode)
    case ChangeWhichTaxYearsPage => changeWhichTaxYears(CheckMode)
    case _ => _ => CheckYourAnswersController.onPageLoad()
  }

  def taxYearSelection(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(FREResponse) match {
    case Some(FlatRateExpenseOptions.FRENoYears) =>
      if (userAnswers.get(TaxYearSelectionPage).get.contains(CurrentYear)) {
        YourEmployerController.onPageLoad(mode)
      } else {
        YourAddressController.onPageLoad(mode)
      }
    case Some(FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount) =>
      AlreadyClaimingFRESameAmountController.onPageLoad(mode)
    case Some(FlatRateExpenseOptions.FREAllYearsAllAmountsDifferent) =>
      AlreadyClaimingFREDifferentAmountsController.onPageLoad(mode)
    case Some(FlatRateExpenseOptions.ComplexClaim) =>
      PhoneUsController.onPageLoad()
    case Some(FlatRateExpenseOptions.TechnicalDifficulties) =>
      controllers.routes.TechnicalDifficultiesController.onPageLoad()
    case _ =>
      SessionExpiredController.onPageLoad()
  }

  def alreadyClaimingFRESameAmount(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(AlreadyClaimingFRESameAmountPage) match {
      case Some(AlreadyClaimingFRESameAmount.NoChange) =>
        NoCodeChangeController.onPageLoad()
      case Some(AlreadyClaimingFRESameAmount.Remove) =>
        RemoveFRECodeController.onPageLoad(mode)
      case _ =>
        SessionExpiredController.onPageLoad()
    }

  def alreadyClaimingFREDifferentAmount(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(AlreadyClaimingFREDifferentAmountsPage) match {
      case Some(NoChange) =>
        NoCodeChangeController.onPageLoad()
      case Some(Change) =>
        ChangeWhichTaxYearsController.onPageLoad(mode)
      case Some(Remove) =>
        RemoveFRECodeController.onPageLoad(mode)
      case _ =>
        SessionExpiredController.onPageLoad()
    }

  def yourAddress(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(YourAddressPage) match {
    case Some(true) =>
      CheckYourAnswersController.onPageLoad()
    case Some(false) =>
      UpdateYourAddressController.onPageLoad()
    case _ =>
      SessionExpiredController.onPageLoad()
  }

  def yourEmployer(mode: Mode)(userAnswers: UserAnswers): Call = {

    if (mode == NormalMode) {
      userAnswers.get(YourEmployerPage) match {
        case Some(true) =>
          YourAddressController.onPageLoad(mode)
        case Some(false) =>
          UpdateEmployerInformationController.onPageLoad(mode)
        case _ =>
          SessionExpiredController.onPageLoad()
      }
    } else {
      (userAnswers.get(YourEmployerPage), userAnswers.get(YourAddressPage)) match {
        case (Some(true), None) =>
          YourAddressController.onPageLoad(mode)
        case (Some(true), Some(_)) =>
          CheckYourAnswersController.onPageLoad()
        case (Some(false), _) =>
          UpdateEmployerInformationController.onPageLoad(mode)
        case _ =>
          SessionExpiredController.onPageLoad()
      }
    }
  }

  def updateEmployerInformation(mode: Mode)(userAnswers: UserAnswers): Call = {
    if (mode == CheckMode) {
      userAnswers.get(YourAddressPage) match {
        case Some(_) =>
          CheckYourAnswersController.onPageLoad()
        case None =>
          YourAddressController.onPageLoad(mode)
        case _ =>
          SessionExpiredController.onPageLoad()
      }
    } else {
      YourAddressController.onPageLoad(mode)
    }
  }

  def changeWhichTaxYears(mode: Mode)(userAnswers: UserAnswers): Call = {
    (mode, userAnswers.get(YourEmployerPage), userAnswers.get(ChangeWhichTaxYearsPage)) match {
      case (NormalMode, None, Some(selectedYears)) =>
        if (selectedYears.contains(CurrentYear)) YourEmployerController.onPageLoad(mode) else YourAddressController.onPageLoad(mode)
      case (NormalMode, _, None) =>
        YourAddressController.onPageLoad(mode)
      case (CheckMode, None, Some(selectedYears)) =>
        if (selectedYears.contains(CurrentYear)) YourEmployerController.onPageLoad(mode) else CheckYourAnswersController.onPageLoad()
      case _ =>
        CheckYourAnswersController.onPageLoad()
    }
  }

  def checkYourAnswers()(userAnswers: UserAnswers): Call = {
    (userAnswers.get(TaxYearSelectionPage), userAnswers.get(RemoveFRECodePage)) match {
      case (Some(_), Some(_)) =>
        ConfirmationClaimStoppedController.onPageLoad()
      case (Some(taxYears), None) =>
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
