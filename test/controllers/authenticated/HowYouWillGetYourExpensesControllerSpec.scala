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

package controllers.authenticated

import base.SpecBase
import generators.Generators
import models.FlatRateExpenseOptions.FRENoYears
import models.{EmployerContribution, FlatRateExpense, FlatRateExpenseAmounts, TaiTaxYear, TaxYearSelection}
import models.TaxYearSelection._
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions, EmployerContributionPage, FREAmounts, FREResponse}
import pages.authenticated._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{HowYouWillGetYourExpensesCurrentAndPreviousYearView, HowYouWillGetYourExpensesCurrentView, HowYouWillGetYourExpensesPreviousView}

class HowYouWillGetYourExpensesControllerSpec extends SpecBase with ScalaCheckPropertyChecks with Matchers with Generators {

  "HowYouWillGetYourExpensesController" must {
    "return OK and the correct view for a GET when" when {
      "user has selected current year only for changes" in {
        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(Seq(CurrentYear)))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
        view(controllers.authenticated.routes.SubmissionController.onSubmit.url, hasClaimIncreased = false)(request, messages).toString

        application.stop()
      }

      "user has selected current year only for changes with an increase" in {
        val ua = yearsUserAnswers(Seq(CurrentYear))
          .set(ClaimAmountAndAnyDeductions, 120).success.value
        val application = applicationBuilder(userAnswers = Some(ua)).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, hasClaimIncreased = true)(request, messages).toString

        application.stop()
      }

      "user has selected current year only for changes with a decrease" in {
        val ua = yearsUserAnswers(Seq(CurrentYear))
          .set(ClaimAmountAndAnyDeductions, 40).success.value
        val application = applicationBuilder(userAnswers = Some(ua)).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, hasClaimIncreased = false)(request, messages).toString

        application.stop()
      }

       "user has selected CY-1 only for changes" in {
        val taxYearSelection = Seq(CurrentYearMinus1)
        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, currentYearMinus1Selected = true)(request, messages).toString

        application.stop()
      }

      "user has selected CY and CY-1 for changes" in {
        val taxYearSelection = Seq(CurrentYear, CurrentYearMinus1)
        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, currentYearMinus1Selected = true, hasClaimIncreased = false)(request, messages).toString

        application.stop()
      }

      "user has selected CY and CY-1 for changes with an increase" in {
        val ua = yearsUserAnswers(Seq(CurrentYear, CurrentYearMinus1))
          .set(ClaimAmountAndAnyDeductions, 120).success.value
        val application = applicationBuilder(userAnswers = Some(ua)).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, currentYearMinus1Selected = true, hasClaimIncreased = true)(request, messages).toString

        application.stop()
      }

      "user has selected CY and CY-1 for changes with a decrease" in {
        val ua = yearsUserAnswers(Seq(CurrentYear, CurrentYearMinus1))
          .set(ClaimAmountAndAnyDeductions, 40).success.value
        val application = applicationBuilder(userAnswers = Some(ua)).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, currentYearMinus1Selected = true, hasClaimIncreased = false)(request, messages).toString

        application.stop()
      }

      "user has selected CY, CY-1 and previous year for changes" in {
        val taxYearSelection = Seq(CurrentYear, CurrentYearMinus1, CurrentYearMinus1)
        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, true, hasClaimIncreased = false)(request, messages).toString

        application.stop()
      }

      "user has selected CY and CY-2 for changes" in {
        val taxYearSelection = Seq(CurrentYear, CurrentYearMinus2)
        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, false, hasClaimIncreased = false)(request, messages).toString

        application.stop()
      }

      "user has selected CY-1 and CY-2 for changes" in {
        val taxYearSelection = Seq(CurrentYearMinus1, CurrentYearMinus2)
        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, currentYearMinus1Selected = true)(request, messages).toString

        application.stop()
      }

      "user has selected CY-2 and CY-3 for changes" in {
        val taxYearSelection = Seq(CurrentYearMinus2, CurrentYearMinus3)
        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, currentYearMinus1Selected = false)(request, messages).toString

        application.stop()
      }
    }

    "return OK and the correct view for a GET when change years are selected" when {
      def changeYearsUserAnswers(years: Seq[TaxYearSelection]) = emptyUserAnswers
        .set(EmployerContributionPage,  EmployerContribution.NoEmployerContribution).success.value
        .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1, CurrentYearMinus2, CurrentYearMinus3, CurrentYearMinus4)).success.value
        .set(ChangeWhichTaxYearsPage, years).success.value
        .set(YourAddressPage, true).success.value
        .set(YourEmployerPage, true).success.value
        .set(ClaimAmount, 100).success.value
        .set(ClaimAmountAndAnyDeductions, 80).success.value
        .set(FREResponse, FRENoYears).success.value
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))).success.value

      "user has selected current year only for changes" in {
        val taxYearSelection = Seq(CurrentYear)
        val application = applicationBuilder(userAnswers = Some(changeYearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, hasClaimIncreased = false)(request, messages).toString

        application.stop()
      }

      "user has selected CY-1 only for changes" in {
        val taxYearSelection = Seq(CurrentYearMinus1)
        val application = applicationBuilder(userAnswers = Some(changeYearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, true)(request, messages).toString

        application.stop()
      }

      "user has selected CY and CY-1 for changes" in {
        val taxYearSelection = Seq(CurrentYear, CurrentYearMinus1)
        val application = applicationBuilder(userAnswers = Some(changeYearsUserAnswers(taxYearSelection))).build()
        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)
        val result = route(application, request).value
        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(controllers.authenticated.routes.SubmissionController.onSubmit.url, true, false)(request, messages).toString

        application.stop()
      }
    }
  }
}
