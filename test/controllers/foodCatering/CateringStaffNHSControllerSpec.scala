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

package controllers.foodCatering

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimAmount
import pages.foodCatering.CateringStaffNHSPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class CateringStaffNHSControllerSpec
    extends SpecBase
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val cateringStaffNHSRoute: String = routes.CateringStaffNHSController.onPageLoad(NormalMode).url

  "CateringStaffNHS Controller" must {

    "return OK for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, cateringStaffNHSRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers().set(CateringStaffNHSPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, cateringStaffNHSRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val mockSessionRepository: SessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.foodCatering).toInstance(new FakeNavigator(onwardRoute)))
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

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cateringStaffNHSRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, cateringStaffNHSRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

  "save 'catering' to ClaimAmount when 'Yes' is selected" in {

    val mockSessionRepository: SessionRepository = mock[SessionRepository]

    when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))

    val ua1 = emptyUserAnswers

    val application = applicationBuilder(userAnswers = Some(ua1))
      .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
      .build()

    val request = FakeRequest(POST, cateringStaffNHSRoute)
      .withFormUrlEncodedBody(("value", "true"))

    val ua2 = ua1
      .set(ClaimAmount, ClaimAmounts.Healthcare.catering)
      .success
      .value
      .set(CateringStaffNHSPage, true)
      .success
      .value

    whenReady(route(application, request).value) { _ =>
      verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
    }

    application.stop()
  }

  "save 'DefaultRate' to ClaimAmount when 'No' is selected" in {

    val mockSessionRepository: SessionRepository = mock[SessionRepository]

    when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))

    val ua1 = emptyUserAnswers

    val application = applicationBuilder(userAnswers = Some(ua1))
      .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
      .build()

    val request = FakeRequest(POST, cateringStaffNHSRoute)
      .withFormUrlEncodedBody(("value", "false"))

    val ua2 = ua1
      .set(ClaimAmount, ClaimAmounts.defaultRate)
      .success
      .value
      .set(CateringStaffNHSPage, false)
      .success
      .value

    whenReady(route(application, request).value) { _ =>
      verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
    }

    application.stop()
  }

}
