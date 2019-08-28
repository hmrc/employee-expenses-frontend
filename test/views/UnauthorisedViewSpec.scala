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

import play.api.Application
import play.twirl.api.Html
import views.behaviours.ViewBehaviours
import views.html.UnauthorisedView

class UnauthorisedViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder().build()

  "Unauthorised view" must {

    val view = application.injector.instanceOf[UnauthorisedView]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "unauthorised")

    val printAndPostLink = Html(s"""<a href="${frontendAppConfig.p87ClaimByPostUrl}">${messages("unauthorised.printAndPost")}</a>""")
    val helplineLink = Html(s"""<a href="${frontendAppConfig.contactHMRC}">${messages("unauthorised.helpline")}</a>""")
    val claimOnlineLink = Html(s"""<a href="${frontendAppConfig.claimOnlineUrl}">${messages("unauthorised.claimOnline")}</a>""")

    behave like pageWithBodyText(applyView,
      "unauthorised.cannotContinue",
      "unauthorised.makeYourClaim",
      Html(messages("unauthorised.byPost", printAndPostLink)).toString,
      Html(messages("unauthorised.byPhone", helplineLink)).toString,
      Html(messages("unauthorised.claimOnline", claimOnlineLink)).toString
    )
  }

  application.stop()
}
