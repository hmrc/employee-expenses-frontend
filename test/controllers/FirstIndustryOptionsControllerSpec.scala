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
import generators.Generators
import models.FirstIndustryOptions._
import models.{FirstIndustryOptions, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ClaimAmount, FirstIndustryOptionsPage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class FirstIndustryOptionsControllerSpec
    extends SpecBase
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with ScalaCheckPropertyChecks
    with Generators
    with OptionValues {

  def onwardRoute: Call = Call("GET", "/FOO")

  lazy val firstIndustryOptionsRoute: String =
    controllers.routes.FirstIndustryOptionsController.onPageLoad(NormalMode).url

  private val mockSessionRepository = mock[SessionRepository]
  private val userAnswers           = emptyUserAnswers

  when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))

  "FirstIndustryOptionsController" must {

    "return Ok" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request                  = FakeRequest(GET, firstIndustryOptionsRoute)
      val result                   = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers().set(FirstIndustryOptionsPage, FirstIndustryOptions.values.head).success.value
      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request                  = FakeRequest(GET, firstIndustryOptionsRoute)
      val result                   = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "redirect to next page when valid data is submitted" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .overrides(bind[Navigator].qualifiedWith(NavConstant.generic).toInstance(new FakeNavigator(onwardRoute)))
        .build()

      val firstIndustryOptions: Gen[FirstIndustryOptions] = Gen.oneOf(FirstIndustryOptions.values)

      forAll(firstIndustryOptions) { firstIndustryOption =>
        val request = FakeRequest(POST, firstIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", firstIndustryOption.toString))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }

      application.stop()
    }

    "return a bad request and errors when bad data is submitted" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, firstIndustryOptionsRoute)
        .withFormUrlEncodedBody(("value", "invalidData"))
      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to session expired for a Get if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None)
        .build()
      val request = FakeRequest(GET, firstIndustryOptionsRoute)
        .withFormUrlEncodedBody(("value", FirstIndustryOptions.options.head.value))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to session expired for a Post if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None)
        .build()
      val request = FakeRequest(POST, firstIndustryOptionsRoute)
        .withFormUrlEncodedBody(("value", FirstIndustryOptions.options.head.value))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

  for (trade <- FirstIndustryOptions.values) {
    val userAnswers2 = trade match {
      case Retail =>
        userAnswers
          .set(FirstIndustryOptionsPage, trade)
          .success
          .value
          .set(ClaimAmount, ClaimAmounts.defaultRate)
          .success
          .value
      case _ =>
        userAnswers
          .set(FirstIndustryOptionsPage, trade)
          .success
          .value
    }

    s"save correct amount to ClaimAmount when '$trade' is selected" in {

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val argCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockSessionRepository.set(any(), argCaptor.capture())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, firstIndustryOptionsRoute).withFormUrlEncodedBody(("value", trade.toString))

      val result = route(application, request).value

      whenReady(result)(_ => assert(argCaptor.getValue.data == userAnswers2.data))

      application.stop()
    }
  }

}
