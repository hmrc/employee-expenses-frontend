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

import views.newBehaviours.ViewBehaviours
import views.html.HowYouWillGetYourExpensesCurrentView

class HowYouWillGetYourExpensesCurrentViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "HowYouWillGetYourExpensesCurrent view" must {

    val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentView]

    def applyView(hasClaimIncreased: Boolean = true) =
      view.apply("onwardRoute", hasClaimIncreased)(fakeRequest, messages)

    val applyViewWithAuth =
      view.apply("onwardRoute", true)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave.like(normalPage(applyView(), "howYouWillGetYourExpenses"))

    behave.like(pageWithAccountMenu(applyViewWithAuth))

    behave.like(pageWithBackLink(applyView()))

    "displays corrects text when claim amount increases" in {

      val doc = asDocument(applyView())

      assertContainsMessages(doc, "howYouWillGetYourExpenses.item1less")
    }

    "displays corrects text when claim amount decreases" in {

      val doc = asDocument(applyView(false))

      assertContainsMessages(doc, "howYouWillGetYourExpenses.item1more")
    }
  }

  application.stop()
}
