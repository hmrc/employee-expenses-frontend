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
import forms.TransportVehicleTradeFormProvider
import models.{NormalMode, TransportVehicleTrade, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.transport.TransportVehicleTradePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.TransportVehicleTradeView

class TransportVehicleTradeControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val transportVehicleTradeRoute = routes.TransportVehicleTradeController.onPageLoad(NormalMode).url

  val formProvider = new TransportVehicleTradeFormProvider()
  val form = formProvider()

  "TransportVehicleTrade Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, transportVehicleTradeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TransportVehicleTradeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

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
        view(form.fill(TransportVehicleTrade.values.head), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    for (trade <- TransportVehicleTrade.values) {
      s"redirect to the next page when valid data for '$trade' is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[Navigator].qualifiedWith("Transport").toInstance(new FakeNavigator(onwardRoute)))
            .build()

        val request =
          FakeRequest(POST, transportVehicleTradeRoute)
            .withFormUrlEncodedBody(("value", trade.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, transportVehicleTradeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TransportVehicleTradeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, transportVehicleTradeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, transportVehicleTradeRoute)
          .withFormUrlEncodedBody(("value", TransportVehicleTrade.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
