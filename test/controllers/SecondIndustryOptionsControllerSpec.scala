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

package controllers

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import generators.Generators
import models.SecondIndustryOptions.Education
import models.{NormalMode, SecondIndustryOptions, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ClaimAmount, SecondIndustryOptionsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class SecondIndustryOptionsControllerSpec
    extends SpecBase
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with ScalaCheckPropertyChecks
    with Generators
    with OptionValues {

  private val userAnswers = emptyUserAnswers

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val secondIndustryOptionsRoute: String = routes.SecondIndustryOptionsController.onPageLoad(NormalMode).url

  "SecondIndustryOptions Controller" must {

    "return OK for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, secondIndustryOptionsRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers().set(SecondIndustryOptionsPage, SecondIndustryOptions.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, secondIndustryOptionsRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.generic).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val secondIndustryOptions: Gen[SecondIndustryOptions] = Gen.oneOf(SecondIndustryOptions.values)

      forAll(secondIndustryOptions) { secondIndustryOption =>
        val request = FakeRequest(POST, secondIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", secondIndustryOption.toString))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, secondIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, secondIndustryOptionsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, secondIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", SecondIndustryOptions.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    for (choice <- SecondIndustryOptions.values) {

      val userAnswers2 = choice match {
        case Education =>
          userAnswers
            .set(SecondIndustryOptionsPage, choice)
            .success
            .value
            .set(ClaimAmount, ClaimAmounts.defaultRate)
            .success
            .value
        case _ =>
          userAnswers
            .set(SecondIndustryOptionsPage, choice)
            .success
            .value
      }

      s"save correct data when '$choice' is selected " in {
        val mockSessionRepository = mock[SessionRepository]

        when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

        val request = FakeRequest(POST, secondIndustryOptionsRoute).withFormUrlEncodedBody(("value", choice.toString))

        val result = route(application, request).value

        whenReady(result)(_ => verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2))

        application.stop()
      }
    }
  }

}
