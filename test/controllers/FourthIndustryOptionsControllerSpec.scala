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
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import forms.FourthIndustryOptionsFormProvider
import models.{FourthIndustryOptions, NormalMode, UserAnswers}
import models.FourthIndustryOptions._
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.{ClaimAmount, FourthIndustryOptionsPage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FourthIndustryOptionsView

import scala.concurrent.Future

class FourthIndustryOptionsControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  lazy val fourthIndustryOptionsRoute = routes.FourthIndustryOptionsController.onPageLoad(NormalMode).url

  private val formProvider = new FourthIndustryOptionsFormProvider()
  private val form = formProvider()
  private val userAnswers = emptyUserAnswers
  private val mockSessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  "FourthIndustryOptions Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, fourthIndustryOptionsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[FourthIndustryOptionsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

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
        view(form.fill(FourthIndustryOptions.values.head), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.generic).toInstance(new FakeNavigator(onwardRoute)))
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
        view(boundForm, NormalMode)(request, messages).toString

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

    for(trade <- FourthIndustryOptions.values){

      val userAnswers = emptyUserAnswers
      val userAnswers2 = trade match {
        case Agriculture => userAnswers
          .set(FourthIndustryOptionsPage, trade).success.value
          .set(ClaimAmount, ClaimAmounts.agriculture).success.value
        case FireService => userAnswers
          .set(FourthIndustryOptionsPage, trade).success.value
          .set(ClaimAmount, ClaimAmounts.fireService).success.value
        case Leisure => userAnswers
          .set(FourthIndustryOptionsPage, trade).success.value
          .set(ClaimAmount, ClaimAmounts.leisure).success.value
        case Prisons => userAnswers
          .set(FourthIndustryOptionsPage, trade).success.value
          .set(ClaimAmount, ClaimAmounts.prisons).success.value
        case _ => userAnswers.set(FourthIndustryOptionsPage, trade).success.value
      }

      s"save correct amount to ClaimAmount when '$trade' is selected" in {

        val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

        val argCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])

        when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

        val request = FakeRequest(POST, fourthIndustryOptionsRoute).withFormUrlEncodedBody(("value", trade.toString))

        val result = route(application, request).value

        whenReady(result) {
          _ =>
            assert(argCaptor.getValue.data == userAnswers2.data)
        }

        application.stop()
      }
    }
  }
}
