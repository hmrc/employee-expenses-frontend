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

package views.confirmation

import models.{FlatRateExpenseOptions, Rates, ScottishRate, StandardRate, TaxYearSelection}
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import service.ClaimAmountService
import views.behaviours.ViewBehaviours
import views.html.confirmation.ConfirmationCurrentAndPreviousYearsView

class ConfirmationCurrentAndPreviousYearsViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "PreviousCurrentYearsConfirmationView" must {

    val view = application.injector.instanceOf[ConfirmationCurrentAndPreviousYearsView]

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

    def applyView(claimAmountsAndRates: Seq[Rates] = Seq(claimAmountsRates, scottishClaimAmountsRates),
                  claimAmount: Int = claimAmount,
                  updateEmployer: Boolean = false,
                  updateAddress: Boolean = false,
                  currentYearMinus1: Boolean = true,
                  freResponse: FlatRateExpenseOptions = FlatRateExpenseOptions.FRENoYears
                 )(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(claimAmountsAndRates, claimAmount, Some(updateEmployer), Some(updateAddress), currentYearMinus1, freResponse)(fakeRequest, messages, frontendAppConfig)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(viewWithAnswers, "confirmation")

    behave like pageWithAccountMenu(applyViewWithAuth)

    "display correct static text" in {

      val doc = asDocument(viewWithAnswers)

      assertContainsMessages(doc,
        "confirmation.heading",
        messages("confirmation.personalAllowanceIncrease", claimAmount),
        "confirmation.actualAmount",
        "confirmation.whatHappensNext",
        "confirmation.currentTaxYear",
        "confirmation.taxCodeChanged.paragraph1",
        "confirmation.taxCodeChanged.paragraph2",
        "confirmation.continueToClaim.paragraph1",
        "confirmation.continueToClaim.paragraph2",
        "confirmation.previousTaxYears",
        "confirmation.additionalConfirmationLetter",
        messages("confirmation.currentYearMinusOneDelay",
          TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYearMinus1).toString,
          TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYear).toString
        )
      )
    }

    "not display currentYearMinusOneDelay when currentYearMinus1 is false" in {

      val doc = asDocument(applyView(currentYearMinus1 = false)(fakeRequest, messages))

      assertTextNotRendered(doc,
        messages("confirmation.currentYearMinusOneDelay")
      )
    }

    "display correct dynamic text for tax rates" in {

      val doc = asDocument(viewWithAnswers)

      assertContainsText(doc, messages(
        "confirmation.basicRate",
        claimAmountsRates.calculatedBasicRate,
        claimAmount,
        claimAmountsRates.basicRate
      ))
      assertContainsText(doc, messages(
        "confirmation.higherRate",
        claimAmountsRates.calculatedHigherRate,
        claimAmount,
        claimAmountsRates.higherRate
      ))
      assertContainsText(doc, messages("claimAmount.englandHeading"))
      assertContainsText(doc, messages("claimAmount.scotlandHeading"))
    }

    "display correct title when freResponse is 'FREAllYearsAmountsDifferent'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FREAllYearsAllAmountsDifferent)(fakeRequest, messages))

      doc.getElementsByTag("title").text mustBe "Claim changed - Claim for your work uniform and tools - GOV.UK"
    }

    "display correct title when freResponse is 'FREAllYearsAllAmountsSameAsClaimAmount'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount)(fakeRequest, messages))

      doc.getElementsByTag("title").text mustBe "Claim changed - Claim for your work uniform and tools - GOV.UK"
    }

    "display correct title when freResponse is 'FRENoYears'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FRENoYears)(fakeRequest, messages))

      doc.getElementsByTag("title").text mustBe "Claim completed - Claim for your work uniform and tools - GOV.UK"
    }

    "YourAddress" must {

      "display update address button and content when 'false'" in {

        val doc = asDocument(applyView()(fakeRequest, messages))

        assertContainsMessages(doc, "confirmation.updateAddressInfo", "confirmation.addressChange")
        doc.getElementById("updateAddressInfoBtn").text mustBe messages("confirmation.updateAddressInfoNow")
      }

      "not display update address button and content when 'true'" in {

        val doc = asDocument(applyView(updateAddress = true)(fakeRequest, messages))

        assertNotRenderedById(doc, "updateAddressInfoBtn")
      }
    }

    "YourEmployer" must {

      "display update employer button and content when 'false'" in {

        val viewWithSpecificAnswers = applyView()(fakeRequest, messages)

        val doc = asDocument(viewWithSpecificAnswers)

        assertContainsMessages(doc, "confirmation.updateEmployerInfo", "confirmation.employerChange")
        doc.getElementById("updateEmployerInfoBtn").text mustBe messages("confirmation.updateEmployerInfoNow")
      }

      "not display update employer button and content when 'true'" in {

        val viewWithSpecificAnswers = applyView(updateEmployer = true)(fakeRequest, messages)

        val doc = asDocument(viewWithSpecificAnswers)

        assertNotRenderedById(doc, "updateEmployerInfoNow")
      }
    }

  }

  application.stop()
}
