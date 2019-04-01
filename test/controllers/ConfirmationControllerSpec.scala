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
import connectors.TaiConnector
import controllers.actions.Authed
import models.{FlatRateExpenseOptions, Rates, TaxCodeRecord, TaxYearSelection}
import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.ClaimAmountAndAnyDeductions
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import service.ClaimAmountService
import views.html.ConfirmationView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  val mockTaiConnector: TaiConnector = mock[TaiConnector]
  val mockClaimAmountService: ClaimAmountService = mock[ClaimAmountService]
  val claimAmountService = new ClaimAmountService(frontendAppConfig)
  val claimAmount: Int = fullUserAnswers.get(ClaimAmountAndAnyDeductions).get
  val claimAmountsAndRates = Rates(
    None,
    frontendAppConfig.taxPercentageBand1,
    frontendAppConfig.taxPercentageBand2,
    None,
    claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
    claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount),
    None
  )

  "Confirmation Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L"))))
      when(mockClaimAmountService.getRates(any(),any())).thenReturn(Seq(claimAmountsAndRates))

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          taxYearSelections = Seq(TaxYearSelection.CurrentYear),
          removeFreOption = None,
          updateEmployerInfo = None,
          updateAddressInfo = None,
          claimAmount = claimAmount,
          claimAmountsAndRates = Seq(claimAmountsAndRates),
          freResponse = FlatRateExpenseOptions.FRENoYears
        )(fakeRequest, messages).toString

      application.stop()
    }

    "Redirect to TechnicalDifficulties when call to Tai fails" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any())(any(), any())).thenReturn(Future.failed(new Exception))

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.TechnicalDifficultiesController.onPageLoad().url

      application.stop()
    }

    "Redirect to SessionExpired when missing userAnswers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "Remove session on page load" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L"))))

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      whenReady(result) {
        _ =>
          val sessionRepository = application.injector.instanceOf[SessionRepository]
          sessionRepository.get(Authed(userAnswersId)).map(_ mustBe None)
      }

      application.stop()
    }
  }
}
