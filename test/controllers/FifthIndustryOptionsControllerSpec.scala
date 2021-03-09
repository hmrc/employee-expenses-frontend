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

package controllers

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import forms.FifthIndustryOptionsFormProvider
import generators.Generators
import models.FifthIndustryOptions.{Forestry, NoneOfAbove}
import models.{FifthIndustryOptions, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ClaimAmount, FifthIndustryOptionsPage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FifthIndustryOptionsView

import scala.concurrent.Future

class FifthIndustryOptionsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures
  with IntegrationPatience with ScalaCheckPropertyChecks with Generators with OptionValues with BeforeAndAfterEach {

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]
  override def beforeEach(): Unit = {
    reset(mockSessionRepository)
    when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)
  }

  def onwardRoute = Call("GET", "/foo")

  lazy val fifthIndustryOptionsRoute = routes.FifthIndustryOptionsController.onPageLoad(NormalMode).url
  private val formProvider = new FifthIndustryOptionsFormProvider()
  private val form = formProvider()

  "FifthIndustryOptions Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, fifthIndustryOptionsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[FifthIndustryOptionsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(FifthIndustryOptionsPage, FifthIndustryOptions.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, fifthIndustryOptionsRoute)

      val view = application.injector.instanceOf[FifthIndustryOptionsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(FifthIndustryOptions.values.head), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(NavConstant.generic).toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          ).build()

      val request =
        FakeRequest(POST, fifthIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", FifthIndustryOptions.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, fifthIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[FifthIndustryOptionsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, fifthIndustryOptionsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, fifthIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", FifthIndustryOptions.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  for (trade <- FifthIndustryOptions.values) {

    val userAnswers = emptyUserAnswers
    val userAnswers2 = trade match {
      case Forestry => userAnswers
        .set(FifthIndustryOptionsPage, trade).success.value
        .set(ClaimAmount, ClaimAmounts.forestry).success.value
      case NoneOfAbove => userAnswers
        .set(FifthIndustryOptionsPage, trade).success.value
        .set(ClaimAmount, ClaimAmounts.defaultRate).success.value
      case _ => userAnswers.set(FifthIndustryOptionsPage, trade).success.value
    }

    s"save correct amount to ClaimAmount when '$trade' is selected" in {

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val argCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, fifthIndustryOptionsRoute).withFormUrlEncodedBody(("value", trade.toString))

      val result = route(application, request).value

      whenReady(result) {
        _ =>
          assert(argCaptor.getValue.data == userAnswers2.data)
      }

      application.stop()
    }
  }
}
