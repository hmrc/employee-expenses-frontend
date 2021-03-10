/*
 * Copyright 2021 HM Revenue & Customs
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

package views.authenticated

import forms.authenticated.ChangeWhichTaxYearsFormProvider
import models.{NormalMode, TaiTaxYear, TaxYearSelection}
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption
import views.behaviours.CheckboxViewBehaviours
import views.html.authenticated.ChangeWhichTaxYearsView

class ChangeWhichTaxYearsViewSpec extends CheckboxViewBehaviours[TaxYearSelection] {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val form = new ChangeWhichTaxYearsFormProvider()()

  val taxYearsAndAmounts: Seq[(RadioCheckboxOption, Int)] = TaxYearSelection.options.map { year => (year, 123) }

  def applyView(form: Form[Seq[TaxYearSelection]]): HtmlFormat.Appendable =
    application.injector.instanceOf[ChangeWhichTaxYearsView].apply(form, NormalMode, taxYearsAndAmounts)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[Seq[TaxYearSelection]]): HtmlFormat.Appendable =
    application.injector.instanceOf[ChangeWhichTaxYearsView]
      .apply(form, NormalMode, taxYearsAndAmounts)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  val messageKeyPrefix = "changeWhichTaxYears"

  "ChangeWhichTaxYearsView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))


    "contains correct values for list" in {
      val doc = asDocument(applyView(form))

      taxYearsAndAmounts.map{
        options =>
          doc.getElementById(s"fre-amount-${options._1.value}").text mustBe s"${messages("changeWhichTaxYears.columnHeading2", s"£${options._2}")}"
      }
    }

    "behave like a checkbox page" must {
      "contain a legend for the question" in {
        val doc = asDocument(applyView(form))
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe messages(s"$messageKeyPrefix.heading")
      }

      "contain an input for the value" in {
        val doc = asDocument(applyView(form))
        for {
          (_, i) <- taxYearsAndAmounts.zipWithIndex
        } yield {
          assertRenderedById(doc, form("value")(s"[$i]").id)
        }
      }

      "contain a label for each input" in {
        val doc = asDocument(applyView(form))
        for {
          (option, i) <- taxYearsAndAmounts.zipWithIndex
        } yield {
          val id = form("value")(s"[$i]").id
          doc.select(s"label[for=$id]").text mustBe s"${option._1.message.html.toString} ${messages("changeWhichTaxYears.columnHeading2", s"£${option._2}")}"
        }
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(applyView(form))
        for {
          (_, i) <- taxYearsAndAmounts.zipWithIndex
        } yield {
          assert(!doc.getElementById(form("value")(s"[$i]").id).hasAttr("checked"))
        }
      }

      taxYearsAndAmounts.zipWithIndex.foreach {
        case (checkboxOption, i) =>
          s"have correct value checked when value `${checkboxOption._1.value}` is given" in {
            val data: Map[String, String] =
              Map(s"value[$i]" -> checkboxOption._1.value)

            val doc = asDocument(applyView(form.bind(data)))
            val field = form("value")(s"[$i]")

            assert(doc.getElementById(field.id).hasAttr("checked"), s"${field.id} is not checked")

            taxYearsAndAmounts.zipWithIndex.foreach {
              case (option, j) =>
                if (option != checkboxOption) {
                  val field = form("value")(s"[$j]")
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
        val doc = asDocument(applyView(form.withError(FormError("value", "error.invalid"))))
        doc.title.contains("Error: ") mustBe true
      }

      "show an error summary" in {
        val doc = asDocument(applyView(form.withError(FormError("value", "error.invalid"))))
        assertRenderedById(doc, "error-summary-heading")
      }

      "show an error in the value field's label" in {
        val doc = asDocument(applyView(form.withError(FormError("value", "error.invalid"))))
        val errorSpan = doc.getElementsByClass("error-message").first
        errorSpan.text mustBe messages("error.invalid")
      }
    }
  }

  application.stop()
}
