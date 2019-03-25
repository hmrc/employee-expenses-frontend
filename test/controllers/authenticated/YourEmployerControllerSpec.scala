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
import controllers.authenticated.routes._
import controllers.routes._
import forms.authenticated.YourEmployerFormProvider
import models.{Employment, NormalMode, TaxYearSelection, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.joda.time.LocalDate
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import pages.YourEmployerName
import pages.authenticated.{TaxYearSelectionPage, YourEmployerPage}
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.TaiService
import views.html.authenticated.YourEmployerView

import scala.concurrent.Future

class YourEmployerControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new YourEmployerFormProvider()
  private val form = formProvider()
  private val mockTaiService = mock[TaiService]

  lazy val yourEmployerRoute: String = YourEmployerController.onPageLoad(NormalMode).url

  lazy val taiEmploymentsWithCurrent: Seq[Employment] = Seq(
    Employment(
      name = "ABC Co",
      startDate = LocalDate.parse("2017-06-27"),
      endDate = Some(LocalDate.parse("2018-06-27"))
    ),
    Employment(
      name = "XYZ Co",
      startDate = LocalDate.parse("2018-06-27"),
      endDate = None
    ),
    Employment(
      name = "123 Co",
      startDate = LocalDate.parse("2014-06-27"),
      endDate = None
    )
  )

  lazy val taiEmploymentsWithoutCurrent: Seq[Employment] = Seq(
    Employment(
      name = "ABC Co",
      startDate = LocalDate.parse("2017-06-27"),
      endDate = Some(LocalDate.parse("2018-06-27"))
    ),
    Employment(
      name = "XYZ Co",
      startDate = LocalDate.parse("2017-06-27"),
      endDate = Some(LocalDate.parse("2018-06-27"))
    ),
    Employment(
      name = "123 Co",
      startDate = LocalDate.parse("2014-06-27"),
      endDate = Some(LocalDate.parse("2016-06-27"))
    )
  )

  "YourEmployer Controller" must {

    "return OK and the correct view for a GET" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(TaxYearSelectionPage, Seq(TaxYearSelection.CurrentYear)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TaiService].toInstance(mockTaiService))
        .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmployment))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YourEmployerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, taiEmployment.head.name)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET and show correct employer name for employments with current active employment" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(TaxYearSelectionPage, Seq(TaxYearSelection.CurrentYear)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TaiService].toInstance(mockTaiService))
        .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmploymentsWithCurrent))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YourEmployerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, taiEmploymentsWithCurrent(1).name)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET and show first employer name for employments with no current active employment" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(TaxYearSelectionPage, Seq(TaxYearSelection.CurrentYear)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TaiService].toInstance(mockTaiService))
        .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmploymentsWithoutCurrent))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YourEmployerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, taiEmploymentsWithoutCurrent.head.name)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = fullUserAnswers
        .set(YourEmployerPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TaiService].toInstance(mockTaiService))
        .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmployment))

      val request = FakeRequest(GET, yourEmployerRoute)

      val view = application.injector.instanceOf[YourEmployerView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, taiEmployment.head.name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(YourEmployerName, taiEmployment.head.name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
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
        .set(YourEmployerName, taiEmployment.head.name).success.value

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
        view(boundForm, NormalMode, taiEmployment.head.name)(fakeRequest, messages).toString

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

    }

    "redirect to Session Expired for a POST if no tax year selection in user answers" in {
      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
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
          .overrides(bind[Navigator].qualifiedWith("Authenticated").toInstance(new FakeNavigator(onwardRoute)))
          .overrides(bind[TaiService].toInstance(mockTaiService))
          .build()

      when(mockTaiService.employments(any(), any())(any(), any())).thenReturn(Future.successful(taiEmployment))

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
