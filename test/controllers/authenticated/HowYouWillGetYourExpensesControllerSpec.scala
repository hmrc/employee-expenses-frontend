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

package controllers.authenticated

import base.SpecBase
import generators.Generators
import models.TaxYearSelection
import org.scalatest.MustMatchers
import org.scalatest.prop.PropertyChecks
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{HowYouWillGetYourExpensesCurrentAndPreviousYearView, HowYouWillGetYourExpensesCurrentView, HowYouWillGetYourExpensesPreviousView}

class HowYouWillGetYourExpensesControllerSpec extends SpecBase with PropertyChecks with MustMatchers with Generators {

  "HowYouWillGetYourExpensesController" must {

    "return OK and the correct view for a GET when" when {

      "user has selected current year only for changes" in {

        val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(Seq(TaxYearSelection.CurrentYear)))).build()

        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(controllers.confirmation.routes.ConfirmationCurrentYearOnlyController.onPageLoad().url)(request, messages).toString

        application.stop()
      }

      "user has selected CY-1 only for changes" in {

        val application = applicationBuilder(userAnswers =
          Some(yearsUserAnswers(Seq(TaxYearSelection.CurrentYearMinus1)))).build()

        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(controllers.confirmation.routes.ConfirmationPreviousYearsOnlyController.onPageLoad().url,
          true)(request, messages).toString

        application.stop()

      }

      "user has selected CY and CY-1 for changes" in {

        val application = applicationBuilder(userAnswers =
          Some(yearsUserAnswers(Seq
          (TaxYearSelection.CurrentYear,
           TaxYearSelection.CurrentYearMinus1)
           ))).build()

        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(controllers.confirmation.routes.ConfirmationCurrentAndPreviousYearsController.onPageLoad().url,
            true)(request, messages).toString

        application.stop()

      }

      "user has selected CY, CY-1 and previous year for changes" in {

        val application = applicationBuilder(userAnswers =
          Some(yearsUserAnswers(Seq
          (TaxYearSelection.CurrentYear,
           TaxYearSelection.CurrentYearMinus1,
           TaxYearSelection.CurrentYearMinus1)
           ))).build()

        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(controllers.confirmation.routes.ConfirmationCurrentAndPreviousYearsController.onPageLoad().url,
            true)(request, messages).toString

        application.stop()
      }

      "user has selected CY and CY-2 for changes" in {

        val application = applicationBuilder(userAnswers =
          Some(yearsUserAnswers(Seq
          (TaxYearSelection.CurrentYear,
           TaxYearSelection.CurrentYearMinus2)
           ))).build()

        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(controllers.confirmation.routes.ConfirmationCurrentAndPreviousYearsController.onPageLoad().url,
            false)(request, messages).toString

        application.stop()
      }

      "user has selected CY-1 and CY-2 for changes" in {

        val application = applicationBuilder(userAnswers =
          Some(yearsUserAnswers(Seq
          (TaxYearSelection.CurrentYearMinus1,
           TaxYearSelection.CurrentYearMinus2)
           ))).build()

        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(controllers.confirmation.routes.ConfirmationPreviousYearsOnlyController.onPageLoad().url,
            currentYearMinus1Selected = true)(request, messages).toString

        application.stop()
      }

      "user has selected CY-2 and CY-3 for changes" in {

        val taxYearSelection: Seq[TaxYearSelection] = Seq(TaxYearSelection.CurrentYearMinus2, TaxYearSelection.CurrentYearMinus3)

        val application = applicationBuilder(userAnswers =
          Some(yearsUserAnswers(Seq
          (TaxYearSelection.CurrentYearMinus2,
           TaxYearSelection.CurrentYearMinus3)
           ))).build()

        val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(controllers.confirmation.routes.ConfirmationPreviousYearsOnlyController.onPageLoad().url,
          currentYearMinus1Selected = false)(request, messages).toString

        application.stop()
      }
    }
  }
}