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
import controllers.actions.UnAuthed
import models.{EmployerContribution, NormalMode, Rates, UserAnswers}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions, EmployerContributionPage}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import service.ClaimAmountService
import views.html.ClaimAmountView

class ClaimAmountControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with OptionValues with MockitoSugar {

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  "ClaimAmount Controller" must {

    "return OK and the correct view for a GET when all data is found" in {
      val claimAmount = 60
      val userAnswers = emptyUserAnswers.set(ClaimAmount, claimAmount).success.value
        .set(EmployerContributionPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val claimAmountService = application.injector.instanceOf[ClaimAmountService]

      val claimAmountsAndRates = Rates(
        basicRate = frontendAppConfig.taxPercentageBand1,
        higherRate = frontendAppConfig.taxPercentageBand2,
        calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
        calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount),
        prefix = None
      )

      val scottishClaimAmountsAndRates = Rates(
        basicRate = frontendAppConfig.taxPercentageScotlandBand1,
        higherRate = frontendAppConfig.taxPercentageScotlandBand2,
        calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand1, claimAmount),
        calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand2, claimAmount),
        prefix = Some('S')
      )

      val sessionRepository = application.injector.instanceOf[SessionRepository]
      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result = route(application, request).value
      val view = application.injector.instanceOf[ClaimAmountView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(claimAmount, None, claimAmountsAndRates, scottishClaimAmountsAndRates, "/employee-expenses/which-tax-year")(fakeRequest, messages).toString

      whenReady(sessionRepository.get(UnAuthed(userAnswersId))) {
        _.value.get(ClaimAmountAndAnyDeductions).value mustBe 60
      }

      sessionRepository.remove(UnAuthed(userAnswersId))
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
        val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }
  }
}
