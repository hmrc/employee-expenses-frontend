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

import controllers.authenticated.routes
import forms.authenticated.AlreadyClaimingFREFormProvider
import models.{FlatRateExpense, FlatRateExpenseAmounts, NormalMode, TaiTaxYear, TaxYearSelection}
import org.jsoup.select.Elements
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.authenticated.AlreadyClaimingFREView

class AlreadyClaimingFREViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "alreadyClaimingFRE"

  val form = new AlreadyClaimingFREFormProvider()()

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  private val fakeClaimAmount = 100
  private val fakeFreAmounts = Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))

  "AlreadyClaimingFRE view" must {

    val view = application.injector.instanceOf[AlreadyClaimingFREView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeClaimAmount, fakeFreAmounts)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeClaimAmount, fakeFreAmounts)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(
      form = form,
      createView = applyView,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.AlreadyClaimingFREController.onSubmit(NormalMode).url,
      legendLabel = Some(messageKeyPrefix + ".radioLabel")
    )

    "contains correct headings for table" in {
      val doc = asDocument(applyView(form))

      doc.getElementById("tax-year-heading").text mustBe messages("alreadyClaimingFRE.tableTaxYearHeading")
      doc.getElementById("amount-heading").text mustBe messages("alreadyClaimingFRE.tableAmountHeading")
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
