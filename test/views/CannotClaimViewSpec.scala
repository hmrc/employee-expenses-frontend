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

import views.behaviours.ViewBehaviours
import views.html.CannotClaimView

class CannotClaimViewSpec extends ViewBehaviours {

  "CannotClaim view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

    val view = application.injector.instanceOf[CannotClaimView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "cannotClaim")

    behave like pageWithBackLink(applyView)

    "display page content" in {
      val doc = asDocument(applyView)
      assertContainsMessages(doc, "cannotClaim.para1", "cannotClaim.link")
      doc.getElementById("link").attr("href") mustBe "https://www.gov.uk/tax-relief-for-employees"
    }

  }
}
