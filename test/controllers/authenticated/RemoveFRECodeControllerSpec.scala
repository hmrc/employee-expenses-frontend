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

package controllers.authenticated

import base.SpecBase
import forms.authenticated.RemoveFRECodeFormProvider
import models.{NormalMode, TaxYearSelection, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import pages.authenticated.RemoveFRECodePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.authenticated.RemoveFRECodeView
import repositories.SessionRepository

import scala.concurrent.Future

class RemoveFRECodeControllerSpec extends SpecBase with ScalaFutures {

  def onwardRoute = Call("GET", "/foo")

  lazy val removeFRECodeRoute = routes.RemoveFRECodeController.onPageLoad(NormalMode).url

  val formProvider = new RemoveFRECodeFormProvider()
  val form = formProvider()

  "RemoveFRECode Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, removeFRECodeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RemoveFRECodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(RemoveFRECodePage, TaxYearSelection.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, removeFRECodeRoute)

      val view = application.injector.instanceOf[RemoveFRECodeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(TaxYearSelection.values.head), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, removeFRECodeRoute)
          .withFormUrlEncodedBody(("value", TaxYearSelection.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, removeFRECodeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[RemoveFRECodeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, removeFRECodeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, removeFRECodeRoute)
          .withFormUrlEncodedBody(("value", TaxYearSelection.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    for (option <- TaxYearSelection.values) {
      s"save '${option}' when '${option}' is selected" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .build()

        val sessionRepository = application.injector.instanceOf[SessionRepository]

        val request = FakeRequest(POST, removeFRECodeRoute)
          .withFormUrlEncodedBody(("value", option.toString))

        route(application, request).value.futureValue

        whenReady(sessionRepository.get(userAnswersId)) {
          _.value.get(RemoveFRECodePage).value mustBe option
        }

        application.stop()
      }
    }
  }
}
