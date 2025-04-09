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
import views.html.IdentityVerificationFailedView
import views.newBehaviours.ViewBehaviours

class IdentityVerificationFailedViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder().build()

  "Unauthorised view" must {

    val view = application.injector.instanceOf[IdentityVerificationFailedView]

    val applyView = view.apply()(fakeRequest, messages)

    behave.like(normalPage(applyView, "ivFailed"))

    val printAndPostLink: Html =
      Html(s"""<a class="govuk-link" href="${frontendAppConfig.p87ClaimByPostUrl}">${messages(
          "ivFailed.printAndPost"
        )}</a>""")
    val helplineLink: Html =
      Html(s"""<a class="govuk-link" href="${frontendAppConfig.contactHMRC}">${messages("ivFailed.helpline")}</a>""")
    val claimOnlineLink: Html = Html(s"""<a class="govuk-link" href="${frontendAppConfig.claimOnlineUrl}">${messages(
        "ivFailed.confirmIdentity"
      )}</a>""")

    behave.like(
      pageWithBodyText(
        applyView,
        "ivFailed.cannotContinue",
        "ivFailed.makeYourClaim",
        Html(messages("ivFailed.byPost", printAndPostLink)).toString,
        Html(messages("ivFailed.byPhone", helplineLink)).toString,
        Html(messages("ivFailed.claimOnline", claimOnlineLink)).toString
      )
    )
  }

  application.stop()
}
