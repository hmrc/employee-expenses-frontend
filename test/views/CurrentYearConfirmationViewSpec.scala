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

package views

import models.{Rates, ScottishRate, StandardRate}
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import service.ClaimAmountService
import views.behaviours.ViewBehaviours
import views.html.CurrentYearConfirmationView

class CurrentYearConfirmationViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "CurrentYearConfirmation view" must {

    val view = application.injector.instanceOf[CurrentYearConfirmationView]
    val claimAmountService = application.injector.instanceOf[ClaimAmountService]

    val claimAmount: Int = 100

    val claimAmountsRates = StandardRate(
      basicRate = frontendAppConfig.taxPercentageBand1,
      higherRate = frontendAppConfig.taxPercentageBand2,
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount)
    )

    val scottishClaimAmountsRates = ScottishRate(
      starterRate = frontendAppConfig.taxPercentageScotlandBand1,
      basicRate = frontendAppConfig.taxPercentageScotlandBand2,
      higherRate = frontendAppConfig.taxPercentageScotlandBand3,
      calculatedStarterRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand1, claimAmount),
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand2, claimAmount),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand3, claimAmount)
    )

    def applyView(
                  removeFREOption: Boolean = false,
                  claimAmountsAndRates: Seq[Rates] = Seq(claimAmountsRates),
                  claimAmount: Int = claimAmount,
                  updateEmployer: Boolean = false,
                  updateAddress: Boolean = false
                  )(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(removeFREOption, claimAmountsAndRates, claimAmount, updateEmployer, updateAddress)(fakeRequest, messages)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(viewWithAnswers, "confirmation")

    behave like pageWithAccountMenu(applyViewWithAuth)
  }

  application.stop()
}
