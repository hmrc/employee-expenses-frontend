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

package controllers

import base.SpecBase
import models.TaxYearSelection
import models.TaxYearSelection._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{HowYouWillGetYourExpensesCurrentAndPreviousYearView, HowYouWillGetYourExpensesCurrentView, HowYouWillGetYourExpensesPreviousView}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks

class HowYouWillGetYourExpensesControllerSpec extends SpecBase with PropertyChecks {

  "HowYouWillGetYourExpensesCurrent Controller" must {

    "return OK and the current year view when user has only selected current year for changes" in {

      val application = applicationBuilder(userAnswers = Some(currentYearFullUserAnswers)).build()

      val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view("")(request, messages).toString

      application.stop()
    }

    "return OK and the previous year view when user has only selected previous year for changes" in {
      val previousYearGen: Gen[TaxYearSelection] =
        Gen.oneOf(CurrentYearMinus1, CurrentYearMinus2, CurrentYearMinus3, CurrentYearMinus4)

      forAll(previousYearGen) {
        previousYear =>

          val application = applicationBuilder(userAnswers = Some(previousYearUserAnswers(previousYear))).build()

          val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[HowYouWillGetYourExpensesPreviousView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view("")(request, messages).toString

          application.stop()

      }
    }


    "return OK and the current and previous year view when user has only selected current and previous years for changes" in {

      val application = applicationBuilder(userAnswers = Some(currentYearAndCurrentYearMinus1UserAnswers)).build()

      val request = FakeRequest(GET, routes.HowYouWillGetYourExpensesController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[HowYouWillGetYourExpensesCurrentAndPreviousYearView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view("")(request, messages).toString

      application.stop()
    }
  }
}
