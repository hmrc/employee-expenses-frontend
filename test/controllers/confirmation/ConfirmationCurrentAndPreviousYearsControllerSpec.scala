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

package controllers.confirmation

import base.SpecBase
import connectors.TaiConnector
import controllers.actions.Authed
import controllers.confirmation.routes._
import controllers.routes._
import models.FlatRateExpenseOptions.FRENoYears
import models.TaxCodeStatus.Live
import models._
import org.mockito.Matchers._
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import service.ClaimAmountService
import views.html.confirmation._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConfirmationCurrentAndPreviousYearsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  val mockTaiConnector: TaiConnector = mock[TaiConnector]
  val mockClaimAmountService: ClaimAmountService = mock[ClaimAmountService]
  val claimAmountService = new ClaimAmountService(frontendAppConfig)
  val claimAmount: Int = currentYearFullUserAnswers.get(ClaimAmountAndAnyDeductions).get
  val claimAmountsAndRates = StandardRate(
    frontendAppConfig.taxPercentageBasicRate,
    frontendAppConfig.taxPercentageHigherRate,
    claimAmountService.calculateTax(frontendAppConfig.taxPercentageBasicRate, claimAmount),
    claimAmountService.calculateTax(frontendAppConfig.taxPercentageHigherRate, claimAmount)
  )

  "ConfirmationCurrentAndPreviousYearsController" must {
    "return OK and the correct ConfirmationCurrentAndPreviousYearsView for a GET with address" in {

      val answers = yearsUserAnswers(Seq(TaxYearSelection.CurrentYear, TaxYearSelection.CurrentYearMinus1))
        .set(CitizenDetailsAddress, address).success.value

      val application = applicationBuilder(userAnswers = Some(answers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any(), any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L", Live))))
      when(mockClaimAmountService.getRates(any(), any())).thenReturn(Seq(claimAmountsAndRates))

      val request = FakeRequest(GET, ConfirmationCurrentAndPreviousYearsController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationCurrentAndPreviousYearsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          claimAmountsAndRates = Seq(claimAmountsAndRates),
          claimAmount = claimAmount,
          employerCorrect = Some(true),
          address = Some(address),
          hasClaimIncreased = false,
          freResponse = FRENoYears,
          npsFreAmount = 100
        )(request, messages, frontendAppConfig).toString

      application.stop()
    }

    "return OK and the correct ConfirmationCurrentAndPreviousYearsView for a GET with address for an increase" in {

      val answers = yearsUserAnswers(Seq(TaxYearSelection.CurrentYear, TaxYearSelection.CurrentYearMinus1))
        .set(CitizenDetailsAddress, address).success.value
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(70)), TaiTaxYear()))).success.value

      val application = applicationBuilder(userAnswers = Some(answers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any(), any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L", Live))))
      when(mockClaimAmountService.getRates(any(), any())).thenReturn(Seq(claimAmountsAndRates))

      val request = FakeRequest(GET, ConfirmationCurrentAndPreviousYearsController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationCurrentAndPreviousYearsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          claimAmountsAndRates = Seq(claimAmountsAndRates),
          claimAmount = claimAmount,
          employerCorrect = Some(true),
          address = Some(address),
          hasClaimIncreased = true,
          freResponse = FRENoYears,
          npsFreAmount = 70
        )(request, messages, frontendAppConfig).toString

      application.stop()
    }

    "return OK and the correct ConfirmationCurrentAndPreviousYearsView for a GET with address for a decrease" in {

      val answers = yearsUserAnswers(Seq(TaxYearSelection.CurrentYear, TaxYearSelection.CurrentYearMinus1))
        .set(CitizenDetailsAddress, address).success.value
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))).success.value

      val application = applicationBuilder(userAnswers = Some(answers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any(), any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L", Live))))
      when(mockClaimAmountService.getRates(any(), any())).thenReturn(Seq(claimAmountsAndRates))

      val request = FakeRequest(GET, ConfirmationCurrentAndPreviousYearsController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationCurrentAndPreviousYearsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          claimAmountsAndRates = Seq(claimAmountsAndRates),
          claimAmount = claimAmount,
          employerCorrect = Some(true),
          address = Some(address),
          hasClaimIncreased = false,
          freResponse = FRENoYears,
          npsFreAmount = 100
        )(request, messages, frontendAppConfig).toString

      application.stop()
    }


    "return OK and the correct ConfirmationCurrentAndPreviousYearsView for a GET without address" in {
      val application = applicationBuilder(userAnswers = Some(yearsUserAnswers(Seq(TaxYearSelection.CurrentYear, TaxYearSelection.CurrentYearMinus1))))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any(), any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L", Live))))
      when(mockClaimAmountService.getRates(any(), any())).thenReturn(Seq(claimAmountsAndRates))

      val request = FakeRequest(GET, ConfirmationCurrentAndPreviousYearsController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationCurrentAndPreviousYearsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          claimAmountsAndRates = Seq(claimAmountsAndRates),
          claimAmount = claimAmount,
          employerCorrect = Some(true),
          address = None,
          hasClaimIncreased = false,
          freResponse = FRENoYears,
          npsFreAmount = 100
        )(request, messages, frontendAppConfig).toString

      application.stop()
    }

    "Redirect to TechnicalDifficulties when call to Tai fails" in {

      val application = applicationBuilder(userAnswers = Some(currentYearFullUserAnswers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any(), any())(any(), any())).thenReturn(Future.failed(new Exception))

      val request = FakeRequest(GET, ConfirmationCurrentAndPreviousYearsController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe TechnicalDifficultiesController.onPageLoad.url

      application.stop()
    }

    "Redirect to SessionExpired when missing userAnswers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, ConfirmationCurrentAndPreviousYearsController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "Remove session on page load" in {

      val application = applicationBuilder(userAnswers = Some(currentYearFullUserAnswers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any(), any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L", Live))))

      val request = FakeRequest(GET, ConfirmationCurrentAndPreviousYearsController.onPageLoad().url)

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
