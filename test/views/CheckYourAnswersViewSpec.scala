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

import models.FlatRateExpenseOptions
import play.api.Application
import play.twirl.api.HtmlFormat
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.behaviours.ViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder().build()

  "Index view" must {

    val cyaHelper = new CheckYourAnswersHelper(fullUserAnswers)

    val sections = Seq(AnswerSection(None, Seq(
      cyaHelper.industryType,
      cyaHelper.employerContribution,
      cyaHelper.expensesEmployerPaid,
      cyaHelper.taxYearSelection,
      cyaHelper.yourAddress
    ).flatten))

    def applyView(freOption: FlatRateExpenseOptions, removeFRE: Boolean): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, checkYourAnswersTextStopFre)(fakeRequest, messages)

    def applyViewWithAuth(freOption: FlatRateExpenseOptions, removeFRE: Boolean): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, checkYourAnswersTextStopFre)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    def applyViewNewClaim(freOption: FlatRateExpenseOptions, removeFRE: Boolean): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, checkYourAnswersTextNoFre)(fakeRequest, messages)

    def applyViewChangeClaim(freOption: FlatRateExpenseOptions, removeFRE: Boolean): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, checkYourAnswersTextChangeFre)(fakeRequest, messages)

    behave like normalPage(applyView(FlatRateExpenseOptions.FRENoYears, removeFRE = true), "checkYourAnswers")

    behave like pageWithAccountMenu(applyViewWithAuth(FlatRateExpenseOptions.FRENoYears, removeFRE = true))

    "display correct content" when {

      "new claim has been made" in {
        val doc = asDocument(applyViewNewClaim(FlatRateExpenseOptions.FRENoYears, removeFRE = false))

        assertContainsMessages(doc,
          "checkYourAnswers.heading.claimExpenses",
          "checkYourAnswers.confirmInformationNoFre",
          "checkYourAnswers.claimExpenses"
        )

        doc.getElementById("submit").text mustBe messages("site.acceptClaimExpenses")
      }

      "claim has been changed" in {
        val doc = asDocument(applyViewChangeClaim(FlatRateExpenseOptions.FRESomeYears, removeFRE = false))

        assertContainsMessages(doc,
          "checkYourAnswers.heading.claimExpenses",
          "checkYourAnswers.confirmInformationChangeFre",
          "checkYourAnswers.changeClaim"
        )

        doc.getElementById("submit").text mustBe messages("site.acceptChangeClaim")
      }

      "claim has been stopped" in {
        val doc = asDocument(applyView(FlatRateExpenseOptions.FRENoYears, removeFRE = true))

        assertContainsMessages(doc,
          "checkYourAnswers.heading",
          "checkYourAnswers.confirmInformationChangeFre",
          "checkYourAnswers.stopClaim"
        )

        doc.getElementById("submit").text mustBe messages("site.acceptStopClaim")
      }
    }
  }

  application.stop()
}
