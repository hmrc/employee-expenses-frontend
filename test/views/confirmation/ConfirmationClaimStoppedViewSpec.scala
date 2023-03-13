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

package views.confirmation

import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.newBehaviours.ViewBehaviours
import views.html.confirmation.ConfirmationClaimStoppedView

class ConfirmationClaimStoppedViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ClaimStoppedConfirmation view" must {

    val view = application.injector.instanceOf[ConfirmationClaimStoppedView]

    def applyView()(fakeRequest: FakeRequest[AnyContent], messages: Messages): Html =
      view()(fakeRequest, messages)

    val viewWithAnswers = applyView()(fakeRequest, messages)

    val applyViewWithAuth = applyView()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like pageWithAccountMenu(applyViewWithAuth)

    "display correct static text" in {

      val doc = asDocument(viewWithAnswers)

      assertContainsMessages(doc,
        "confirmation.heading.stoppedClaim",
        "confirmation.noLongerGetAmount"
      )
    }

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(viewWithAnswers)
          assertRenderedByCssSelector(doc, "div.govuk-header__content")
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
            s"${messages(s"confirmation.titleStopped")} - ${frontendAppConfig.serviceTitle}"
          )
        }

        "display the correct page title" in {

          val doc = asDocument(viewWithAnswers)
          assertPageTitleEqualsMessage(doc, s"confirmation.heading.stoppedClaim")
        }

        "display language toggles" in {

          val doc = asDocument(viewWithAnswers)
          assertRenderedByCssSelector(doc, ".hmrc-language-select")
        }
      }
    }
  }

  application.stop()
}
