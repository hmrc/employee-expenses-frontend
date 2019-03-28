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

import models.Rates
import pages.ClaimAmount
import play.api.Application
import service.ClaimAmountService
import views.behaviours.ViewBehaviours
import views.html.ClaimAmountView

class ClaimAmountViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ClaimAmount view" must {

    val claimAmount: Int = 180
    val noEmployerContribution: Option[Int] = None
    val someEmployerContribution: Option[Int] = Some(10)

    val onwardRoute: String = "/employee-expenses/which-tax-year"

    val userAnswers = emptyUserAnswers.set(ClaimAmount, claimAmount).success.value

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val view = application.injector.instanceOf[ClaimAmountView]

    val claimAmountService = application.injector.instanceOf[ClaimAmountService]

    def claimAmountsAndRates(deduction: Option[Int]) = Rates(
      basicRate = frontendAppConfig.taxPercentageBand1,
      higherRate = frontendAppConfig.taxPercentageBand2,
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount - deduction.getOrElse(0)),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount - deduction.getOrElse(0)),
      prefix = None
    )

    def scottishClaimAmountsAndRates(deduction: Option[Int]) = Rates(
      starterRate = Some(frontendAppConfig.taxPercentageScotlandBand1),
      basicRate = frontendAppConfig.taxPercentageScotlandBand2,
      higherRate = frontendAppConfig.taxPercentageScotlandBand3,
      calculatedStarterRate = Some(claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand1, claimAmount - deduction.getOrElse(0))),
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand2, claimAmount - deduction.getOrElse(0)),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand3, claimAmount - deduction.getOrElse(0)),
      prefix = Some('S')
    )

    val applyView = view.apply(
      claimAmount, noEmployerContribution, claimAmountsAndRates(noEmployerContribution), scottishClaimAmountsAndRates(noEmployerContribution), onwardRoute
    )(fakeRequest, messages)

    val applyViewWithEmployerContribution = view.apply(
      claimAmount, someEmployerContribution, claimAmountsAndRates(someEmployerContribution), scottishClaimAmountsAndRates(someEmployerContribution), onwardRoute
    )(fakeRequest, messages)

    val applyViewWithAuth = view.apply(
      claimAmount, noEmployerContribution, claimAmountsAndRates(noEmployerContribution), scottishClaimAmountsAndRates(someEmployerContribution), onwardRoute
    )(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView, "claimAmount")

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(applyView)

    "Some employer contribution" when {
      "display correct text when" in {
        val doc = asDocument(applyViewWithEmployerContribution)
        assertContainsText(doc, messages("claimAmount.someContributionDescription", claimAmount - someEmployerContribution.get))
        assertContainsText(doc, messages("claimAmount.employerContributionDescription"))
      }
      "display relevant data" in {
        val doc = asDocument(applyViewWithEmployerContribution)
        assertContainsText(doc, messages(
          "claimAmount.basicRate",
          claimAmountsAndRates(someEmployerContribution).calculatedBasicRate,
          claimAmount,
          claimAmountsAndRates(someEmployerContribution).basicRate
        ))
        assertContainsText(doc, messages(
          "claimAmount.higherRate",
          claimAmountsAndRates(someEmployerContribution).calculatedHigherRate,
          claimAmount,
          claimAmountsAndRates(someEmployerContribution).higherRate
        ))
        assertContainsText(doc, messages(
          "claimAmount.starterRate",
          scottishClaimAmountsAndRates(someEmployerContribution).calculatedStarterRate.get,
          claimAmount,
          scottishClaimAmountsAndRates(someEmployerContribution).starterRate.get
        ))
        assertContainsText(doc, messages(
          "claimAmount.basicRate",
          scottishClaimAmountsAndRates(someEmployerContribution).calculatedBasicRate,
          claimAmount,
          scottishClaimAmountsAndRates(someEmployerContribution).basicRate
        ))
        assertContainsText(doc, messages(
          "claimAmount.higherRate",
          scottishClaimAmountsAndRates(someEmployerContribution).calculatedHigherRate,
          claimAmount,
          scottishClaimAmountsAndRates(someEmployerContribution).higherRate
        ))
      }
    }

    "No employer contribution" when {
      "display correct text when" in {
        val doc = asDocument(applyView)
        assertContainsText(doc, messages("claimAmount.noContributionDescription", claimAmount))
      }
      "display relevant data" in {
        val doc = asDocument(applyView)
        assertContainsText(doc, messages(
          "claimAmount.basicRate",
          claimAmountsAndRates(noEmployerContribution).calculatedBasicRate,
          claimAmount,
          claimAmountsAndRates(noEmployerContribution).basicRate
        ))
        assertContainsText(doc, messages(
          "claimAmount.higherRate",
          claimAmountsAndRates(noEmployerContribution).calculatedHigherRate,
          claimAmount,
          claimAmountsAndRates(noEmployerContribution).higherRate
        ))
        assertContainsText(doc, messages(
          "claimAmount.basicRate",
          scottishClaimAmountsAndRates(noEmployerContribution).calculatedBasicRate,
          claimAmount,
          scottishClaimAmountsAndRates(noEmployerContribution).basicRate
        ))
        assertContainsText(doc, messages(
          "claimAmount.higherRate",
          scottishClaimAmountsAndRates(noEmployerContribution).calculatedHigherRate,
          claimAmount,
          scottishClaimAmountsAndRates(noEmployerContribution).higherRate
        ))
      }
    }

    behave like pageWithBodyText(
      applyView,
      "claimAmount.description",
      "claimAmount.englandHeading",
      "claimAmount.scotlandHeading"
    )

    behave like pageWithButtonLink(applyView, onwardRoute, "site.continue")
  }

  application.stop()
}
