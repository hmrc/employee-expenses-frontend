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

package views.behaviours

import forms.CheckboxFormProvider
import models.{Checkbox, NormalMode}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.ViewSpecBase
import views.html.CheckboxView

import scala.collection.immutable.Map

trait CheckboxViewBehaviours[A] extends ViewSpecBase {

  val form = new CheckboxFormProvider()()

  val application: Application = applicationBuilder(userData = Some(emptyUserData)).build()

  val view: CheckboxView = application.injector.instanceOf[CheckboxView]

  def applyView(form: Form[Set[Checkbox]]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  def aCheckboxViewWithSingleValueNotChecked() = {
    "contain checkbox buttons for the value" in {
      val doc = asDocument(applyView(form))

      for ((option, index) <- Checkbox.options.zipWithIndex) {
        assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option.value, isChecked = false)
      }
    }
  }

  def aCheckboxViewWithSingleValueChecked(): Unit = {
    for ((option, index) <- Checkbox.options.zipWithIndex) {

      s"have the '${option.value}' checkbox button selected" in {

        val doc = asDocument(applyView(form.bind(Map(s"value[$index]" -> s"${option.value}"))))
        assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option.value, isChecked = true)
      }
    }
  }

  def aCheckboxViewWithAllValuesChecked: Set[Unit] = {
    for {
      (option, index) <- Checkbox.values.zipWithIndex
    } yield "have all boxes checked" in {
      val data = Map(s"value[$index]" -> option.toString)

      val doc = asDocument(applyView(form.bind(data)))

      for ((option, index) <- Checkbox.options.zipWithIndex) {
        assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option.value, isChecked = true)
      }
    }
  }
}
