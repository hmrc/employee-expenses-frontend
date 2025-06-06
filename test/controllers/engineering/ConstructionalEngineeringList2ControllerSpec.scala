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

package controllers.engineering

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimAmount
import pages.engineering.ConstructionalEngineeringList2Page
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class ConstructionalEngineeringList2ControllerSpec
    extends SpecBase
    with ScalaFutures
    with MockitoSugar
    with IntegrationPatience
    with OptionValues {

  def onwardRoute: Call = Call("GET", "/foo")

  private val userAnswers = emptyUserAnswers

  lazy val constructionalEngineeringList2Route: String =
    routes.ConstructionalEngineeringList2Controller.onPageLoad(NormalMode).url

  "ConstructionalEngineeringList2 Controller" must {

    "return OK for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, constructionalEngineeringList2Route)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers().set(ConstructionalEngineeringList2Page, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, constructionalEngineeringList2Route)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "redirect to the next page when answer is true" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.engineering).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, constructionalEngineeringList2Route)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to the next page when answer is false" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.engineering).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, constructionalEngineeringList2Route)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, constructionalEngineeringList2Route)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, constructionalEngineeringList2Route)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, constructionalEngineeringList2Route)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "save 'list2' to ClaimAmount when 'Yes' is selected" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, constructionalEngineeringList2Route).withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      val userAnswers2 = userAnswers
        .set(ClaimAmount, ClaimAmounts.ConstructionalEngineering.list2)
        .success
        .value
        .set(ConstructionalEngineeringList2Page, true)
        .success
        .value

      whenReady(result)(_ => verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2))

      application.stop()
    }

    "save no ClaimAmount when 'No' is selected" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, constructionalEngineeringList2Route).withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      val userAnswers2 = userAnswers
        .set(ConstructionalEngineeringList2Page, false)
        .success
        .value

      whenReady(result)(_ => verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2))

      application.stop()
    }
  }

}
