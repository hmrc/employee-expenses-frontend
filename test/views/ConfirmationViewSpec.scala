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

import models.TaxYearSelection
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.behaviours.ViewBehaviours
import views.html.ConfirmationView

class ConfirmationViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "Confirmation view" must {

    val view = application.injector.instanceOf[ConfirmationView]

    def applyView(taxYearSelection: Seq[TaxYearSelection],
                  removeFREOption: Option[TaxYearSelection] = None,
                  updateEmployer: Option[Boolean] = None,
                  updateAddress: Option[Boolean] = None,
                  claimAmount: Int = 100,
                  basicRate: Int = 20,
                  higherRate: Int = 40,
                  claimAmountBasicRate: String = "20",
                  claimAmountHigherRate: String = "40")(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view.apply(taxYearSelection, removeFREOption, updateEmployer, updateAddress, claimAmount, basicRate, higherRate, claimAmountBasicRate, claimAmountHigherRate)(fakeRequest, messages)

    val viewWithAnswers = applyView(
      taxYearSelection = Seq(TaxYearSelection.CurrentYear))(fakeRequest, messages)

    val applyViewWithAuth = applyView(
      taxYearSelection = Seq(TaxYearSelection.CurrentYear))(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(viewWithAnswers, "confirmation")

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(viewWithAnswers)

    "when only CY is selected" must {

      val viewWithAnswers =
        applyView(Seq(TaxYearSelection.CurrentYear))(fakeRequest, messages)

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

        assertContainsText(doc, messages("confirmation.personalAllowanceIncrease", "100").toString)
        assertContainsText(doc, messages("confirmation.basicRate", "20", 20).toString)
        assertContainsText(doc, messages("confirmation.higherRate", "40", 40).toString)
      }
    }


    "when CY and previous years have been selected" must {

      val viewWithAnswers =
        applyView(Seq(TaxYearSelection.CurrentYear, TaxYearSelection.CurrentYearMinus1))(fakeRequest, messages)

      val doc = asDocument(viewWithAnswers)

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

        assertContainsText(doc, messages("confirmation.personalAllowanceIncrease", "100").toString)
        assertContainsText(doc, messages("confirmation.basicRate", "20", 20).toString)
        assertContainsText(doc, messages("confirmation.higherRate", "40", 40).toString)
      }
    }

    "when only previous years have been selected" must {

      val viewWithAnswers =
        applyView(Seq(TaxYearSelection.CurrentYearMinus1))(fakeRequest, messages)

      val doc = asDocument(viewWithAnswers)


      "display correct static text" in {

        assertContainsMessages(doc,
          "confirmation.heading",
          "confirmation.whatHappensNext",
          "confirmation.letterConfirmation"
         )
      }

      "display correct dynamic text for title and tax rates" in {
        assertContainsText(doc, messages("confirmation.basicRate", "20", 20).toString)
        assertContainsText(doc, messages("confirmation.higherRate", "40", 40).toString)
      }
    }

    "when removeFREOption has been selected" must {

      "display correct content" in {
        val viewWithAnswers =
          applyView(
            taxYearSelection = Seq(TaxYearSelection.CurrentYearMinus1),
            removeFREOption = Some(TaxYearSelection.CurrentYearMinus1))(fakeRequest, messages)

        val doc = asDocument(viewWithAnswers)

        assertContainsMessages(doc, "confirmation.heading.stoppedClaim", "confirmation.noLongerGetAmount")
      }
    }

    "when YourAddress is false" must {

      "display update address button and content" in {

        val viewWithAnswers =
          applyView(
            taxYearSelection = Seq(TaxYearSelection.CurrentYearMinus1),
            updateAddress = Some(false))(fakeRequest, messages)

        val doc = asDocument(viewWithAnswers)

        assertContainsMessages(doc, "confirmation.updateAddressInfo", "confirmation.addressChange")
        doc.getElementById("updateAddressInfoBtn").text mustBe messages("confirmation.updateAddressInfoNow")
      }
    }

    "when YourEmployer is false" must {

      "display update employer button and content" in {

        val viewWithAnswers =
          applyView(
            taxYearSelection = Seq(TaxYearSelection.CurrentYearMinus1),
            updateEmployer = Some(false))(fakeRequest, messages)

        val doc = asDocument(viewWithAnswers)

        assertContainsMessages(doc, "confirmation.updateEmployerInfo", "confirmation.employerChange")
        doc.getElementById("updateEmployerInfoBtn").text mustBe messages("confirmation.updateEmployerInfoNow")

      }
    }
  }

  application.stop()
}
