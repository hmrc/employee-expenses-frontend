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

import forms.authenticated.TaxYearSelectionFormProvider
import models.{NormalMode, TaxYearSelection}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption
import views.behaviours.CheckboxViewBehaviours
import views.html.authenticated.TaxYearSelectionView

class TaxYearSelectionViewSpec extends CheckboxViewBehaviours[TaxYearSelection] {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val form = new TaxYearSelectionFormProvider()()

  def applyView(form: Form[Seq[TaxYearSelection]]): HtmlFormat.Appendable =
    application.injector.instanceOf[TaxYearSelectionView].apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[Seq[TaxYearSelection]]): HtmlFormat.Appendable =
    application.injector.instanceOf[TaxYearSelectionView].apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  val messageKeyPrefix = "taxYearSelection"

  val options: Seq[RadioCheckboxOption] = TaxYearSelection.options

  "TaxYearSelectionView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like checkboxPage(form, applyView, messageKeyPrefix, options)

    behave like pageWithBodyText(applyView(form), messageKeyPrefix + ".paragraph")
  }

  application.stop()
}
