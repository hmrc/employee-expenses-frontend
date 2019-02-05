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

package controllers.electrical

import base.SpecBase
import controllers.electrical.routes._
import forms.electrical.ElectricalFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import pages.ClaimAmount
import pages.electrical.ElectricalPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.electrical.ElectricalView

class ElectricalControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ElectricalFormProvider()
  val form = formProvider()

  lazy val electricalRoute = ElectricalController.onPageLoad(NormalMode).url

  "Electrical Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, electricalRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ElectricalView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ElectricalPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, electricalRoute)

      val view = application.injector.instanceOf[ElectricalView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Electrical").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, electricalRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, electricalRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ElectricalView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, electricalRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, electricalRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "when true save only landry value to claim amount" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, electricalRoute)
        .withFormUrlEncodedBody(("value", "true"))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(userAnswersId)) {
        _.map{_.get(ClaimAmount)} mustBe Some(claimAmountsConfig.Electrical.onlyLaundry)
      }

      application.stop()
    }

    "when false save all other workers value to claim amount" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, electricalRoute)
        .withFormUrlEncodedBody(("value", "false"))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(userAnswersId)) {
        _.map{_.get(ClaimAmount)} mustBe Some(claimAmountsConfig.Electrical.allOther)
      }

      application.stop()
    }
  }
}
