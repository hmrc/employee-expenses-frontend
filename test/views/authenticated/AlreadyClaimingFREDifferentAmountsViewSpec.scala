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

import forms.authenticated.AlreadyClaimingFREDifferentAmountsFormProvider
import models.{AlreadyClaimingFREDifferentAmounts, NormalMode, TaiTaxYear, TaxYearSelection}
import pages.{ClaimAmountAndAnyDeductions, FREAmounts}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.authenticated.AlreadyClaimingFREDifferentAmountsView

class AlreadyClaimingFREDifferentAmountsViewSpec extends OptionsViewBehaviours[AlreadyClaimingFREDifferentAmounts] {

  val messageKeyPrefix = "alreadyClaimingFREDifferentAmounts"

  val form = new AlreadyClaimingFREDifferentAmountsFormProvider()()

  val application = applicationBuilder(userAnswers = Some(minimumUserAnswers)).build()

  val view = application.injector.instanceOf[AlreadyClaimingFREDifferentAmountsView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(
      form,
      NormalMode,
      minimumUserAnswers.get(ClaimAmountAndAnyDeductions).get,
      minimumUserAnswers.get(FREAmounts).get
    )(fakeRequest, messages)

  def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
    view.apply(
      form,
      NormalMode,
      minimumUserAnswers.get(ClaimAmountAndAnyDeductions).get,
      minimumUserAnswers.get(FREAmounts).get
    )(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  "AlreadyClaimingFREDifferentAmountsView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like optionsPage(form, applyView, AlreadyClaimingFREDifferentAmounts.options)

    "display correct body text" in {
      val doc = asDocument(applyView(form))
      assertContainsText(doc, messages("alreadyClaimingFREDifferentAmounts.bodyText1", minimumUserAnswers.get(ClaimAmountAndAnyDeductions).get))
      assertContainsText(doc, messages("alreadyClaimingFREDifferentAmounts.bodyText2"))
    }

    "contains correct headings for table" in {
      val doc = asDocument(applyView(form))

      doc.getElementById("tax-year-heading").text mustBe messages("alreadyClaimingFREDifferentAmounts.tableTaxYearHeading")
      doc.getElementById("amount-heading").text mustBe messages("alreadyClaimingFREDifferentAmounts.tableAmountHeading")
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
