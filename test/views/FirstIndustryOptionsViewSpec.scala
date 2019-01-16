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

import forms.FirstIndustryOptionsFormProvider
import models.{FirstIndustryOptions, NormalMode}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.FirstIndustryOptionsView

class FirstIndustryOptionsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "firstIndustryOptions"
  val form = new FirstIndustryOptionsFormProvider()()
  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
  val view: FirstIndustryOptionsView = application.injector.instanceOf[FirstIndustryOptionsView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  "FirstIndustryOptionsView" must {

    behave like normalPage(applyView(form), messageKeyPrefix, None)
    behave like pageWithBackLink(applyView(form))
  }

  "FirstIndustryOptionsView" when {

    "rendered" must {

      "contain radio buttons for the values" in {

        val doc = asDocument(applyView(form))

        for (option <- FirstIndustryOptions.options) {

          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }
    for (option <- FirstIndustryOptions.options) {

      s"rendered with a value of '${option.value}' " must {

        s"have the '${option.value}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- FirstIndustryOptions.options.filterNot(o => o == option)) {

            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)

          }
        }
      }

    }
  }
  application.stop()
}
