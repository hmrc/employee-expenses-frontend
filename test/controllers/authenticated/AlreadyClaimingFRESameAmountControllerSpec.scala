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

package controllers.authenticated

import base.SpecBase
import forms.authenticated.AlreadyClaimingFRESameAmountFormProvider
import models.{AlreadyClaimingFRESameAmount, FlatRateExpense, FlatRateExpenseAmounts, NormalMode, TaiTaxYear}
import navigation.{FakeNavigator, Navigator}
import pages.authenticated.AlreadyClaimingFRESameAmountPage
import pages.{ClaimAmountAndAnyDeductions, FREAmounts}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.authenticated.AlreadyClaimingFRESameAmountView

class AlreadyClaimingFRESameAmountControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val alreadyClaimingFRESameAmountRoute: String = routes.AlreadyClaimingFRESameAmountController.onPageLoad(NormalMode).url

  val formProvider = new AlreadyClaimingFRESameAmountFormProvider()
  val form = formProvider()

  "AlreadyClaimingFRESameAmount Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AlreadyClaimingFRESameAmountView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form,
          NormalMode,
          fullUserAnswers.get(ClaimAmountAndAnyDeductions).get,
          fullUserAnswers.get(FREAmounts).get
        )(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = fullUserAnswers.set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val view = application.injector.instanceOf[AlreadyClaimingFRESameAmountView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form.fill(AlreadyClaimingFRESameAmount.values.head),
          NormalMode,
          fullUserAnswers.get(ClaimAmountAndAnyDeductions).get,
          fullUserAnswers.get(FREAmounts).get
        )(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(fullUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
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

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers)).build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AlreadyClaimingFRESameAmountView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(
          boundForm,
          NormalMode,
          fullUserAnswers.get(ClaimAmountAndAnyDeductions).get,
          fullUserAnswers.get(FREAmounts).get
        )(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired on GET if ClaimAmount is missing" in {

      val userAnswers = emptyUserAnswers.set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired on GET if FREAmount is missing" in {

      val userAnswers = emptyUserAnswers.set(ClaimAmountAndAnyDeductions, 100).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired on POST if ClaimAmount is missing" in {

      val userAnswers = emptyUserAnswers.set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired on POST if FREAmount is missing" in {

      val userAnswers = emptyUserAnswers.set(ClaimAmountAndAnyDeductions, 100).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, alreadyClaimingFRESameAmountRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, alreadyClaimingFRESameAmountRoute)
          .withFormUrlEncodedBody(("value", AlreadyClaimingFRESameAmount.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
