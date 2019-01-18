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

package views.transport

import forms.transport.TypeOfTransportFormProvider
import models.{NormalMode, TypeOfTransport}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.transport.TypeOfTransportView

class TypeOfTransportViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "typeOfTransport"

  val form = new TypeOfTransportFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val view = application.injector.instanceOf[TypeOfTransportView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  "TypeOfTransportView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))
  }

  "TypeOfTransportView" when {

    "rendered" must {

      "contain radio buttons for the value" in {

        val doc = asDocument(applyView(form))

        for (option <- TypeOfTransport.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- TypeOfTransport.options) {

      s"rendered with a value of '${option.value}'" must {

        s"have the '${option.value}' radio button selected" in {

          val doc = asDocument(applyView(form.bind(Map("value" -> s"${option.value}"))))

          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- TypeOfTransport.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }
  }

  application.stop()
}
