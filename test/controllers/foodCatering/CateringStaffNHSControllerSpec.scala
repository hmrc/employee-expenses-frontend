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

package controllers.foodCatering

import base.SpecBase
import config.ClaimAmounts
import controllers.actions.UnAuthed
import forms.foodCatering.CateringStaffNHSFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import pages.ClaimAmount
import pages.foodCatering.CateringStaffNHSPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.foodCatering.CateringStaffNHSView

import scala.concurrent.Future

class CateringStaffNHSControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with OptionValues with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new CateringStaffNHSFormProvider()
  val form = formProvider()
  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  lazy val cateringStaffNHSRoute = routes.CateringStaffNHSController.onPageLoad(NormalMode).url

  "CateringStaffNHS Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, cateringStaffNHSRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CateringStaffNHSView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(CateringStaffNHSPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, cateringStaffNHSRoute)

      val view = application.injector.instanceOf[CateringStaffNHSView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith("FoodCatering").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, cateringStaffNHSRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, cateringStaffNHSRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CateringStaffNHSView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cateringStaffNHSRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, cateringStaffNHSRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  "save 'catering' to ClaimAmount when 'Yes' is selected" in {

    when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

    val ua1 = emptyUserAnswers

    val application = applicationBuilder(userAnswers = Some(ua1))
      .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
      .build()

    val request = FakeRequest(POST, cateringStaffNHSRoute)
      .withFormUrlEncodedBody(("value", "true"))

    val ua2 = ua1
      .set(ClaimAmount, ClaimAmounts.Healthcare.catering).success.value
      .set(CateringStaffNHSPage, true).success.value

    whenReady(route(application, request).value) {
      _ =>
        verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
    }

    application.stop()
  }

  "save 'DefaultRate' to ClaimAmount when 'No' is selected" in {

    when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

    val ua1 = emptyUserAnswers

    val application = applicationBuilder(userAnswers = Some(ua1))
      .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
      .build()

    val request = FakeRequest(POST, cateringStaffNHSRoute)
      .withFormUrlEncodedBody(("value", "false"))

    val ua2 = ua1
      .set(ClaimAmount, ClaimAmounts.defaultRate).success.value
      .set(CateringStaffNHSPage, false).success.value

    whenReady(route(application, request).value) {
      _ =>
        verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
    }

    application.stop()
  }
}
