/*
 * Copyright 2021 HM Revenue & Customs
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

import play.twirl.api.Html
import views.behaviours.ViewBehaviours
import views.html.PhoneUsView

class PhoneUsViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "PhoneUs view" must {

    val view = application.injector.instanceOf[PhoneUsView]

    val applyView = view.apply()(fakeRequest, messages)

    val applyViewWithAuth = view.apply()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView, "phoneUs")

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(applyViewWithAuth)

    val link: Html = Html(s"""<a href="${frontendAppConfig.contactHMRC}">${messages("phoneUs.paragraph.linkText")}</a>""")

    behave like pageWithBodyText(applyViewWithAuth, Html(messages("phoneUs.paragraph", link)).toString)
  }

  application.stop()
}
