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

package controllers.transport

import base.SpecBase
import config.ClaimAmounts
import controllers.actions.UnAuthed
import forms.transport.WhichRailwayTradeFormProvider
import models.{NormalMode, UserAnswers, WhichRailwayTrade}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.ClaimAmount
import pages.transport.{AirlineJobListPage, WhichRailwayTradePage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.transport.WhichRailwayTradeView

import scala.concurrent.Future

class WhichRailwayTradeControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichRailwayTradeRoute = controllers.transport.routes.WhichRailwayTradeController.onPageLoad(NormalMode).url

  private val formProvider = new WhichRailwayTradeFormProvider()
  private val form = formProvider()
  private val userAnswers = emptyUserAnswers

  private val mockSessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  "WhichRailwayTrade Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, whichRailwayTradeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhichRailwayTradeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhichRailwayTradePage, WhichRailwayTrade.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, whichRailwayTradeRoute)

      val view = application.injector.instanceOf[WhichRailwayTradeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(WhichRailwayTrade.values.head), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith("Transport").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, whichRailwayTradeRoute)
          .withFormUrlEncodedBody(("value", WhichRailwayTrade.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, whichRailwayTradeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhichRailwayTradeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, whichRailwayTradeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, whichRailwayTradeRoute)
          .withFormUrlEncodedBody(("value", WhichRailwayTrade.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    for(trade <- WhichRailwayTrade.values) {
      val claimAmount = trade match {
        case WhichRailwayTrade.VehiclePainters => ClaimAmounts.Transport.Railways.vehiclePainters
        case WhichRailwayTrade.VehicleRepairersWagonLifters => ClaimAmounts.Transport.Railways.vehicleRepairersWagonLifters
        case WhichRailwayTrade.NoneOfTheAbove => ClaimAmounts.Transport.Railways.allOther
      }

      s"save '$claimAmount' to ClaimAmount when '$trade' is selected" in {

        val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

        val request = FakeRequest(POST, whichRailwayTradeRoute).withFormUrlEncodedBody(("value", trade.toString))

        val result = route(application, request).value

        val userAnswers2 = userAnswers
          .set(ClaimAmount, claimAmount).success.value
          .set(WhichRailwayTradePage, trade).success.value

        whenReady(result){
          _ =>
            verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
        }

        application.stop()
      }
    }
  }
}
