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

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait YesNoViewBehaviours extends QuestionViewBehaviours[Boolean] {

  def yesNoPage(
      form: Form[Boolean],
      createView: Form[Boolean] => HtmlFormat.Appendable,
      messageKeyPrefix: String,
      expectedFormAction: String,
      legendLabel: Option[String] = None
  ): Unit =

    "behave like a page with a Yes/No question" when {

      "rendered" must {

        "contain a legend for the question" in {

          val doc     = asDocument(createView(form))
          val legends = doc.getElementsByTag("legend")
          legends.size mustBe 1

          if (legendLabel.isDefined) {
            legends.first.text mustBe messages(legendLabel.get)
          } else {
            legends.first.text mustBe messages(s"$messageKeyPrefix.heading")
          }
        }

        "contain an input for the value" in {

          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
          assertRenderedById(doc, "value-2")
        }

        "have no values checked when rendered with no form" in {

          val doc = asDocument(createView(form))
          assert(!doc.getElementById("value").hasAttr("checked"))
          assert(!doc.getElementById("value-2").hasAttr("checked"))
        }

        "not render an error summary" in {

          val doc = asDocument(createView(form))
          assertNotRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
        }
      }

      "rendered with a value of true" must
        behave.like(answeredYesNoPage(createView, true))

      "rendered with a value of false" must
        behave.like(answeredYesNoPage(createView, false))

      "rendered with an error" must {

        "show an error summary" in {

          val doc = asDocument(createView(form.withError(error)))

          assertRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
        }

        "show an error in the value field's label" in {

          val doc       = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe "Error: " + messages(errorMessage)
        }

        "show an error prefix in the browser title" in {

          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(
            doc,
            "title",
            s"""${messages("error.browser.title.prefix")} ${messages(
                s"$messageKeyPrefix.title"
              )} - ${frontendAppConfig.serviceTitle}"""
          )
        }
      }
    }

  def answeredYesNoPage(createView: Form[Boolean] => HtmlFormat.Appendable, answer: Boolean): Unit = {

    "have only the correct value checked" in {

      val doc = asDocument(createView(form.fill(answer)))
      assert(doc.getElementById("value").hasAttr("checked") == answer)
      assert(doc.getElementById("value-2").hasAttr("checked") != answer)
    }

    "not render an error summary" in {

      val doc = asDocument(createView(form.fill(answer)))
      assertNotRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
    }
  }

}
