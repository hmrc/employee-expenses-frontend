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

import play.twirl.api.Html
import views.behaviours.ViewBehaviours
import views.html.ClaimByAlternativeView

class ClaimByAlternativeViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ClaimByAlternative view" must {

    val view = application.injector.instanceOf[ClaimByAlternativeView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "claimByAlternative")

    behave like pageWithBackLink(applyView)

    val link1: Html = Html(s"""<a href="${frontendAppConfig.p87ClaimOnlineUrl}">${messages("claimByAlternative.onlineFormLink")}</a>""")

    val link2: Html = Html(s"""<a href="${frontendAppConfig.p87ClaimByPostUrl}">${messages("claimByAlternative.claimByPostLink")}</a>""")

    behave like pageWithBodyText(
      applyView,
      Html(messages("claimByAlternative.onlineFormLink", link1)).toString,
      Html(messages("claimByAlternative.claimByPostLink", link2)).toString
    )
  }

  application.stop()
}
