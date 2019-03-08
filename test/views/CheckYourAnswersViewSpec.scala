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

    def applyView(removeFRE: Boolean): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, removeFRE)(fakeRequest, messages)

    def applyViewWithAuth(removeFRE: Boolean): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections, removeFRE)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(true), "checkYourAnswers")

    behave like pageWithAccountMenu(applyViewWithAuth(true))

    "display correct content" when {
      "removeFRE is false" in {
        val doc = asDocument(applyView(false))

        assertContainsMessages(doc,
          "checkYourAnswers.heading.claimExpenses",
          "checkYourAnswers.claimExpenses",
          "checkYourAnswers.confirmInformation",
          "checkYourAnswers.prosecuted"
        )

        doc.getElementById("submit").text mustBe messages("site.accept")
      }

      "removeFRE is true" in {
        val doc = asDocument(applyView(true))

        assertContainsMessages(doc, "checkYourAnswers.heading")

        doc.getElementById("submit").text mustBe messages("site.save")
      }
    }
  }

  application.stop()
}
