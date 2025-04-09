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

package views

import models.TaxYearSelection._
import views.newBehaviours.ViewBehaviours
import views.html.HowYouWillGetYourExpensesPreviousView

class HowYouWillGetYourExpensesPreviousViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "HowYouWillGetYourExpensesPreviousYearView" must {

    val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

    def applyView(currentYearMinus1: Boolean, authorised: Boolean = true) = {
      val request = if (authorised) fakeRequest.withSession(("authToken", "SomeAuthToken")) else fakeRequest
      view.apply("onwardRoute", currentYearMinus1)(request, messages)
    }

    behave.like(normalPage(applyView(true, false), "howYouWillGetYourExpenses"))

    behave.like(pageWithAccountMenu(applyView(true)))

    behave.like(pageWithBackLink(applyView(true)))

    "does show paragraph when CY-1 is selected" must {
      val wantedMessage = messages(
        "howYouWillGetYourExpensesPrevious.para2",
        taxYearStartString(1),
        taxYearEndString(1)
      )

      behave.like(
        pageWithBodyText(
          applyView(true),
          wantedMessage
        )
      )
    }

    "does not show paragraph when CY-1 is not selected" in {
      val doc = asDocument(applyView(false))

      val unwantedMessage = messages(
        "howYouWillGetYourExpensesPrevious.para2",
        taxYearStartString(1),
        taxYearEndString(1)
      )

      assertTextNotRendered(doc, unwantedMessage)
    }
  }

  application.stop()
}
