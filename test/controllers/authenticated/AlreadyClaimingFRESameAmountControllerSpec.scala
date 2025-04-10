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
import models.{AlreadyClaimingFRESameAmount, FlatRateExpense, FlatRateExpenseAmounts, NormalMode, TaiTaxYear}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.authenticated.AlreadyClaimingFRESameAmountPage
import pages.{ClaimAmountAndAnyDeductions, FREAmounts}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class AlreadyClaimingFRESameAmountControllerSpec
    extends SpecBase
    with ScalaFutures
    with MockitoSugar
    with IntegrationPatience {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val alreadyClaimingFRESameAmountRoute: String =
    routes.AlreadyClaimingFRESameAmountController.onPageLoad(NormalMode).url

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))

  "AlreadyClaimingFRESameAmount Controller" must {

    "return OK for a GET" in {
      val userAnswers = currentYearFullUserAnswers
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear())))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = currentYearFullUserAnswers
        .set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.values.head)
        .success
        .value
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear())))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = currentYearFullUserAnswers
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear())))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", AlreadyClaimingFRESameAmount.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = currentYearFullUserAnswers
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear())))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired on GET if ClaimAmount is missing" in {

      val userAnswers = emptyUserAnswers
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear())))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired on GET if FREAmount is missing" in {

      val userAnswers = emptyUserAnswers.set(ClaimAmountAndAnyDeductions, 100).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired on POST if ClaimAmount is missing" in {

      val userAnswers = emptyUserAnswers
        .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear())))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired on POST if FREAmount is missing" in {

      val userAnswers = emptyUserAnswers.set(ClaimAmountAndAnyDeductions, 100).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", AlreadyClaimingFRESameAmount.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
