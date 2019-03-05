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

import play.twirl.api.HtmlFormat
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.behaviours.ViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBehaviours {

  val application = applicationBuilder().build()

  "Index view" must {

    val view = application.injector.instanceOf[CheckYourAnswersView]

    val cyaHelper = new CheckYourAnswersHelper(minimumUserAnswers)

    val sections = Seq(AnswerSection(None, Seq(
      cyaHelper.industryType,
      cyaHelper.employerContribution,
      cyaHelper.expensesEmployerPaid,
      cyaHelper.taxYearSelection,
      cyaHelper.yourAddress
    ).flatten))

    def applyView(): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections)(fakeRequest, messages)

    def applyViewWithAuth(): HtmlFormat.Appendable =
      application.injector.instanceOf[CheckYourAnswersView].apply(sections)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(), "checkYourAnswers")

    behave like pageWithAccountMenu(applyViewWithAuth())
  }

  application.stop()
}