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

package views.confirmation

import models.{Address, FlatRateExpenseOptions, Rates, ScottishRate, StandardRate}
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import service.ClaimAmountService
import views.newBehaviours.ViewBehaviours
import views.html.confirmation.ConfirmationCurrentAndPreviousYearsView

class ConfirmationCurrentAndPreviousYearsViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "PreviousCurrentYearsConfirmationView" must {

    val view = application.injector.instanceOf[ConfirmationCurrentAndPreviousYearsView]

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
      advancedRate = frontendAppConfig.taxPercentageScotlandAdvancedRate,
      topRate = frontendAppConfig.taxPercentageScotlandTopRate,
      calculatedStarterRate =
        claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandStarterRate, claimAmount),
      calculatedBasicRate =
        claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBasicRate, claimAmount),
      calculatedIntermediateRate =
        claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandIntermediateRate, claimAmount),
      calculatedHigherRate =
        claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandHigherRate, claimAmount),
      calculatedAdvancedRate =
        claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandAdvancedRate, claimAmount),
      calculatedTopRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandTopRate, claimAmount)
    )

    def applyView(
        claimAmountsAndRates: Seq[Rates] = Seq(claimAmountsRates, scottishClaimAmountsRates),
        claimAmount: Int = claimAmount,
        updateEmployer: Option[Boolean] = Some(false),
        updateAddress: Boolean = false,
        currentYearMinus1: Boolean = true,
        freResponse: FlatRateExpenseOptions = FlatRateExpenseOptions.FRENoYears,
        address: Option[Address] = None,
        hasClaimIncreased: Boolean = true,
        npsFreAmount: Int = 0
    )(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(
        claimAmountsAndRates,
        claimAmount,
        updateEmployer,
        address,
        hasClaimIncreased,
        freResponse,
        npsFreAmount
      )(fakeRequest, messages)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave.like(normalPage(viewWithAnswers, "confirmation"))

    behave.like(pageWithAccountMenu(applyViewWithAuth))

    "display correct static text" in {

      val doc = asDocument(viewWithAnswers)

      assertContainsMessages(
        doc,
        "confirmation.heading",
        messages("confirmation.newPersonalAllowance", claimAmount),
        "confirmation.whatHappensNext",
        "confirmation.currentTaxYear",
        "confirmation.taxCodeChanged.currentYear.paragraph1",
        "confirmation.taxCodeChanged.currentYear.paragraph2",
        "confirmation.previousTaxYears",
        "confirmation.additionalConfirmationLetter"
      )
    }

    "not display currentYearMinusOneDelay when currentYearMinus1 is false" in {

      val doc = asDocument(applyView(currentYearMinus1 = false)(fakeRequest, messages))

      assertTextNotRendered(doc, messages("confirmation.currentYearMinusOneDelay"))
    }

    "display correct dynamic text for tax rates" in {

      val doc = asDocument(viewWithAnswers)

      assertContainsText(
        doc,
        messages(
          "confirmation.basicRate",
          claimAmountsRates.calculatedBasicRate,
          claimAmount,
          claimAmountsRates.basicRate
        )
      )
      assertContainsText(
        doc,
        messages(
          "confirmation.higherRate",
          claimAmountsRates.calculatedHigherRate,
          claimAmount,
          claimAmountsRates.higherRate
        )
      )
      assertContainsText(
        doc,
        messages(
          "confirmation.intermediateRate",
          scottishClaimAmountsRates.calculatedIntermediateRate,
          claimAmount,
          scottishClaimAmountsRates.intermediateRate
        )
      )
      assertContainsText(doc, messages("claimAmount.englandHeading"))
      assertContainsText(doc, messages("claimAmount.scotlandHeading"))
    }

    "display correct title when freResponse is 'FREAllYearsAmountsDifferent'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FRESomeYears)(fakeRequest, messages))

      doc.title mustBe "Claim changed - Claim for your work uniform and tools - GOV.UK"
    }

    "display correct title when freResponse is 'FREAllYearsAllAmountsSameAsClaimAmount'" in {

      val doc = asDocument(
        applyView(freResponse = FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount)(fakeRequest, messages)
      )

      doc.title mustBe "Claim changed - Claim for your work uniform and tools - GOV.UK"
    }

    "display correct title when freResponse is 'FRENoYears'" in {

      val doc = asDocument(applyView(freResponse = FlatRateExpenseOptions.FRENoYears)(fakeRequest, messages))

      doc.title mustBe "Claim complete for uniform, work clothing and tools - Claim for your work uniform and tools - GOV.UK"
    }

    "YourAddress" must {

      "display address" in {

        val doc = asDocument(applyView(address = Some(address))(fakeRequest, messages))

        assertRenderedById(doc, "citizenDetailsAddress")
      }

      "display correct content when no address" in {

        val doc = asDocument(applyView()(fakeRequest, messages))

        assertNotRenderedById(doc, "citizenDetailsAddress")
        assertRenderedById(doc, "no-address")
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

        val viewWithSpecificAnswers = applyView()(fakeRequest, messages)

        val doc = asDocument(viewWithSpecificAnswers)

        assertNotRenderedById(doc, "updateEmployerInfoNow")
      }
    }

  }

  application.stop()
}
