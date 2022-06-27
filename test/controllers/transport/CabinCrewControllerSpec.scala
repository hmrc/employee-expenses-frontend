/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.transport

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import forms.transport.CabinCrewFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimAmount
import pages.transport.CabinCrewPage
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.transport.CabinCrewView

import scala.concurrent.Future

class CabinCrewControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience with OptionValues with BeforeAndAfterEach {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new CabinCrewFormProvider()
  private val form = formProvider()

  private val userAnswers = emptyUserAnswers
  private val mockSessionRepository: SessionRepository = mock[SessionRepository]
  override def beforeEach(): Unit = {
    reset(mockSessionRepository)
    when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)
  }

  lazy val cabinCrewRoute = routes.CabinCrewController.onPageLoad(NormalMode).url

  "CabinCrew Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, cabinCrewRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CabinCrewView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(CabinCrewPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, cabinCrewRoute)

      val view = application.injector.instanceOf[CabinCrewView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.transport).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          ).build()

      val request =
        FakeRequest(POST, cabinCrewRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, cabinCrewRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CabinCrewView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cabinCrewRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, cabinCrewRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "save ClaimAmount 'CabinCrew' when true" in {

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, cabinCrewRoute).withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      val userAnswers2 = userAnswers
        .set(ClaimAmount, ClaimAmounts.Transport.Airlines.cabinCrew).success.value
        .set(CabinCrewPage, true).success.value

      whenReady(result) {
        _ =>
          verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
      }

      application.stop()
    }

    "save ClaimAmount 'defaultRate' when false" in {

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, cabinCrewRoute).withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      val userAnswers2 = userAnswers
        .set(ClaimAmount, ClaimAmounts.defaultRate).success.value
        .set(CabinCrewPage, false).success.value

      whenReady(result) {
        _ =>
          verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
      }

      application.stop()
    }

  }
}
