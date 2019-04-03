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

import models.{Rates, ScottishRate, StandardRate, TaxYearSelection}
import models.{FlatRateExpenseOptions, Rates, TaxYearSelection}
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import service.ClaimAmountService
import views.behaviours.ViewBehaviours
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "Confirmation view" must {
    val view = application.injector.instanceOf[ConfirmationView]
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

    def applyView(taxYearSelection: Seq[TaxYearSelection] = Seq(TaxYearSelection.CurrentYear),
                  removeFREOption: Option[TaxYearSelection] = None,
                  updateEmployer: Option[Boolean] = None,
                  updateAddress: Option[Boolean] = None,
                  claimAmount: Int = claimAmount,
                  claimAmountsAndRates: Seq[Rates] = Seq(claimAmountsRates),
                  freResponse: FlatRateExpenseOptions = FlatRateExpenseOptions.FRENoYears)(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(taxYearSelection, removeFREOption, updateEmployer, updateAddress, claimAmount, claimAmountsAndRates, freResponse)(fakeRequest, messages)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(viewWithAnswers, "confirmation")

    behave like pageWithAccountMenu(applyViewWithAuth)

    "display claimStopped title" in {
      val claimStoppedView =
        applyView(removeFREOption = Some(TaxYearSelection.CurrentYearMinus1))(fakeRequest, messages)

      val doc = asDocument(claimStoppedView)

      assertEqualsMessage(doc, "title", s"${messages("confirmation.titleStopped")} - ${frontendAppConfig.serviceTitle}")
    }

    "display claimChanged title" in {
      val claimChangedView =
        applyView(freResponse = FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount)(fakeRequest, messages)

      val doc = asDocument(claimChangedView)

      assertEqualsMessage(doc, "title", s"${messages("confirmation.titleChanged")} - ${frontendAppConfig.serviceTitle}")

    }

    "when english tax record" must {

      val viewWithSpecificAnswers =
        applyView(claimAmountsAndRates = Seq(claimAmountsRates))(fakeRequest, messages)

      val doc = asDocument(viewWithSpecificAnswers)

      "display correct dynamic tax rates" in {
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
      }
    }

    "when scottish tax record" must {

      val viewWithSpecificAnswers =
        applyView(claimAmountsAndRates = Seq(scottishClaimAmountsRates))(fakeRequest, messages)

      val doc = asDocument(viewWithSpecificAnswers)

      "display correct dynamic tax rates" in {
        assertContainsText(doc, messages(
          "confirmation.starterRate",
          scottishClaimAmountsRates.calculatedStarterRate,
          claimAmount,
          scottishClaimAmountsRates.starterRate
        ))
        assertContainsText(doc, messages(
          "confirmation.basicRate",
          scottishClaimAmountsRates.calculatedBasicRate,
          claimAmount,
          scottishClaimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          scottishClaimAmountsRates.calculatedHigherRate,
          claimAmount,
          scottishClaimAmountsRates.higherRate
        ))
      }
    }

    "when empty tax record" must {

      val viewWithSpecificAnswers =
        applyView(claimAmountsAndRates = Seq(claimAmountsRates, scottishClaimAmountsRates))(fakeRequest, messages)

      val doc = asDocument(viewWithSpecificAnswers)

      "display correct dynamic tax rates and headings" in {
        assertContainsText(doc, messages("confirmation.englandHeading"))
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
        assertContainsText(doc, messages("confirmation.scotlandHeading"))
        assertContainsText(doc, messages(
          "confirmation.starterRate",
          scottishClaimAmountsRates.calculatedStarterRate,
          claimAmount,
          scottishClaimAmountsRates.starterRate
        ))
        assertContainsText(doc, messages(
          "confirmation.basicRate",
          scottishClaimAmountsRates.calculatedBasicRate,
          claimAmount,
          scottishClaimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          scottishClaimAmountsRates.calculatedHigherRate,
          claimAmount,
          scottishClaimAmountsRates.higherRate
        ))
      }
    }

    "when only CY is selected" must {

      val doc = asDocument(viewWithAnswers)

      "display correct static text" in {

        assertContainsMessages(doc,
          "confirmation.heading",
          "confirmation.actualAmount",
          "confirmation.whatHappensNext",
          "confirmation.taxCodeChanged",
          "confirmation.continueToClaim")
      }

      "display correct dynamic text for title and tax rates" in {

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
      }
    }
    "when CY minus 1 has not been been selected" must {

      val viewWithSpecificAnswers =
        applyView(Seq(TaxYearSelection.CurrentYear,TaxYearSelection.CurrentYearMinus2))(fakeRequest, messages)

      val doc = asDocument(viewWithSpecificAnswers)

      "display correct static text" in {

        assertContainsMessages(doc,
          "confirmation.heading",
          "confirmation.actualAmount",
          "confirmation.whatHappensNext",
          "confirmation.currentTaxYear",
          "confirmation.taxCodeChanged",
          "confirmation.continueToClaim",
          "confirmation.previousTaxYears",
          "confirmation.additionalConfirmationLetter"

        )

        assertTextNotRendered(doc,
          messages("confirmation.currentYearMinusOneDelay")
        )

      }
    }

    "when CY and previous years have been selected" must {

      val viewWithSpecificAnswers =
        applyView(Seq(TaxYearSelection.CurrentYear, TaxYearSelection.CurrentYearMinus1))(fakeRequest, messages)

      val doc = asDocument(viewWithSpecificAnswers)

      "display correct static text" in {

        assertContainsMessages(doc,
          "confirmation.heading",
          "confirmation.actualAmount",
          "confirmation.whatHappensNext",
          "confirmation.currentTaxYear",
          "confirmation.taxCodeChanged",
          "confirmation.continueToClaim",
          "confirmation.previousTaxYears",
          "confirmation.additionalConfirmationLetter"
        )

        assertContainsText(doc,
          messages(
            "confirmation.currentYearMinusOneDelay",
            TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYearMinus1).toString,
            TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYear).toString
          ))
      }

      "display correct dynamic text for title and tax rates" in {

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
      }
    }

    "when only previous years have been selected" must {

      val viewWithSpecificAnswers =
        applyView(Seq(TaxYearSelection.CurrentYearMinus1))(fakeRequest, messages)

      val doc = asDocument(viewWithSpecificAnswers)


      "display correct static text" in {

        assertContainsMessages(doc,
          "confirmation.heading",
          "confirmation.whatHappensNext",
          "confirmation.confirmationLetter"
        )
      }

      "display correct dynamic text for title and tax rates" in {
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
      }
    }

    "when removeFREOption has been selected" must {

      "display correct content" in {
        val viewWithSpecificAnswers =
          applyView(
            taxYearSelection = Seq(TaxYearSelection.CurrentYearMinus1),
            removeFREOption = Some(TaxYearSelection.CurrentYearMinus1))(fakeRequest, messages)

        val doc = asDocument(viewWithSpecificAnswers)

        assertContainsMessages(doc, "confirmation.heading.stoppedClaim", "confirmation.noLongerGetAmount")
      }
    }

    "when YourAddress is false" must {

      "display update address button and content" in {

        val viewWithSpecificAnswers =
          applyView(
            taxYearSelection = Seq(TaxYearSelection.CurrentYearMinus1),
            updateAddress = Some(false))(fakeRequest, messages)

        val doc = asDocument(viewWithSpecificAnswers)

        assertContainsMessages(doc, "confirmation.updateAddressInfo", "confirmation.addressChange")
        doc.getElementById("updateAddressInfoBtn").text mustBe messages("confirmation.updateAddressInfoNow")
      }
    }

    "when YourEmployer is false" must {

      "display update employer button and content" in {

        val viewWithSpecificAnswers =
          applyView(
            taxYearSelection = Seq(TaxYearSelection.CurrentYearMinus1),
            updateEmployer = Some(false))(fakeRequest, messages)

        val doc = asDocument(viewWithSpecificAnswers)

        assertContainsMessages(doc, "confirmation.updateEmployerInfo", "confirmation.employerChange")
        doc.getElementById("updateEmployerInfoBtn").text mustBe messages("confirmation.updateEmployerInfoNow")

      }
    }
  }

  application.stop()
}
