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
import forms.TransportVehicleTradeFormProvider
import models.{NormalMode, TransportVehicleTrade, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimAmount
import pages.transport.TransportVehicleTradePage
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.transport.TransportVehicleTradeView

import scala.concurrent.Future

class TransportVehicleTradeControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  lazy val transportVehicleTradeRoute = routes.TransportVehicleTradeController.onPageLoad(NormalMode).url

  private val formProvider = new TransportVehicleTradeFormProvider()
  private val form = formProvider()
  private val userAnswers = emptyUserAnswers

  private val mockSessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  "TransportVehicleTrade Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, transportVehicleTradeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TransportVehicleTradeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(TransportVehicleTradePage, TransportVehicleTrade.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, transportVehicleTradeRoute)

      val view = application.injector.instanceOf[TransportVehicleTradeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(TransportVehicleTrade.values.head), NormalMode)(request, messages).toString

      application.stop()
    }

    for (trade <- TransportVehicleTrade.values if trade != TransportVehicleTrade.Or) {
      s"redirect to the next page when valid data for '$trade' is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
            .overrides(bind[Navigator].qualifiedWith(NavConstant.transport).toInstance(new FakeNavigator(onwardRoute)))
            .build()

        val request = FakeRequest(POST, transportVehicleTradeRoute)
            .withFormUrlEncodedBody(("value", trade.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, transportVehicleTradeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TransportVehicleTradeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, transportVehicleTradeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, transportVehicleTradeRoute)
          .withFormUrlEncodedBody(("value", TransportVehicleTrade.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    for (trade <- TransportVehicleTrade.values if trade != TransportVehicleTrade.Or) {
      val claimAmount = trade match {
        case TransportVehicleTrade.Builder => ClaimAmounts.Transport.VehicleTrade.buildersRepairersWagonLifters
        case TransportVehicleTrade.VehicleRepairerWagonLifter => ClaimAmounts.Transport.VehicleTrade.buildersRepairersWagonLifters
        case TransportVehicleTrade.RailwayVehiclePainter => ClaimAmounts.Transport.Railways.vehiclePainters
        case TransportVehicleTrade.Letterer => ClaimAmounts.Transport.VehicleTrade.paintersLetterersAssistants
        case TransportVehicleTrade.BuildersAssistantOrRepairersAssistant => ClaimAmounts.Transport.VehicleTrade.paintersLetterersAssistants
        case TransportVehicleTrade.NoneOfTheAbove => ClaimAmounts.Transport.VehicleTrade.allOther
      }

      s"save $claimAmount 'buildersRepairersWagonLifters' when '$trade' is selected" in {


        val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

        val request = FakeRequest(POST, transportVehicleTradeRoute)
          .withFormUrlEncodedBody(("value", trade.toString))

        val userAnswers2 = userAnswers
          .set(TransportVehicleTradePage, trade).success.value
          .set(ClaimAmount, claimAmount).success.value

        val result = route(application, request).value

        whenReady(result) {
          _ =>
            verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
        }

        application.stop()
      }
    }
  }
}
