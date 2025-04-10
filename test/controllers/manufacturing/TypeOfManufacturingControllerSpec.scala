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

package controllers.manufacturing

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import models.TypeOfManufacturing._
import models.{NormalMode, TypeOfManufacturing, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimAmount
import pages.manufacturing.TypeOfManufacturingPage
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class TypeOfManufacturingControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val typeOfManufacturingRoute: String =
    controllers.manufacturing.routes.TypeOfManufacturingController.onPageLoad(NormalMode).url

  private val userAnswers = emptyUserAnswers

  "TypeOfManufacturing Controller" must {

    "return OK for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, typeOfManufacturingRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers().set(TypeOfManufacturingPage, TypeOfManufacturing.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, typeOfManufacturingRoute)

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
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.manufacturing).toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        FakeRequest(POST, typeOfManufacturingRoute)
          .withFormUrlEncodedBody(("value", TypeOfManufacturing.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, typeOfManufacturingRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, typeOfManufacturingRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, typeOfManufacturingRoute)
          .withFormUrlEncodedBody(("value", TypeOfManufacturing.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    for (trade <- TypeOfManufacturing.values) {
      val userAnswers2 = trade match {
        case BrassCopper =>
          userAnswers
            .set(ClaimAmount, ClaimAmounts.Manufacturing.brassCopper)
            .success
            .value
            .set(TypeOfManufacturingPage, trade)
            .success
            .value
        case Glass =>
          userAnswers
            .set(ClaimAmount, ClaimAmounts.Manufacturing.glass)
            .success
            .value
            .set(TypeOfManufacturingPage, trade)
            .success
            .value
        case PreciousMetals =>
          userAnswers
            .set(ClaimAmount, ClaimAmounts.Manufacturing.quarryingPreciousMetals)
            .success
            .value
            .set(TypeOfManufacturingPage, trade)
            .success
            .value
        case NoneOfAbove =>
          userAnswers
            .set(ClaimAmount, ClaimAmounts.defaultRate)
            .success
            .value
            .set(TypeOfManufacturingPage, trade)
            .success
            .value
        case _ =>
          userAnswers
            .set(TypeOfManufacturingPage, trade)
            .success
            .value
      }

      s"save correct amount to ClaimAmount when '$trade' is selected" in {
        val mockSessionRepository = mock[SessionRepository]

        when(mockSessionRepository.set(any(), any())).thenReturn(Future.successful(true))
        val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

        val request = FakeRequest(POST, typeOfManufacturingRoute)
          .withFormUrlEncodedBody(("value", trade.toString))

        val result = route(application, request).value

        whenReady(result)(_ => verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2))

        application.stop()
      }
    }
  }

}
