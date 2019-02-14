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

package views.authenticated

import views.behaviours.ViewBehaviours
import views.html.authenticated.PhoneUsView

class PhoneUsViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "PhoneUs view" must {

    val view = application.injector.instanceOf[PhoneUsView]

    val applyViewWithAuth = view.apply("/foo")(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(applyViewWithAuth)

    "display page content" in {
      val doc = asDocument(applyViewWithAuth)
      assertContainsMessages(doc, "phoneUs.para1.part1","phoneUs.para1.linkText","phoneUs.para1.part2")
    }
  }

  application.stop()
}
