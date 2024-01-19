/*
 * Copyright 2023 HM Revenue & Customs
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

import models.{ScottishRate, StandardRate}
import pages.ClaimAmount
import play.api.Application
import service.ClaimAmountService
import views.newBehaviours.ViewBehaviours
import views.html.ClaimAmountView

class ClaimAmountViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ClaimAmount view" must {

    val claimAmount: Int = 180
    val someEmployerContribution: Option[Int] = Some(10)

    val onwardRoute: String = "/employee-expenses/which-tax-year"

    val userAnswers = emptyUserAnswers.set(ClaimAmount, claimAmount).success.value

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val view = application.injector.instanceOf[ClaimAmountView]

    val claimAmountService = application.injector.instanceOf[ClaimAmountService]

    def claimAmountsAndRates(deduction: Option[Int] = None) = StandardRate(
      basicRate = frontendAppConfig.taxPercentageBasicRate,
      higherRate = frontendAppConfig.taxPercentageHigherRate,
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBasicRate, claimAmount - deduction.getOrElse(0)),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageHigherRate, claimAmount - deduction.getOrElse(0))
    )

    def scottishClaimAmountsAndRates(deduction: Option[Int] = None) = ScottishRate(
      starterRate = frontendAppConfig.taxPercentageScotlandStarterRate,
      basicRate = frontendAppConfig.taxPercentageScotlandBasicRate,
      intermediateRate = frontendAppConfig.taxPercentageScotlandIntermediateRate,
      higherRate = frontendAppConfig.taxPercentageScotlandHigherRate,
      calculatedStarterRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandStarterRate, claimAmount - deduction.getOrElse(0)),
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBasicRate, claimAmount - deduction.getOrElse(0)),
      calculatedIntermediateRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandIntermediateRate, claimAmount - deduction.getOrElse(0)),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandHigherRate, claimAmount - deduction.getOrElse(0))
    )

    val applyView = view.apply(
      claimAmount, None, claimAmountsAndRates(), scottishClaimAmountsAndRates(), onwardRoute
    )(fakeRequest, messages)

    val applyViewWithEmployerContribution = view.apply(
      claimAmount, someEmployerContribution, claimAmountsAndRates(someEmployerContribution), scottishClaimAmountsAndRates(someEmployerContribution), onwardRoute
    )(fakeRequest, messages)

    val applyViewWithAuth = view.apply(
      claimAmount, None, claimAmountsAndRates(), scottishClaimAmountsAndRates(), onwardRoute
    )(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView, "claimAmount")

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(applyView)

    behave like pageWithBodyText(
      applyViewWithEmployerContribution,
      messages("claimAmount.increasePA", claimAmount),
      "claimAmount.englandHeading",
      messages(
        "claimAmount.basicRate",
        claimAmountsAndRates(someEmployerContribution).basicRate,
        claimAmount,
        claimAmountsAndRates(someEmployerContribution).calculatedBasicRate
      ),
      messages(
        "claimAmount.higherRate",
        claimAmountsAndRates(someEmployerContribution).higherRate,
        claimAmount,
        claimAmountsAndRates(someEmployerContribution).calculatedHigherRate
      ),
      "claimAmount.scotlandHeading",
      messages(
        "claimAmount.starterRate",
        scottishClaimAmountsAndRates(someEmployerContribution).starterRate,
        claimAmount,
        scottishClaimAmountsAndRates(someEmployerContribution).calculatedStarterRate
      ),
      messages(
        "claimAmount.basicRate",
        scottishClaimAmountsAndRates(someEmployerContribution).basicRate,
        claimAmount,
        scottishClaimAmountsAndRates(someEmployerContribution).calculatedBasicRate
      ),
      messages(
        "claimAmount.higherRate",
        scottishClaimAmountsAndRates(someEmployerContribution).higherRate,
        claimAmount,
        scottishClaimAmountsAndRates(someEmployerContribution).calculatedHigherRate
      ),
      "claimAmount.howThisIsCalculated",
      "claimAmount.amountDepends",
      "claimAmount.industry",
      "claimAmount.whereYouLive",
      "claimAmount.rateOfTax",
      "claimAmount.employerContribution"
    )

    "hide employer contribution bullet point when no employer contribution has been applied" in {
      val doc = asDocument(applyView)

      assertTextNotRendered(doc, "claimAmount.employerContribution")
    }

    behave like pageWithButtonLink(applyView, onwardRoute, "site.continue")
  }

  application.stop()
}
