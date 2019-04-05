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

import models.{FlatRateExpenseOptions, Rates, ScottishRate, StandardRate}
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import service.ClaimAmountService
import views.behaviours.ViewBehaviours
import views.html.confirmation.CurrentYearConfirmationView

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
                   claimAmountsAndRates: Seq[Rates] = Seq(claimAmountsRates, scottishClaimAmountsRates),
                   claimAmount: Int = claimAmount,
                   updateEmployer: Boolean = false,
                   updateAddress: Boolean = false,
                   freResponse: FlatRateExpenseOptions = FlatRateExpenseOptions.FRENoYears
                 )(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(claimAmountsAndRates, claimAmount, updateEmployer, updateAddress, freResponse)(fakeRequest, messages)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(viewWithAnswers, "confirmation")

    behave like pageWithAccountMenu(applyViewWithAuth)

    "display correct static text" in {

      val doc = asDocument(viewWithAnswers)

      assertContainsMessages(doc,
        "confirmation.heading",
        "confirmation.actualAmount",
        "confirmation.whatHappensNext",
        "confirmation.taxCodeChanged",
        "confirmation.continueToClaim")
    }

    "display correct dynamic text for tax rates" in {

      val doc = asDocument(viewWithAnswers)

      assertContainsText(doc, messages("confirmation.personalAllowanceIncrease", claimAmount))
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

      doc.getElementsByTag("title").text mustBe "Claim changed - Employee Expenses - GOV.UK"
    }

    "display correct title when freResponse is 'FREAllYearsAllAmountsSameAsClaimAmount'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount)(fakeRequest, messages))

      doc.getElementsByTag("title").text mustBe "Claim changed - Employee Expenses - GOV.UK"
    }

    "display correct title when freResponse is 'FRENoYears'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FRENoYears)(fakeRequest, messages))

      doc.getElementsByTag("title").text mustBe "Claim completed - Employee Expenses - GOV.UK"
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
