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
import controllers.routes._
import javax.inject.Inject
import models.{CheckMode, FlatRateExpenseOptions, Mode, NormalMode, UserAnswers}
import pages.authenticated._
import pages.{FREResponse, Page}
import play.api.mvc.Call

class AuthenticatedNavigator @Inject()() extends Navigator {
  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case TaxYearSelectionPage             => taxYearSelection(NormalMode)
    case AlreadyClaimingFRESameAmountPage => alreadyClaimingFRESameAmount(NormalMode)
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case TaxYearSelectionPage => taxYearSelection(CheckMode)
  }

  def taxYearSelection(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(FREResponse) match {
    case Some(FlatRateExpenseOptions.FRENoYears) =>
      YourAddressController.onPageLoad(mode)
    case Some(FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount) =>
      AlreadyClaimingFRESameAmountController.onPageLoad(mode)
    case Some(FlatRateExpenseOptions.FREAllYearsAllAmountsDifferentToClaimAmount) =>
      RemoveFRECodeController.onPageLoad(mode)
    case Some(FlatRateExpenseOptions.ComplexClaim) =>
      PhoneUsController.onPageLoad()
    case Some(FlatRateExpenseOptions.TechnicalDifficulties) =>
      SessionExpiredController.onPageLoad()
    case _ =>
      SessionExpiredController.onPageLoad()
  }

  def alreadyClaimingFRESameAmount(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(AlreadyClaimingFRESameAmountPage) match {
      case Some(true) =>
        NoCodeChangeController.onPageLoad()
      case Some(false) =>
        RemoveFRECodeController.onPageLoad(NormalMode)
      case _ =>
        SessionExpiredController.onPageLoad()
    }

}
