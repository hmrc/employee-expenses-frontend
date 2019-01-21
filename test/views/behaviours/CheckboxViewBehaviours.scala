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

package views.behaviours

import play.api.Application
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption
import views.ViewSpecBase

trait CheckboxViewBehaviours[A] extends ViewSpecBase {

  val form: Form[Set[A]]

  val application: Application = applicationBuilder(userData = Some(emptyUserData)).build()

  def options: Set[RadioCheckboxOption]

  def messageKeyPrefix: String

  def fieldKey: String

  def errorMessage: String

  lazy val error = FormError(fieldKey, errorMessage)

  def applyView(form: Form[Set[A]]): HtmlFormat.Appendable

  def checkboxPage(legend: Option[String] = None): Unit = {
    "rendered" must {
      "contain a legend for the question" in {
        val doc = asDocument(applyView(form))
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe legend.getOrElse(messages(s"$messageKeyPrefix.heading"))
      }

      "contain an input for the value" in {
        val doc = asDocument(applyView(form))
        for {
          (_, i) <- options.zipWithIndex
        } yield {
          assertRenderedById(doc, form(fieldKey)(s"[$i]").id)
        }
      }

      "contain a label for each input" in {
        val doc = asDocument(applyView(form))
        for {
          (option, i) <- options.zipWithIndex
        } yield {
          val id = form(fieldKey)(s"[$i]").id
          doc.select(s"label[for=$id]").text mustEqual messages(option.messageKey)
        }
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(applyView(form))
        for {
          (_, i) <- options.zipWithIndex
        } yield {
          assert(!doc.getElementById(form(fieldKey)(s"[$i]").id).hasAttr("checked"))
        }
      }
    }

    options.zipWithIndex.foreach {
      case (RadioCheckboxOption, i) =>
        s"has correct value checked when value `${RadioCheckboxOption.value}` is given" in {
          val data: Map[String, String] =
            Map(s"$fieldKey[$i]" -> RadioCheckboxOption.value)

          val doc = asDocument(applyView(form.bind(data)))
          val field = form(fieldKey)(s"[$i]")

          assert(doc.getElementById(field.id).hasAttr("checked"), s"${field.id} is not checked")

          options.zipWithIndex.foreach {
            case (option, j) =>
              if (option != RadioCheckboxOption) {
                val field = form(fieldKey)(s"[$j]")
                assert(!doc.getElementById(field.id).hasAttr("checked"), s"${field.id} is checked")
              }
          }
        }
    }

    "not render an error summary" in {
      val doc = asDocument(applyView(form))
      assertNotRenderedById(doc, "error-summary-heading")
    }


    "show error in the title" in {
      val doc = asDocument(applyView(form.withError(error)))
      doc.title.contains("Error: ") mustBe true
    }
  }

  "rendered with an error" must {
    "show an error summary" in {
      val doc = asDocument(applyView(form.withError(error)))
      assertRenderedById(doc, "error-summary-heading")
    }

    "show an error in the value field's label" in {
      val doc = asDocument(applyView(form.withError(error)))
      val errorSpan = doc.getElementsByClass("error-notification").first
      errorSpan.text mustBe messages(errorMessage)
    }
  }
}
