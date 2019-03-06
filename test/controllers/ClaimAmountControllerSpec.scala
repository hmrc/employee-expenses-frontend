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
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions, EmployerContributionPage, ExpensesEmployerPaidPage}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import views.html.ClaimAmountView

class ClaimAmountControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with OptionValues {

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  "ClaimAmount Controller" must {

    "return OK and the correct view for a GET when all data is found" in {
      val claimAmount = 60
      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ClaimAmount.toString -> claimAmount,
          EmployerContributionPage.toString -> EmployerContribution.NoContribution.toString
        )
      )
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val sessionRepository = application.injector.instanceOf[SessionRepository]
      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimAmountView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(claimAmount, None, 20, "12.00", 40, "24.00", 19, "11.40", 41, "24.59", "/employee-expenses/which-tax-year")(fakeRequest, messages).toString

      whenReady(sessionRepository.get(userAnswersId)) {
        _.value.get(ClaimAmountAndAnyDeductions).value mustBe 60
      }

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

      "claim amount found but no employer contribution" in {
        val claimAmount = 180
        val userAnswers = UserAnswers(userAnswersId, Json.obj(ClaimAmount.toString -> claimAmount))
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
        val sessionRepository = application.injector.instanceOf[SessionRepository]
        val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "display correct figures when no employer contribution" in {
      val claimAmount = 60
      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ClaimAmount.toString -> claimAmount,
          EmployerContributionPage.toString -> EmployerContribution.NoContribution.toString
        )
      )
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val sessionRepository = application.injector.instanceOf[SessionRepository]
      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimAmountView]

      contentAsString(result) mustEqual
        view(claimAmount, None, 20, "12.00", 40, "24.00", 19, "11.40", 41, "24.59", "/employee-expenses/which-tax-year")(fakeRequest, messages).toString

      whenReady(sessionRepository.get(userAnswersId)) {
        _.value.get(ClaimAmountAndAnyDeductions).value mustBe 60
      }

      application.stop()
    }

    "display correct figures when employer contribution is 15" in {
      val claimAmount = 60
      val employerContribution = 15
      val userAnswers =
        UserAnswers(
          userAnswersId,
          Json.obj(
            ClaimAmount.toString -> claimAmount,
            ExpensesEmployerPaidPage.toString -> employerContribution,
            EmployerContributionPage.toString -> EmployerContribution.SomeContribution.toString
          )
        )
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val sessionRepository = application.injector.instanceOf[SessionRepository]
      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimAmountView]

      contentAsString(result) mustEqual
        view(claimAmount, Some(employerContribution), 20, "9.00", 40, "18.00", 19, "8.55", 41, "18.45", "/employee-expenses/which-tax-year")(fakeRequest, messages).toString

      whenReady(sessionRepository.get(userAnswersId)) {
        _.value.get(ClaimAmountAndAnyDeductions).value mustBe 45
      }

      application.stop()
    }
  }
}
