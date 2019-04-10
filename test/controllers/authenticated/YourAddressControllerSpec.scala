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
import connectors.CitizenDetailsConnector
import controllers.actions.Authed
import controllers.authenticated.routes._
import controllers.routes._
import forms.authenticated.YourAddressFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.CitizenDetailsAddress
import pages.authenticated.YourAddressPage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.authenticated.YourAddressView

import scala.concurrent.Future

class YourAddressControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with MockitoSugar with BeforeAndAfterEach {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new YourAddressFormProvider()
  private val form: Form[Boolean] = formProvider()
  private val mockSessionRepository = mock[SessionRepository]
  private val userAnswers = emptyUserAnswers

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  private val mockCitizenDetailsConnector: CitizenDetailsConnector = mock[CitizenDetailsConnector]

  override def beforeEach(): Unit = reset(mockCitizenDetailsConnector)

  lazy val yourAddressRoute: String = YourAddressController.onPageLoad(NormalMode).url

  "YourAddress Controller" must {

    "return OK and the correct view for a GET and save address to 'CitizenDetailsAddress'" in {
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
        .build()

      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn Future.successful(address)

      val request = FakeRequest(GET, yourAddressRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YourAddressView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, address)(request, messages).toString

      val userAnswers2 = userAnswers
          .set(CitizenDetailsAddress, address).success.value

      whenReady(result){
        _ =>
          verify(mockSessionRepository, times(1)).set(Authed(userAnswersId), userAnswers2)
      }

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(YourAddressPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
        .build()

      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn Future.successful(address)

      val request = FakeRequest(GET, yourAddressRoute)

      val view = application.injector.instanceOf[YourAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, address)(request, messages).toString

      application.stop()
    }

    "redirect to update your address if no address returned from Citizen Details" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
          .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
          .build()

      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn Future.successful(emptyAddress)

      val request = FakeRequest(GET, yourAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual UpdateYourAddressController.onPageLoad().url

      application.stop()

    }

    "redirect to the next page when valid data is submitted" in {

      val ua = emptyUserAnswers.set(CitizenDetailsAddress, address).success.value

      val application =
        applicationBuilder(userAnswers = Some(ua))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val ua = emptyUserAnswers.set(CitizenDetailsAddress, address).success.value

      val application = applicationBuilder(userAnswers = Some(ua))
        .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
        .build()

      val request =
        FakeRequest(POST, yourAddressRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[YourAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, address)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, yourAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to UpdateYourAddressController for a GET if citizen details address call fails" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
        .build()

      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn Future.failed(new Exception)

      val request =
        FakeRequest(GET, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual UpdateYourAddressController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if CitizenDetailsAddress is not stored in user answers" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
