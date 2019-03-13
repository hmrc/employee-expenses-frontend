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
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import pages.ClaimAmount
import pages.transport.WhichRailwayTradePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.transport.WhichRailwayTradeView

class WhichRailwayTradeControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichRailwayTradeRoute = controllers.transport.routes.WhichRailwayTradeController.onPageLoad(NormalMode).url

  val formProvider = new WhichRailwayTradeFormProvider()
  val form = formProvider()

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

    "save 'vehiclePainters' to ClaimAmount when 'VehiclePainters' is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, whichRailwayTradeRoute).withFormUrlEncodedBody(("value", WhichRailwayTrade.VehiclePainters.toString))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(UnAuthed(userAnswersId))) {
        _.value.get(ClaimAmount).value mustBe ClaimAmounts.Transport.Railways.vehiclePainters
      }

      sessionRepository.remove(UnAuthed(userAnswersId))
      application.stop()
    }

    "save 'vehicleRepairersWagonLifters' to ClaimAmount when 'VehicleRepairersWagonLifters' is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, whichRailwayTradeRoute).withFormUrlEncodedBody(("value", WhichRailwayTrade.VehicleRepairersWagonLifters.toString))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(UnAuthed(userAnswersId))) {
        _.value.get(ClaimAmount).value mustBe ClaimAmounts.Transport.Railways.vehicleRepairersWagonLifters
      }

      sessionRepository.remove(UnAuthed(userAnswersId))
      application.stop()
    }

    "save 'allOther' to ClaimAmount when 'NoneOfTheAbove' is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, whichRailwayTradeRoute).withFormUrlEncodedBody(("value", WhichRailwayTrade.NoneOfTheAbove.toString))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(UnAuthed(userAnswersId))) {
        _.value.get(ClaimAmount).value mustBe ClaimAmounts.Transport.Railways.allOther
      }

      sessionRepository.remove(UnAuthed(userAnswersId))
      application.stop()
    }
  }
}
