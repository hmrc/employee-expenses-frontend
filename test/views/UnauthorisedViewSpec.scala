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

import play.api.Application
import play.twirl.api.Html
import views.newBehaviours.ViewBehaviours
import views.html.UnauthorisedView

class UnauthorisedViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder().build()

  val sessionId = "id"

  "Unauthorised view" must {

    val view = application.injector.instanceOf[UnauthorisedView]

    val applyView = view.apply(sessionId)(fakeRequest, messages)

    behave like normalPage(applyView, "unauthorised")

    val link: Html = Html(s"""<a class="govuk-link" href="${frontendAppConfig.loginUrl}?continue=${frontendAppConfig.loginContinueUrl}$sessionId">${messages("unauthorised.signIn.text")}</a>""")

    behave like pageWithBodyText(applyView,
      "unauthorised.paragraph.one",
      "unauthorised.paragraph.two",
      "unauthorised.signIn.text"
    )
  }

  application.stop()
}
