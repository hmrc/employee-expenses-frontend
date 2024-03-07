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

package views.authenticated

import play.api.Application
import play.twirl.api.Html
import views.newBehaviours.ViewBehaviours
import views.html.authenticated.NoCodeChangeView
import controllers.mergedJourney.routes.MergedJourneyController
import models.mergedJourney.ClaimNotChanged

class NoCodeChangeViewSpec extends ViewBehaviours {

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "NoCodeChange view" must {

    val view = application.injector.instanceOf[NoCodeChangeView]

    val applyView = view.apply()(fakeRequest, messages)

    val applyViewWithAuth = view.apply()(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView, "noCodeChange")

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(applyViewWithAuth)

    val link: Html = Html(s"""<a href="${frontendAppConfig.incomeTaxSummary}" class="govuk-link">${messages("noCodeChange.link")}</a>""")

    behave like pageWithBodyText(applyViewWithAuth, Html(messages("noCodeChange.guidance2", link)).toString)

    val applyViewMergeJourneyWithAuth = view.apply(isMergeJourney = true)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like pageWithButtonLink(applyViewMergeJourneyWithAuth, MergedJourneyController.mergedJourneyContinue(journey="fre", status=ClaimNotChanged).url, "continue")

  }

  application.stop()
}

