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

package views.confirmation

import models.{FlatRateExpenseOptions, Rates, ScottishRate, StandardRate}
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import service.ClaimAmountService
import views.behaviours.ViewBehaviours
import views.html.confirmation.ConfirmationCurrentYearOnlyView

class ConfirmationCurrentYearOnlyViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "CurrentYearConfirmation view" must {

    val view = application.injector.instanceOf[ConfirmationCurrentYearOnlyView]

    val claimAmountService = application.injector.instanceOf[ClaimAmountService]

    val claimAmount: Int = 100

    val claimAmountsRates = StandardRate(
      basicRate = frontendAppConfig.taxPercentageBasicRate,
      higherRate = frontendAppConfig.taxPercentageHigherRate,
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBasicRate, claimAmount),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageHigherRate, claimAmount)
    )

    val scottishClaimAmountsRates = ScottishRate(
      starterRate = frontendAppConfig.taxPercentageScotlandStarterRate,
      basicRate = frontendAppConfig.taxPercentageScotlandBasicRate,
      intermediateRate = frontendAppConfig.taxPercentageScotlandIntermediateRate,
      higherRate = frontendAppConfig.taxPercentageScotlandHigherRate,
      calculatedStarterRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandStarterRate, claimAmount),
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBasicRate, claimAmount),
      calculatedIntermediateRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandIntermediateRate, claimAmount),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandHigherRate, claimAmount)
    )

    def applyView(
                   claimAmountsAndRates: Seq[Rates] = Seq(claimAmountsRates, scottishClaimAmountsRates),
                   claimAmount: Int = claimAmount,
                   updateEmployer: Boolean = false,
                   updateAddress: Boolean = false,
                   hasClaimIncreased: Boolean = true,
                   freResponse: FlatRateExpenseOptions = FlatRateExpenseOptions.FRENoYears,
                   npsFreAmount: Int = 0
                 )(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(
        claimAmountsAndRates = claimAmountsAndRates,
        claimAmount = claimAmount,
        employerCorrect = Some(updateEmployer),
        address = Option(address),
        hasClaimIncreased = hasClaimIncreased,
        freResponse = freResponse,
        npsFreAmount = npsFreAmount
      )(fakeRequest, messages, frontendAppConfig)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(viewWithAnswers, "confirmation")

    behave like pageWithAccountMenu(applyViewWithAuth)

    "display correct static text for an increase in FRE" in {
      val doc = asDocument(applyView(npsFreAmount = 1)(fakeRequest, messages))

      assertContainsMessages(doc,
        "confirmation.heading",
        messages("confirmation.personalAllowanceIncrease", 1, claimAmount),
        "confirmation.whatHappensNext",
        "confirmation.taxCodeChanged.currentYear.paragraph1",
        "confirmation.taxCodeChanged.currentYear.paragraph2",
        "confirmation.checkAddress.heading",
        "confirmation.checkAddress.paragraph1",
        "confirmation.checkAddress.paragraph2"
      )
    }

    "display correct static text for an decrease in FRE" in {

      val doc = asDocument(applyView(
        hasClaimIncreased = false,
        npsFreAmount = 200
      )(fakeRequest, messages))

      assertContainsMessages(doc,
        "confirmation.heading",
        messages("confirmation.personalAllowanceDecrease", 200, claimAmount),
        "confirmation.whatHappensNext",
        "confirmation.taxCodeChanged.currentYear.paragraph1",
        "confirmation.taxCodeChanged.currentYear.paragraph2",
        "confirmation.checkAddress.heading",
        "confirmation.checkAddress.paragraph1",
        "confirmation.checkAddress.paragraph2"
      )
    }

    "display correct static text for no previous  FRE" in {

      val doc = asDocument(applyView()(fakeRequest, messages))

      assertContainsMessages(doc,
        "confirmation.heading",
        messages("confirmation.newPersonalAllowance", claimAmount),
        "confirmation.whatHappensNext",
        "confirmation.taxCodeChanged.currentYear.paragraph1",
        "confirmation.taxCodeChanged.currentYear.paragraph2",
        "confirmation.checkAddress.heading",
        "confirmation.checkAddress.paragraph1",
        "confirmation.checkAddress.paragraph2"
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
      assertContainsText(doc, messages(
        "confirmation.intermediateRate",
        scottishClaimAmountsRates.calculatedIntermediateRate,
        claimAmount,
        scottishClaimAmountsRates.intermediateRate
      ))
      assertContainsText(doc, messages("claimAmount.englandHeading"))
      assertContainsText(doc, messages("claimAmount.scotlandHeading"))
    }

    "display correct title when freResponse is 'FREAllYearsAmountsDifferent'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FRESomeYears)(fakeRequest, messages))

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

      "display update content when 'false'" in {

        val doc = asDocument(applyView()(fakeRequest, messages))

        assertContainsMessages(doc, "confirmation.checkAddress.paragraph1")
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
