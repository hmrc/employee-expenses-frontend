/*
 * Copyright 2022 HM Revenue & Customs
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

import forms.authenticated.RemoveFRECodeFormProvider
import models.{NormalMode, TaxYearSelection}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.newBehaviours.OptionsViewBehaviours
import views.html.authenticated.RemoveFRECodeView

class RemoveFRECodeViewSpec extends OptionsViewBehaviours[TaxYearSelection] {

  val messageKeyPrefix = "removeFRECode"

  val form = new RemoveFRECodeFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val view = application.injector.instanceOf[RemoveFRECodeView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  "RemoveFRECodeView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like optionsPage(form, applyView, TaxYearSelection.options)
  }

  application.stop()
}
