/*
 * Copyright 2021 HM Revenue & Customs
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
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import forms.engineering.TypeOfEngineeringFormProvider
import models.{NormalMode, TypeOfEngineering, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimAmount
import pages.engineering.TypeOfEngineeringPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.engineering.TypeOfEngineeringView

import scala.concurrent.Future

class TypeOfEngineeringControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  lazy val typeOfEngineeringRoute: String = controllers.engineering.routes.TypeOfEngineeringController.onPageLoad(NormalMode).url

  private val mockSessionRepository = mock[SessionRepository]
  private val userAnswers = emptyUserAnswers

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  val formProvider = new TypeOfEngineeringFormProvider()
  val form = formProvider()

  "TypeOfEngineering Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, typeOfEngineeringRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TypeOfEngineeringView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(TypeOfEngineeringPage, TypeOfEngineering.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, typeOfEngineeringRoute)

      val view = application.injector.instanceOf[TypeOfEngineeringView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(TypeOfEngineering.values.head), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.engineering).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, typeOfEngineeringRoute)
          .withFormUrlEncodedBody(("value", TypeOfEngineering.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to the next page when answer is NoneOfTheAbove" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.engineering).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, typeOfEngineeringRoute)
          .withFormUrlEncodedBody(("value", TypeOfEngineering.NoneOfTheAbove.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, typeOfEngineeringRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TypeOfEngineeringView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, typeOfEngineeringRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, typeOfEngineeringRoute)
          .withFormUrlEncodedBody(("value", TypeOfEngineering.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  "save 'defaultRate' to ClaimAmount when 'NoneOfTheAbove' is selected" in {

    val application = applicationBuilder(userAnswers = Some(userAnswers))
      .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
      .build()

    val request = FakeRequest(POST, typeOfEngineeringRoute).withFormUrlEncodedBody(("value", TypeOfEngineering.NoneOfTheAbove.toString))

    val result = route(application, request).value

    val userAnswers2 = userAnswers
      .set(ClaimAmount, ClaimAmounts.defaultRate).success.value
      .set(TypeOfEngineeringPage, TypeOfEngineering.NoneOfTheAbove).success.value

    whenReady(result) {
      _ =>
        verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
    }

    application.stop()
  }

}
