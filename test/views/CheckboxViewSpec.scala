/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.CheckboxView

import scala.collection.immutable.Map
import scala.collection.mutable

class CheckboxViewSpec extends ViewBehaviours {

	val messageKeyPrefix = "checkbox"

	val form = new CheckboxFormProvider()()

	val application = applicationBuilder(userData = Some(emptyUserData)).build()

	val view = application.injector.instanceOf[CheckboxView]

	def applyView(form: Form[Set[Checkbox]]): HtmlFormat.Appendable =
		view.apply(form, NormalMode)(fakeRequest, messages)

	"CheckboxView" must {

		behave like normalPage(applyView(form), messageKeyPrefix)

		behave like pageWithBackLink(applyView(form))
	}

	"CheckboxView" when {

		"rendered" must {

			"contain checkbox buttons for the value" in {

				val doc = asDocument(applyView(form))

				for ((option, index) <- Checkbox.options.zipWithIndex) {
					assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option.value, isChecked = false)
				}
			}
		}

		for ((option, index) <- Checkbox.options.zipWithIndex) {

			s"rendered with a value of '${option.value}'" must {

				s"have the '${option.value}' checkbox button selected" in {

					val doc = asDocument(applyView(form.bind(Map(s"value[$index]" -> s"${option.value}"))))
					assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option.value, isChecked = true)
				}
			}
		}

		"rendered with all values" must {

			"have all boxes checked" in {

				val data = scala.collection.mutable.Map[String, String]()

				for ((option, index) <- Checkbox.values.zipWithIndex) {
					data += s"value[$index]" -> option.toString
				}

				val doc = asDocument(applyView(form.bind(data.toMap)))

				for ((option, index) <- Checkbox.options.zipWithIndex) {
					assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option.value, isChecked = true)
				}
			}
		}
	}
}
