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

package views.newBehaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption

trait OptionsViewBehaviours[A] extends ViewBehaviours {

  def optionsPage(form: Form[A],
                  createView: Form[A] => HtmlFormat.Appendable,
                  options: Seq[RadioCheckboxOption],
                  radioOr: Boolean = false): Unit = {

    "behave like an options page" must {

      "contain radio buttons for the values" in {

        val doc = asDocument(createView(form))

        for (option <- options) {

          val idVal = if(radioOr && option == options.last){
            s"value-${options.indexOf(option) + 2}"
          } else if(option != options.head){
            s"value-${options.indexOf(option) + 1}"
          } else {s"value"}

          assertContainsRadioButton(doc, idVal, "value", option.value, false)
        }
      }
    }
    for (option <- options) {

      val idVal = if(radioOr && option == options.last){
        s"value-${options.indexOf(option) + 2}"
      } else if(option != options.head){
        s"value-${options.indexOf(option) + 1}"
      } else {s"value"}

      s"rendered with a value of '${option.value}' " must {

        s"have the '${option.value}' radio button selected" in {

          val doc = asDocument(createView(form.bind(Map("value" -> s"${option.value}"))))

          assertContainsRadioButton(doc, idVal, "value", option.value, true)

          for (unselectedOption <- options.filterNot(o => o == option)) {

            val unselectId = if(radioOr && unselectedOption == options.last){
              s"value-${options.indexOf(unselectedOption) + 2}"
            } else if(unselectedOption != options.head) {
              s"value-${options.indexOf(unselectedOption) + 1}"
            } else{s"value"}

            assertContainsRadioButton(doc, unselectId, "value", unselectedOption.value, false)

          }
        }
      }
    }
  }
}
