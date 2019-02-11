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
import connectors.TaiConnector
import forms.authenticated.YourEmployerFormProvider
import models.{NormalMode, TaxCodeRecord, UserAnswers}
import navigation.{AuthenticatedNavigator, FakeNavigator, Navigator}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import pages.authenticated.YourEmployerPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.authenticated.YourEmployerView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class YourEmployerControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new YourEmployerFormProvider()
  val mockTaiConnector = mock[TaiConnector]
  val form = formProvider()
  val employerName = "HMRC"
  val taxCodeRecord = TaxCodeRecord

  lazy val yourEmployerRoute = routes.YourEmployerController.onPageLoad(NormalMode).url

  "YourEmployer Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YourEmployerView]

      when(mockTaiConnector.taiTaxCodeRecords(fakeNino)) thenReturn (Future.successful(validTaiJson))

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, employerName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(YourEmployerPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, yourEmployerRoute)

      val view = application.injector.instanceOf[YourEmployerView]

      val result = route(application, request).value

      when(mockTaiConnector.taiTaxCodeRecords(fakeNino)) thenReturn (Future.successful(validTaiJson))


      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode, employerName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
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

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, yourEmployerRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[YourEmployerView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, employerName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, yourEmployerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, yourEmployerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  val validTaiJson = Json.parse(
    """{
      															 |  "data" : {
      															 |    "current": [{
      															 |      "taxCode": "830L",
      															 |      "employerName": "HMRC",
      															 |      "operatedTaxCode": true,
      															 |      "p2Issued": true,
      															 |      "startDate": "2018-06-27",
      															 |      "endDate": "2019-04-05",
      															 |      "payrollNumber": "1",
      															 |      "pensionIndicator": true,
      															 |      "primary": true
      															 |    }],
      															 |    "previous": [{
      															 |      "taxCode": "1150L",
      															 |      "employerName": "DWP",
      															 |      "operatedTaxCode": true,
      															 |      "p2Issued": true,
      															 |      "startDate": "2018-04-06",
      															 |      "endDate": "2018-06-26",
      															 |      "payrollNumber": "1",
      															 |      "pensionIndicator": true,
      															 |      "primary": true
      															 |    }]
      															 |  },
      															 |  "links" : [ ]
      															 |}""".stripMargin)


}
