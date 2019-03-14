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

import models.{Rates, TaxYearSelection}
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

    val claimAmountsRates: Rates = Rates(
      basicRate = frontendAppConfig.taxPercentageBand1,
      higherRate = frontendAppConfig.taxPercentageBand2,
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount),
      prefix = None
    )

    val scottishClaimAmountsRates = Rates(
      basicRate = frontendAppConfig.taxPercentageScotlandBand1,
      higherRate = frontendAppConfig.taxPercentageScotlandBand2,
      calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand1, claimAmount),
      calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand2, claimAmount),
      prefix = Some('S')
    )

    def applyView(taxYearSelection: Seq[TaxYearSelection] = Seq(TaxYearSelection.CurrentYear),
                  removeFREOption: Option[TaxYearSelection] = None,
                  updateEmployer: Option[Boolean] = None,
                  updateAddress: Option[Boolean] = None,
                  claimAmount: Int = claimAmount,
                  claimAmountsAndRates: Seq[Rates] = Seq(claimAmountsRates))(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(taxYearSelection, removeFREOption, updateEmployer, updateAddress, claimAmount, claimAmountsAndRates)(fakeRequest, messages)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(viewWithAnswers, "confirmation")

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(viewWithAnswers)

    "when english tax record" must {

      val viewWithSpecificAnswers =
        applyView(claimAmountsAndRates = Seq(claimAmountsRates))(fakeRequest, messages)

      val doc = asDocument(viewWithSpecificAnswers)

      "display correct dynamic tax rates" in {
        assertContainsText(doc, messages(
          "confirmation.basicRate",
          claimAmountsRates.calculatedBasicRate,
          claimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          claimAmountsRates.calculatedHigherRate,
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
          "confirmation.basicRate",
          scottishClaimAmountsRates.calculatedBasicRate,
          scottishClaimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          scottishClaimAmountsRates.calculatedHigherRate,
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
          claimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          claimAmountsRates.calculatedHigherRate,
          claimAmountsRates.higherRate
        ))
        assertContainsText(doc, messages("confirmation.scotlandHeading"))
        assertContainsText(doc, messages(
          "confirmation.basicRate",
          scottishClaimAmountsRates.calculatedBasicRate,
          scottishClaimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          scottishClaimAmountsRates.calculatedHigherRate,
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
          claimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          claimAmountsRates.calculatedHigherRate,
          claimAmountsRates.higherRate
        ))
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
          "confirmation.letterConfirmation")
      }

      "display correct dynamic text for title and tax rates" in {

        assertContainsText(doc, messages("confirmation.personalAllowanceIncrease", claimAmount))
        assertContainsText(doc, messages(
          "confirmation.basicRate",
          claimAmountsRates.calculatedBasicRate,
          claimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          claimAmountsRates.calculatedHigherRate,
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
          "confirmation.letterConfirmation"
        )
      }

      "display correct dynamic text for title and tax rates" in {
        assertContainsText(doc, messages(
          "confirmation.basicRate",
          claimAmountsRates.calculatedBasicRate,
          claimAmountsRates.basicRate
        ))
        assertContainsText(doc, messages(
          "confirmation.higherRate",
          claimAmountsRates.calculatedHigherRate,
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
