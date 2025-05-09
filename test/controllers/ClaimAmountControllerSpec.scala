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

package controllers

import base.SpecBase
import controllers.actions.UnAuthed
import models.{EmployerContribution, NormalMode, UserAnswers}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions, EmployerContributionPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import service.ClaimAmountService

import scala.concurrent.Future

class ClaimAmountControllerSpec
    extends SpecBase
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar {

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  "ClaimAmount Controller" must {

    "return OK for a GET when all data is found" in {
      val claimAmount = 60
      val ua1 =
        emptyUserAnswers
          .set(ClaimAmount, claimAmount)
          .success
          .value
          .set(EmployerContributionPage, EmployerContribution.YesEmployerContribution)
          .success
          .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = Some(ua1))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val claimAmountService = application.injector.instanceOf[ClaimAmountService]

      val claimAmountAndAnyDeductions = claimAmountService.calculateClaimAmount(ua1, claimAmount)

      val ua2 = ua1.set(ClaimAmountAndAnyDeductions, claimAmountAndAnyDeductions).success.value

      val request = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
      val result  = route(application, request).value

      whenReady(result) { _ =>
        status(result) mustEqual OK

        verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
      }

      application.stop()
    }

    "redirect to Session Expired for a GET" when {
      "no existing data is found" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
        val request     = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
        val result      = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

        application.stop()
      }

      "claim amount found but no employer contribution" in {
        val claimAmount = 180
        val userAnswers = UserAnswers(Json.obj(ClaimAmount.toString -> claimAmount))
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
        val request     = FakeRequest(GET, routes.ClaimAmountController.onPageLoad(NormalMode).url)
        val result      = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

        application.stop()
      }
    }
  }

}
