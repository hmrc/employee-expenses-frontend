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

import models.UserAnswers
import pages.ClaimAmount
import play.api.libs.json.Json
import views.behaviours.ViewBehaviours
import views.html.ClaimAmountView

class ClaimAmountViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ClaimAmount view" must {

    val claimAmount = 180

    def userAnswers = UserAnswers(userAnswersId, Json.obj(ClaimAmount.toString -> 180))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val view = application.injector.instanceOf[ClaimAmountView]

    val applyView = view.apply(claimAmount, Some("20"), Some("30"))(fakeRequest, messages)

    "behave like a page with dynamic title" when {
      val doc = asDocument(applyView)
      "rendered" must {

        "have the correct banner title" in {

          val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first
        }

        "display the correct browser title" in {

          assertEqualsMessage(doc, "title", messages("claimAmount.title", claimAmount))
        }

        "display the correct page title" in {

          assertPageTitleEqualsMessage(doc, messages("claimAmount.heading", claimAmount))
        }

        "display language toggles" in {

          assertRenderedById(doc, "switchToWelsh")
        }
      }
    }

    behave like pageWithBackLink(applyView)

    "display relevant data" must {
      "claim amount from userAnswers" in {
        val doc = asDocument(applyView)
        assertContainsMessages(doc, "claimAmount.description")
      }

      "band 1 shows the correct amount of 20 when passed 20" in {
        val doc = asDocument(applyView)
        doc.getElementById("band-1").text mustBe "Band 1 : £20"
      }

      "band 2 shows the correct amount of 30 when passed 30" in {
        val doc = asDocument(applyView)
        doc.getElementById("band-2").text mustBe "Band 2 : £30"
      }
    }
  }

  application.stop()
}
