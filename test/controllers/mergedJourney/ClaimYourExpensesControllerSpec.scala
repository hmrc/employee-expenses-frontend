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
import models.mergedJourney._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import java.time.Instant
import scala.concurrent.Future

class ClaimYourExpensesControllerSpec
    extends SpecBase
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterEach {

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  override def beforeEach(): Unit =
    reset(mockSessionRepository)

  val claimYourExpensesUrl: String = "/employee-expenses/claim-your-expenses"

  s"$claimYourExpensesUrl" must {
    "return OK with a view when there is a valid journey set up" in {
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

      val request = FakeRequest(GET, claimYourExpensesUrl)

      val result = route(application, request).value

      status(result) mustBe OK

      application.stop()
    }

    "redirect to eligibility checker if there is no journey config" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      when(mockSessionRepository.getMergedJourney(any())).thenReturn(Future.successful(None))

      val request = FakeRequest(GET, claimYourExpensesUrl)

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

      val request = FakeRequest(GET, claimYourExpensesUrl)

      val result = route(application, request).value

      status(result) mustBe NOT_IMPLEMENTED

      application.stop()
    }
  }

}
