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
import forms.authenticated.AlreadyClaimingFREDifferentAmountsFormProvider
import models.{AlreadyClaimingFREDifferentAmounts, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.authenticated.AlreadyClaimingFREDifferentAmountsPage
import pages.{ClaimAmountAndAnyDeductions, FREAmounts}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.authenticated.AlreadyClaimingFREDifferentAmountsView

import scala.concurrent.Future

class AlreadyClaimingFREDifferentAmountsControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience {

  def onwardRoute = Call("GET", "/foo")

  lazy val alreadyClaimingFREDifferentAmountsRoute = routes.AlreadyClaimingFREDifferentAmountsController.onPageLoad(NormalMode).url

  private val formProvider = new AlreadyClaimingFREDifferentAmountsFormProvider()
  private val form = formProvider()
  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  "AlreadyClaimingFREDifferentAmounts Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFREDifferentAmountsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AlreadyClaimingFREDifferentAmountsView]

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

      val userAnswers = fullUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, AlreadyClaimingFREDifferentAmounts.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, alreadyClaimingFREDifferentAmountsRoute)

      val view = application.injector.instanceOf[AlreadyClaimingFREDifferentAmountsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form.fill(AlreadyClaimingFREDifferentAmounts.values.head),
          NormalMode,
          fullUserAnswers.get(ClaimAmountAndAnyDeductions).get,
          fullUserAnswers.get(FREAmounts).get
        )(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(fullUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, alreadyClaimingFREDifferentAmountsRoute)
          .withFormUrlEncodedBody(("value", AlreadyClaimingFREDifferentAmounts.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(fullUserAnswers)).build()

      val request =
        FakeRequest(POST, alreadyClaimingFREDifferentAmountsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AlreadyClaimingFREDifferentAmountsView]

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

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, alreadyClaimingFREDifferentAmountsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, alreadyClaimingFREDifferentAmountsRoute)
          .withFormUrlEncodedBody(("value", AlreadyClaimingFREDifferentAmounts.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
