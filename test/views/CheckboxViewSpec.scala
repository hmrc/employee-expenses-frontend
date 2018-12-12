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
import models.{NormalMode, Checkbox}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.CheckboxView

class CheckboxViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "checkbox"

  val form = new CheckboxFormProvider()()

  val application = applicationBuilder(userData = Some(emptyUserData)).build()

  val view = application.injector.instanceOf[CheckboxView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  "CheckboxView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))
  }

  "CheckboxView" when {

    "rendered" must {

      "contain checkbox buttons for the value" in {

        val doc = asDocument(applyView(form))

        for (option <- Checkbox.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- Checkbox.options) {

      s"rendered with a value of '${option.value}'" must {

        s"have the '${option.value}' checkbox button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- Checkbox.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }
}
