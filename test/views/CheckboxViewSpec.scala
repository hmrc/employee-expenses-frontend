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

import forms.CheckboxFormProvider
import models.{Checkbox, NormalMode}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.CheckboxOption
import views.behaviours.{CheckboxViewBehaviours, ViewBehaviours}
import views.html.CheckboxView

class CheckboxViewSpec extends ViewBehaviours with CheckboxViewBehaviours[Checkbox] {

	val messageKeyPrefix = "checkbox"

  override val fieldKey = "value"

	val errorMessage = "error.invalid"

  val options: Set[CheckboxOption] = Checkbox.options

	override val form = new CheckboxFormProvider()()

	override val application: Application = applicationBuilder(userData = Some(emptyUserData)).build()

	def applyView(form: Form[Set[Checkbox]]): HtmlFormat.Appendable =
		application.injector.instanceOf[CheckboxView].apply(form, NormalMode)(fakeRequest, messages)

	"CheckboxView" must {
		behave like normalPage(applyView(form), messageKeyPrefix)

		behave like pageWithBackLink(applyView(form))

		behave like checkboxPage()
	}
}
