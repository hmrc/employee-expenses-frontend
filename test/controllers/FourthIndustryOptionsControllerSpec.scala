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

package controllers

import base.SpecBase
import config.ClaimAmounts
import forms.FourthIndustryOptionsFormProvider
import models.{FourthIndustryOptions, NormalMode, UserAnswers}
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import pages.{ClaimAmount, FourthIndustryOptionsPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FourthIndustryOptionsView

class FourthIndustryOptionsControllerSpec extends SpecBase with ScalaFutures with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  lazy val fourthIndustryOptionsRoute = routes.FourthIndustryOptionsController.onPageLoad(NormalMode).url

  val formProvider = new FourthIndustryOptionsFormProvider()
  val form = formProvider()

  "FourthIndustryOptions Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, fourthIndustryOptionsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[FourthIndustryOptionsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(FourthIndustryOptionsPage, FourthIndustryOptions.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, fourthIndustryOptionsRoute)

      val view = application.injector.instanceOf[FourthIndustryOptionsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(FourthIndustryOptions.values.head), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .build()

      val request =
        FakeRequest(POST, fourthIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", FourthIndustryOptions.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, fourthIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[FourthIndustryOptionsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, fourthIndustryOptionsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, fourthIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", FourthIndustryOptions.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }


    "save ClaimAmount when 'Agriculture' is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, fourthIndustryOptionsRoute).withFormUrlEncodedBody(("value", FourthIndustryOptions.Agriculture.toString))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(userAnswersId)) {
        _.value.get(ClaimAmount).value mustBe ClaimAmounts.Generic.agriculture
      }
    }

    "save ClaimAmount when 'FireService' is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, fourthIndustryOptionsRoute).withFormUrlEncodedBody(("value", FourthIndustryOptions.FireService.toString))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(userAnswersId)) {
        _.value.get(ClaimAmount).value mustBe ClaimAmounts.Generic.fireService
      }
    }

    "save ClaimAmount when 'Leisure' is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, fourthIndustryOptionsRoute).withFormUrlEncodedBody(("value", FourthIndustryOptions.Leisure.toString))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(userAnswersId)) {
        _.value.get(ClaimAmount).value mustBe ClaimAmounts.Generic.leisure
      }
    }

    "save ClaimAmount when 'Prisons' is selected" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val sessionRepository = application.injector.instanceOf[SessionRepository]

      val request = FakeRequest(POST, fourthIndustryOptionsRoute).withFormUrlEncodedBody(("value", FourthIndustryOptions.Prisons.toString))

      route(application, request).value.futureValue

      whenReady(sessionRepository.get(userAnswersId)) {
        _.value.get(ClaimAmount).value mustBe ClaimAmounts.Generic.prisons
      }
    }
  }
}
