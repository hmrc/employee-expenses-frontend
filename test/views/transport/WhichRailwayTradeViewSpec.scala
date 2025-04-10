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

package views.transport

import forms.transport.WhichRailwayTradeFormProvider
import models.{NormalMode, WhichRailwayTrade}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.newBehaviours.OptionsViewBehaviours
import views.html.transport.WhichRailwayTradeView

class WhichRailwayTradeViewSpec extends OptionsViewBehaviours[WhichRailwayTrade] {

  val messageKeyPrefix = "whichRailwayTrade"

  val form = new WhichRailwayTradeFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val view = application.injector.instanceOf[WhichRailwayTradeView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  "WhichRailwayTradeView" must {

    behave.like(normalPage(applyView(form), messageKeyPrefix))

    behave.like(pageWithAccountMenu(applyViewWithAuth(form)))

    behave.like(pageWithBackLink(applyView(form)))

    behave.like(optionsPage(form, applyView, WhichRailwayTrade.options))

    behave.like(pageWithBodyText(applyView(form), "whichRailwayTrade.heading"))

  }

  application.stop()
}
