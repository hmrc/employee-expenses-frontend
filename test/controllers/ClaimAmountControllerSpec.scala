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
import models.{EmployerContribution, NormalMode, UserAnswers}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.{ClaimAmount, EmployerContributionPage, ExpensesEmployerPaidPage}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import views.html.ClaimAmountView

class ClaimAmountControllerSpec extends SpecBase {


  def asDocument(html: Html): Document = Jsoup.parse(html.toString())


  "ClaimAmount Controller" must {

    "return OK and the correct view for a GET when all data is found" in {
      val claimAmount = 180
      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ClaimAmount.toString -> claimAmount,
          EmployerContributionPage.toString -> EmployerContribution.None.toString
        )
      )
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimAmountView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(claimAmount, Some("36.00"), Some("72.00"))(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET" when {
      "no existing data is found" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
        val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "display correct figures of 20 and 40 when claimAmount = 100 when no employer contribution" in {
      val claimAmount = 100
      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ClaimAmount.toString -> claimAmount,
          EmployerContributionPage.toString -> EmployerContribution.None.toString
        )
      )
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimAmountView]

      contentAsString(result) mustEqual
        view(claimAmount, Some("20.00"), Some("40.00"))(fakeRequest, messages).toString
      application.stop()
    }

    "display correct figures of 19 and 38 when claimAmount = 100 when employer contribution is 5" in {
      val claimAmount = 100
      val employerContribution = 5
      val userAnswers =
        UserAnswers(
          userAnswersId,
          Json.obj(
            ClaimAmount.toString -> claimAmount,
            ExpensesEmployerPaidPage.toString -> employerContribution,
            EmployerContributionPage.toString -> EmployerContribution.Some.toString
          )
        )
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimAmountView]
      contentAsString(result) mustEqual
        view(claimAmount, Some("19.00"), Some("38.00"))(fakeRequest, messages).toString

      application.stop()
    }
  }
}
