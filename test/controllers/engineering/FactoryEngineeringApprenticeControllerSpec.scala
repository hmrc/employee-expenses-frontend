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

package controllers.engineering

import base.SpecBase
import config.ClaimAmounts
import forms.FactoryEngineeringApprenticeFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import pages.ClaimAmount
import pages.engineering.FactoryEngineeringApprenticePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.engineering.FactoryEngineeringApprenticeView

class FactoryEngineeringApprenticeControllerSpec extends SpecBase with ScalaFutures with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new FactoryEngineeringApprenticeFormProvider()
  val form = formProvider()

  lazy val factoryEngineeringApprenticeRoute: String = routes.FactoryEngineeringApprenticeController.onPageLoad(NormalMode).url

  "FactoryEngineeringApprentice Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, factoryEngineeringApprenticeRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[FactoryEngineeringApprenticeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(FactoryEngineeringApprenticePage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, factoryEngineeringApprenticeRoute)

      val view = application.injector.instanceOf[FactoryEngineeringApprenticeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when answer is true" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Engineering").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, factoryEngineeringApprenticeRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to the next page when answer is false" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Engineering").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, factoryEngineeringApprenticeRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }


    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, factoryEngineeringApprenticeRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[FactoryEngineeringApprenticeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, factoryEngineeringApprenticeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, factoryEngineeringApprenticeRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  "save 'apprentice' to ClaimAmount when 'Yes' is selected" in {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .build()

    val sessionRepository = application.injector.instanceOf[SessionRepository]

    val request = FakeRequest(POST, factoryEngineeringApprenticeRoute)
      .withFormUrlEncodedBody(("value", "true"))

    route(application, request).value.futureValue

    whenReady(sessionRepository.get(userAnswersId)) {
      _.value.get(ClaimAmount).value mustBe ClaimAmounts.FactoryEngineering.apprentice
    }

    application.stop()
  }

  "save 'allOther' to ClaimAmount when 'No' is selected" in {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .build()

    val sessionRepository = application.injector.instanceOf[SessionRepository]

    val request = FakeRequest(POST, factoryEngineeringApprenticeRoute)
      .withFormUrlEncodedBody(("value", "false"))

    route(application, request).value.futureValue

    whenReady(sessionRepository.get(userAnswersId)) {
      _.value.get(ClaimAmount).value mustBe ClaimAmounts.FactoryEngineering.allOther
    }

    application.stop()
  }
}
