/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.mergedJourney

import base.SpecBase
import connectors.CitizenDetailsConnector
import models.mergedJourney._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.http.HttpResponse

import java.time.Instant
import scala.concurrent.Future

class ClaimsCompleteControllerSpec
    extends SpecBase
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterEach {

  private val mockSessionRepository       = mock[SessionRepository]
  private val mockCitizenDetailsConnector = mock[CitizenDetailsConnector]

  override def beforeEach(): Unit = {
    reset(mockSessionRepository)
    reset(mockCitizenDetailsConnector)
  }

  val claimsCompleteUrl: String = "/employee-expenses/claims-complete"

  s"$claimsCompleteUrl" must {
    "return OK with a view when the claims are complete and there is an address" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository),
          bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector)
        )
        .build()

      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrentPrevious,
        ClaimCompleteCurrent,
        ClaimCompletePrevious,
        Instant.now()
      )
      when(mockSessionRepository.getMergedJourney(any())).thenReturn(Future.successful(Some(testJourney)))
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, validAddressJson.toString)))

      val request = FakeRequest(GET, claimsCompleteUrl)

      val result = route(application, request).value

      status(result) mustBe OK

      application.stop()
    }

    "return OK with a view when the claims are complete and there is no address" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrentPrevious,
        ClaimCompleteCurrent,
        ClaimCompletePrevious,
        Instant.now()
      )
      when(mockSessionRepository.getMergedJourney(any())).thenReturn(Future.successful(Some(testJourney)))
      when(mockCitizenDetailsConnector.getAddress(any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(NOT_FOUND, Json.obj().toString())))

      val request = FakeRequest(GET, claimsCompleteUrl)

      val result = route(application, request).value

      status(result) mustBe OK

      application.stop()
    }

    "redirect to claim your expenses if there are pending claims" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val testJourney =
        MergedJourney(userAnswersId, ClaimPending, ClaimCompleteCurrent, ClaimUnsuccessful, Instant.now())
      when(mockSessionRepository.getMergedJourney(any())).thenReturn(Future.successful(Some(testJourney)))

      val request = FakeRequest(GET, claimsCompleteUrl)

      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.mergedJourney.routes.ClaimYourExpensesController.show.url)

      application.stop()
    }

    "redirect to eligibility checker if there is no journey config" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      when(mockSessionRepository.getMergedJourney(any())).thenReturn(Future.successful(None))

      val request = FakeRequest(GET, claimsCompleteUrl)

      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("http://localhost:8787/claim-tax-relief-expenses")

      application.stop()
    }

    "return NOT_IMPLEMENTED if FS is disabled" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .configure(
          "feature-switch.merged-journey" -> false
        )
        .build()

      val request = FakeRequest(GET, claimsCompleteUrl)

      val result = route(application, request).value

      status(result) mustBe NOT_IMPLEMENTED

      application.stop()
    }
  }

}
