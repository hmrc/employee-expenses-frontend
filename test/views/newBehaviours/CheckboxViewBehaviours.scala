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

package views.newBehaviours

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption

trait CheckboxViewBehaviours[A] extends ViewBehaviours {

  def checkboxPage(
      form: Form[Seq[A]],
      createView: Form[Seq[A]] => HtmlFormat.Appendable,
      messageKeyPrefix: String,
      options: Seq[RadioCheckboxOption],
      fieldKey: String = "value",
      legend: Option[String] = None
  ): Unit =

    "behave like a checkbox page" must {
      "contain a legend for the question" in {
        val doc     = asDocument(createView(form))
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe legend.getOrElse(messages(s"$messageKeyPrefix.heading"))
      }

      "contain an input for the value" in {
        val doc = asDocument(createView(form))
        for {
          (option, i) <- options.zipWithIndex
        } yield {
          val idVal = if (option != options.head) { s"value-${i + 1}" }
          else "value"

          assertRenderedById(doc, idVal)
        }
      }

      "contain a label for each input" in {
        val doc = asDocument(createView(form))
        for {
          (option, i) <- options.zipWithIndex
        } yield {
          val id = form(fieldKey)(s"[$i]").id
          doc.select(s"label[for=$id]").text contains option.message.html.toString
        }
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(createView(form))
        for {
          (option, i) <- options.zipWithIndex
        } yield {
          val idVal = if (option != options.head) { s"value-${i + 1}" }
          else "value"

          assert(!doc.getElementById(idVal).hasAttr("checked"))
        }
      }

      options.zipWithIndex.foreach { case (checkboxOption, i) =>
        s"have correct value checked when value `${checkboxOption.value}` is given" in {
          val data: Map[String, String] =
            Map(s"$fieldKey[$i]" -> checkboxOption.value)

          val doc = asDocument(createView(form.bind(data)))
          val field = if (checkboxOption != options.head) { s"value-${i + 1}" }
          else "value"

          assert(doc.getElementById(field).hasAttr("checked"), s"$field is not checked")

          options.zipWithIndex.foreach { case (option, j) =>
            if (option != checkboxOption) {
              val field = if (option != options.head) { s"value-${j + 1}" }
              else "value"
              assert(!doc.getElementById(field).hasAttr("checked"), s"$field is checked")
            }
          }
        }
      }

      "not render an error summary" in {
        val doc = asDocument(createView(form))
        assertNotRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
      }

      "show error in the title" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        doc.title.contains("Error: ") mustBe true
      }

      "show an error summary" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        assertRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
      }

      "show an error in the value field's label" in {
        val doc       = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        val errorSpan = doc.getElementsByClass("govuk-error-message").first
        errorSpan.text mustBe "Error: " + messages("error.invalid")
      }
    }

}
