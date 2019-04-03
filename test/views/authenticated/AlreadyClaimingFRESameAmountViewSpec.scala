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

package views.authenticated

import forms.authenticated.AlreadyClaimingFRESameAmountFormProvider
import models.{AlreadyClaimingFRESameAmount, FlatRateExpense, FlatRateExpenseAmounts, NormalMode, TaiTaxYear, TaxYearSelection}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.authenticated.AlreadyClaimingFRESameAmountView

class AlreadyClaimingFRESameAmountViewSpec extends OptionsViewBehaviours[AlreadyClaimingFRESameAmount] {

  val messageKeyPrefix = "alreadyClaimingFRESameAmount"

  val form = new AlreadyClaimingFRESameAmountFormProvider()()

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  private val fakeClaimAmount = 100
  private val fakeFreAmounts = Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()), FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear().prev))
  private val fakeFreAmount = Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))

  "AlreadyClaimingFRESameAmount view" must {

    val view = application.injector.instanceOf[AlreadyClaimingFRESameAmountView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeClaimAmount, fakeFreAmounts)(fakeRequest, messages)

    def applyViewSingleYear(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeClaimAmount, fakeFreAmount)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeClaimAmount, fakeFreAmounts)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like optionsPage(form, applyView, AlreadyClaimingFRESameAmount.options)

    "contain table and correct headings for multiple years" in {
      val doc = asDocument(applyView(form))

      doc.getElementById("tax-year-heading").text mustBe messages("alreadyClaimingFRESameAmount.tableTaxYearHeading")
      doc.getElementById("amount-heading").text mustBe messages("alreadyClaimingFRESameAmount.tableAmountHeading")
    }

    "not contain table for  for single year" in {
      val doc = asDocument(applyViewSingleYear(form))

      doc.getElementById("tax-year-heading") mustBe null
      doc.getElementById("amount-heading") mustBe null
    }

    "contains correct column values for table" in {
      val doc = asDocument(applyView(form))

      doc.getElementById(s"tax-year-${TaiTaxYear().year}").text mustBe messages(
        s"taxYearSelection.${TaxYearSelection.getTaxYearPeriod(TaiTaxYear().year)}",
        TaiTaxYear().year.toString,
        (TaiTaxYear().year + 1).toString
      )

      doc.getElementById(s"fre-amount-${TaiTaxYear().year}").text mustBe "£100"
    }

  }

  application.stop()
}
