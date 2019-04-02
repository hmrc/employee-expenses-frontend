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
import models.{ScottishRate, StandardRate, TaxCodeRecord}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.ClaimAmountService
import views.html.CurrentYearConfirmationView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CurrentYearConfirmationControllerSpec extends SpecBase with MockitoSugar{

  val mockClaimAmountService: ClaimAmountService = mock[ClaimAmountService]
  val mockTaiConnector: TaiConnector = mock[TaiConnector]


  val claimAmountService = new ClaimAmountService(frontendAppConfig)

  val claimAmount = 80

  val claimAmountsRates = StandardRate(
    basicRate = frontendAppConfig.taxPercentageBand1,
    higherRate = frontendAppConfig.taxPercentageBand2,
    calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
    calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount)
  )

  val scottishClaimAmountsRates = ScottishRate(
    starterRate = frontendAppConfig.taxPercentageScotlandBand1,
    basicRate = frontendAppConfig.taxPercentageScotlandBand2,
    higherRate = frontendAppConfig.taxPercentageScotlandBand3,
    calculatedStarterRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand1, claimAmount),
    calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand2, claimAmount),
    calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand3, claimAmount)
  )

  val claimAmountsAndRates = StandardRate(

    frontendAppConfig.taxPercentageBand1,
    frontendAppConfig.taxPercentageBand2,
    claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
    claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount)
  )

  "CurrentYearConfirmation Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .overrides(bind[ClaimAmountService].toInstance(mockClaimAmountService))
        .build()

      when(mockClaimAmountService.getRates(any(),any())).thenReturn(Seq(claimAmountsAndRates))
      when(mockTaiConnector.taiTaxCodeRecords(any())(any(), any())).thenReturn(Future.successful(Seq(TaxCodeRecord("850L"))))

      val request = FakeRequest(GET, routes.CurrentYearConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CurrentYearConfirmationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(removeFreOption = false,
          claimAmountsAndRates = Seq(claimAmountsRates),
          claimAmount = claimAmount,
          updateEmployerInfo = false,
          updateAddressInfo = true
        )(request, messages).toString

      application.stop()
    }

    "Redirect to TechnicalDifficulties when call to Tai fails" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers))
        .overrides(bind[TaiConnector].toInstance(mockTaiConnector))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any())(any(), any())).thenReturn(Future.failed(new Exception))

      val request = FakeRequest(GET, routes.CurrentYearConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.TechnicalDifficultiesController.onPageLoad().url

      application.stop()
    }

    "redirect to SessionExpired when missing userAnswers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      when(mockTaiConnector.taiTaxCodeRecords(any())(any(), any())).thenReturn(Future.failed(new Exception))

      val request = FakeRequest(GET, routes.CurrentYearConfirmationController.onPageLoad().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
