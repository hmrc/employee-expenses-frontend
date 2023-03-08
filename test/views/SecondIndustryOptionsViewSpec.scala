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

import forms.SecondIndustryOptionsFormProvider
import models.{NormalMode, SecondIndustryOptions}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.newBehaviours.OptionsViewBehaviours
import views.html.SecondIndustryOptionsView

class SecondIndustryOptionsViewSpec extends OptionsViewBehaviours[SecondIndustryOptions] {

  val messageKeyPrefix = "secondIndustryOptions"

  val form = new SecondIndustryOptionsFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val view = application.injector.instanceOf[SecondIndustryOptionsView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  "SecondIndustryOptionsView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like optionsPage(form, applyView, SecondIndustryOptions.options)

    behave like pageWithBodyText(applyView(form), "secondIndustryOptions.heading")

    "must have the correct text of 'or' between last 2 radioButtons" in {

      val doc = asDocument(applyView(form))

      doc.getElementsByClass("govuk-radios__divider").text() mustBe messages("site.or")
    }
  }

  application.stop()
}
