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
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, freOption, removeFRE)(fakeRequest, messages)

    def applyViewWithAuth(freOption: FlatRateExpenseOptions, removeFRE: Boolean): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, freOption, removeFRE)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(FlatRateExpenseOptions.FRENoYears, removeFRE = true), "checkYourAnswers")

    behave like pageWithAccountMenu(applyViewWithAuth(FlatRateExpenseOptions.FRENoYears, removeFRE = true))

    "display correct content" when {

      "new claim has been made" in {
        val doc = asDocument(applyView(FlatRateExpenseOptions.FRENoYears, removeFRE = false))

        assertContainsMessages(doc,
          "checkYourAnswers.heading.claimExpenses",
          "checkYourAnswers.confirmInformationNoFre"
        )

        doc.getElementById("submit").text mustBe messages("site.acceptClaimExpenses")
      }

      "claim has been changed" in {
        val doc = asDocument(applyView(FlatRateExpenseOptions.FREAllYearsAllAmountsDifferent, removeFRE = false))

        assertContainsMessages(doc,
          "checkYourAnswers.heading.claimExpenses",
          "checkYourAnswers.confirmInformationChangeFre"
        )

        doc.getElementById("submit").text mustBe messages("site.acceptChangeClaim")
      }

      "claim has been stopped" in {
        val doc = asDocument(applyView(FlatRateExpenseOptions.FRENoYears, removeFRE = true))

        assertContainsMessages(doc,
          "checkYourAnswers.heading",
          "checkYourAnswers.confirmInformationChangeFre"
        )

        doc.getElementById("submit").text mustBe messages("site.acceptStopClaim")
      }
    }
  }

  application.stop()
}
