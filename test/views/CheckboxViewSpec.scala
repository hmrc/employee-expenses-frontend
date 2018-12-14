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
          assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option._1, isChecked = false)
        }
      }
    }

    for ((option, index) <- Checkbox.options.zipWithIndex) {

      s"rendered with a value of '${option._1}'" must {

        s"have the '${option._1}' checkbox button selected" in {

          val doc = asDocument(applyView(form.bind(Map(s"value[$index]" -> s"${option._1}"))))
          assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option._1, isChecked = true)
        }
      }
    }

    "rendered with all values" must {

      "have all boxes checked" in {
        val doc = asDocument(applyView(form.bind(Map(
          "value[0]" -> Checkbox.Option1.toString,
          "value[1]" -> Checkbox.Option2.toString,
          "value[2]" -> Checkbox.Option3.toString)
        )))

        for ((option, index) <- Checkbox.options.zipWithIndex) {
          assertContainsCheckBox(doc = doc, id = s"value_$index", name = s"value[$index]", value = option._1, isChecked = true)
        }
      }
    }
  }
}
