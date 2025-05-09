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
import config.NavConstant
import controllers.routes._
import models.{
  FlatRateExpense,
  FlatRateExpenseAmounts,
  FlatRateExpenseOptions,
  NormalMode,
  TaiTaxYear,
  TaxYearSelection,
  UserAnswers
}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimAmountAndAnyDeductions
import pages.authenticated.TaxYearSelectionPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import service.TaiService

import scala.concurrent.Future

class TaxYearSelectionControllerSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val taxYearSelectionRoute: String = routes.TaxYearSelectionController.onPageLoad(NormalMode).url

  private val mockSessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))

  private val mockTaiService: TaiService = mock[TaiService]

  "TaxYearSelection Controller" must {

    "return OK for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, taxYearSelectionRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers().set(TaxYearSelectionPage, TaxYearSelection.values).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, taxYearSelectionRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val answers = emptyUserAnswers.set(ClaimAmountAndAnyDeductions, 100).success.value

      val application =
        applicationBuilder(Some(answers))
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute))
          )
          .overrides(bind[TaiService].toInstance(mockTaiService))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

      when(mockTaiService.freResponse(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(FlatRateExpenseOptions.FRENoYears))
      when(mockTaiService.getFREAmount(any(), any())(any(), any()))
        .thenReturn(Future.successful(Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear(2019)))))

      val request =
        FakeRequest(POST, taxYearSelectionRoute)
          .withFormUrlEncodedBody(("value[0]", TaxYearSelection.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, taxYearSelectionRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to session expired when no claim amount set" in {
      val application =
        applicationBuilder(Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute))
          )
          .overrides(bind[TaiService].toInstance(mockTaiService))
          .build()

      when(mockTaiService.freResponse(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(FlatRateExpenseOptions.FRENoYears))
      when(mockTaiService.getFREAmount(any(), any())(any(), any()))
        .thenReturn(Future.successful(Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear(2019)))))

      val request =
        FakeRequest(POST, taxYearSelectionRoute)
          .withFormUrlEncodedBody(("value[0]", TaxYearSelection.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, taxYearSelectionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, taxYearSelectionRoute)
          .withFormUrlEncodedBody(("value", TaxYearSelection.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
