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
import connectors.CitizenDetailsConnector
import controllers.actions.Authed
import controllers.authenticated.routes._
import controllers.routes._
import models.NormalMode
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.CitizenDetailsAddress
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class YourAddressControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with MockitoSugar with BeforeAndAfterEach {

  def onwardRoute = YourEmployerController.onPageLoad(NormalMode).url

  private val mockSessionRepository = mock[SessionRepository]

  lazy val incorrectJson: JsValue = Json.parse(
    s"""
       |{
       |  "IncorrectJson": "incorrectJson"
       |}
     """.stripMargin
  )

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  private val mockCitizenDetailsConnector: CitizenDetailsConnector = mock[CitizenDetailsConnector]

  override def beforeEach(): Unit = reset(mockCitizenDetailsConnector)

  lazy val yourAddressRoute: String = YourAddressController.onPageLoad(NormalMode).url

  "YourAddress Controller" must {

    "redirect to next page and the correct view for a GET and save address to CitizensDetailsAddress" in {
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(200, Some(Json.toJson(address)))))
      when(mockSessionRepository.set(any(), any()))
        .thenReturn(Future.successful(true))

      val userAnswers = minimumUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()
      val request = FakeRequest(GET, yourAddressRoute)
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe onwardRoute

      val newUserAnswers = userAnswers.set(CitizenDetailsAddress, address).success.value

      whenReady(result) {
        _ =>
          verify(mockSessionRepository, times(1)).set(Authed(userAnswersId), newUserAnswers)
      }

      application.stop()
    }

    "redirect to HowWillYouGetYourExpenses if address line one and postcode missing" in {

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers))
        .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
        .build()

      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn
        Future.successful(HttpResponse(200, Some(emptyAddressJson)))

      val request =
        FakeRequest(GET, yourAddressRoute).withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

    "redirect to HowWillYouGetYourExpenses if 404 returned from getAddress" in {
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn
        Future.successful(HttpResponse(404, None))

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers))
          .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
          .build()
      val request =
        FakeRequest(GET, yourAddressRoute).withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

    "redirect to Phone Us if 423 returned from getAddress" in {
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn
        Future.successful(HttpResponse(423, None))

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers))
          .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
          .build()
      val request =
        FakeRequest(GET, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual PhoneUsController.onPageLoad().url

      application.stop()
    }

    "redirect to NextPage if 500 returned from getAddress" in {
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn
        Future.successful(HttpResponse(500, None))

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers))
          .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
          .build()
      val request =
        FakeRequest(GET, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

    "redirect to NextPage if any other status returned from getAddress" in {
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn
        Future.successful(HttpResponse(123, None))

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers))
          .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
          .build()
      val request =
        FakeRequest(GET, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }

    "redirect to Technical Difficulties when call to CitizensDetails fails" in {
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn Future.failed(new Exception)

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers))
        .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
        .build()
      val request =
        FakeRequest(GET, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual TechnicalDifficultiesController.onPageLoad().url

      application.stop()
    }

    "redirect to CheckYourAnswers when could not parse Json to Address model" in {
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any())) thenReturn
        Future.successful(HttpResponse(200, Some(incorrectJson)))

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers))
          .overrides(bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector))
          .build()
      val request =
        FakeRequest(GET, yourAddressRoute)
          .withFormUrlEncodedBody(("value", "true"))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute

      application.stop()
    }
  }
}