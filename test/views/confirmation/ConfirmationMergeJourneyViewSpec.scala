/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.html.confirmation.ConfirmationMergeJourneyView
import views.newBehaviours.ViewBehaviours

class ConfirmationMergeJourneyViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder(userAnswers = Some(currentYearFullUserAnswers)).build()

  "ConfirmationMergeJourney view" must {

    val view = application.injector.instanceOf[ConfirmationMergeJourneyView]

    def applyView()(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view(continueUrl = "some-url")(fakeRequest, messages)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave.like(pageWithAccountMenu(applyViewWithAuth))

    val doc = asDocument(viewWithAnswers)

    "display correct static text" in
      assertContainsMessages(doc, "confirmationMergeJourney.para")

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {
          val doc = asDocument(viewWithAnswers)
          assertRenderedByCssSelector(doc, "div.govuk-header__content")
        }

        "have a confirmation panel" in {
          val doc = asDocument(viewWithAnswers)
          assertRenderedByClass(doc, "govuk-panel govuk-panel--confirmation")
        }

        "have a warning message" in {
          val doc = asDocument(viewWithAnswers)
          assertRenderedByClass(doc, "govuk-warning-text__text")
        }

        "hide account menu when user not logged in" in {

          val doc = asDocument(viewWithAnswers)
          assertNotRenderedById(doc, "secondary-nav")
        }

        "display the correct browser title" in {

          val doc = asDocument(viewWithAnswers)
          assertEqualsMessage(
            doc,
            "title",
            s"${messages(s"confirmationMergeJourney.title")} - ${frontendAppConfig.serviceTitle}"
          )
        }

        "display the correct page title" in {

          val doc = asDocument(viewWithAnswers)
          assertPageTitleEqualsMessage(doc, s"confirmationMergeJourney.title")
        }

        "display language toggles" in {

          val doc = asDocument(viewWithAnswers)
          assertRenderedByCssSelector(doc, ".hmrc-language-select")
        }

        "display a continue buttow with correct url" in {
          val doc = asDocument(viewWithAnswers)
          doc.select(".govuk-button").attr("href") mustBe "some-url"
        }
      }
    }
  }

  application.stop()

}
