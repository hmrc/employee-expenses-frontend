/*
 * Copyright 2020 HM Revenue & Customs
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
import config.NavConstant
import controllers.actions.Authed
import controllers.authenticated.routes._
import controllers.routes._
import forms.authenticated.YourEmployerFormProvider
import models.{NormalMode, TaxYearSelection, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.authenticated.{TaxYearSelectionPage, YourEmployerNames, YourEmployerPage}
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import service.TaiService
import views.html.authenticated.YourEmployerView

import scala.concurrent.Future

class YourEmployerControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new YourEmployerFormProvider()
  private val form = formProvider()
  private val mockTaiService = mock[TaiService]
  private val mockSessionRepository = mock[SessionRepository]
  private val employmentSeq: Seq[String] = Seq("HMRC LongBenton")

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)


  lazy val yourEmployerRoute: String = YourEmployerController.onPageLoad().url

  "YourEmployer Controller" must {

    "return OK and the correct view for a GET and save employer data" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(TaxYearSelectionPage, Seq(TaxYearSelection.CurrentYear)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .overrides(bind[TaiService].toInstance(mockTaiService))
        .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmployment))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YourEmployerView]

      val userAnswers2 = userAnswers
          .set(YourEmployerNames, Seq("HMRC LongBenton")).success.value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, employmentSeq)(request, messages).toString

      whenReady(result) {
        _ =>
          verify(mockSessionRepository, times(1)).set(Authed(userAnswersId), userAnswers2)
      }

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = currentYearFullUserAnswers
        .set(YourEmployerPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .overrides(bind[TaiService].toInstance(mockTaiService))
        .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmployment))

      val request = FakeRequest(GET, yourEmployerRoute)

      val view = application.injector.instanceOf[YourEmployerView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, employmentSeq)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(YourEmployerNames, employmentSeq).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute)))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[TaiService].toInstance(mockTaiService))
          .build()

      val request =
        FakeRequest(POST, yourEmployerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(YourEmployerNames, employmentSeq).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .build()

      val request =
        FakeRequest(POST, yourEmployerRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[YourEmployerView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, employmentSeq)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, yourEmployerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to up Update your employer when no employer is located" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(TaxYearSelectionPage, Seq(TaxYearSelection.CurrentYear)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TaiService].toInstance(mockTaiService))
        .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(Seq.empty))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual UpdateEmployerInformationController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to Session Expired for a POST if no tax year selection in user answers" in {
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute)))
          .overrides(bind[TaiService].toInstance(mockTaiService))
          .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmployment))

      val request = FakeRequest(POST, yourEmployerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a GET if no tax year selection in user answers" in {
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute)))
          .overrides(bind[TaiService].toInstance(mockTaiService))
          .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmployment))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Your Address for a GET when TAI call fails" in {
      val ua1 = emptyUserAnswers.set(TaxYearSelectionPage, TaxYearSelection.values).success.value
      val application =
        applicationBuilder(userAnswers = Some(ua1))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onwardRoute)))
          .overrides(bind[TaiService].toInstance(mockTaiService))
          .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.failed(new Exception))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual UpdateEmployerInformationController.onPageLoad(NormalMode).url

      application.stop()
    }
  }
}
