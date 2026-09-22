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
import connectors.EmployeeWfhExpensesConnector
import controllers.actions.Authed
import models.mergedJourney.*
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => eqs}
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepository
import uk.gov.hmrc.http.InternalServerException

import java.time.Instant
import scala.concurrent.Future

class MergedJourneyControllerSpec
    extends SpecBase
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterEach {

  private val mockSessionRepository            = mock[SessionRepository]
  private val mockEmployeeWfhExpensesConnector = mock[EmployeeWfhExpensesConnector]

  override def beforeEach(): Unit = {
    reset(mockSessionRepository)
    reset(mockEmployeeWfhExpensesConnector)
  }

  def setupMergedJourneyUrl(wfh: Boolean = true, psubs: Boolean = true, fre: Boolean = true) =
    s"/employee-expenses/merged-journey-set-up?wfh=$wfh&psubs=$psubs&fre=$fre"

  def mergedJourneyContinueUrl(journey: String, status: ClaimStatus) =
    s"/employee-expenses/merged-journey-continue?journey=$journey&status=$status"

  val refreshSessionUrl: String = "/employee-expenses/merged-journey-refresh-session"

  s"/employee-expenses/merged-journey-set-up" must {
    "redirect to claim your expenses after setting up config and checking wfh claims if wfh is selected and not fully claimed" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository),
          bind[EmployeeWfhExpensesConnector].toInstance(mockEmployeeWfhExpensesConnector)
        )
        .build()

      val argumentCaptor: ArgumentCaptor[MergedJourney] = ArgumentCaptor.forClass(classOf[MergedJourney])
      when(mockSessionRepository.setMergedJourney(argumentCaptor.capture())).thenReturn(Future.successful(true))
      when(mockEmployeeWfhExpensesConnector.checkIfAllYearsClaimed(any())).thenReturn(Future.successful(false))
      val request = FakeRequest(GET, setupMergedJourneyUrl(wfh = true, psubs = true, fre = true))

      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.mergedJourney.routes.ClaimYourExpensesController.show.url)
      argumentCaptor.getValue must matchPattern {
        case MergedJourney(`userAnswersId`, ClaimPending, ClaimPending, ClaimPending, _: Instant) =>
      }

      application.stop()
    }

    "redirect to iform after checking wfh claims if wfh is selected and is fully claimed" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository),
          bind[EmployeeWfhExpensesConnector].toInstance(mockEmployeeWfhExpensesConnector)
        )
        .build()

      when(mockEmployeeWfhExpensesConnector.checkIfAllYearsClaimed(any())).thenReturn(Future.successful(true))
      val request = FakeRequest(GET, setupMergedJourneyUrl(wfh = true, psubs = true, fre = true))

      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/digital-forms/form/tax-relief-for-expenses-of-employment/draft/guide")

      application.stop()
    }

    "redirect to claim your expenses after setting up config without checking wfh claims if wfh is not selected" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val argumentCaptor: ArgumentCaptor[MergedJourney] = ArgumentCaptor.forClass(classOf[MergedJourney])
      when(mockSessionRepository.setMergedJourney(argumentCaptor.capture())).thenReturn(Future.successful(true))
      val request = FakeRequest(GET, setupMergedJourneyUrl(wfh = false, psubs = true, fre = true))

      val result = route(application, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.mergedJourney.routes.ClaimYourExpensesController.show.url)
      argumentCaptor.getValue must matchPattern {
        case MergedJourney(`userAnswersId`, ClaimSkipped, ClaimPending, ClaimPending, _: Instant) =>
      }

      application.stop()
    }

    "redirect to eligibility checker if less than 2 claims selected" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(GET, setupMergedJourneyUrl(wfh = true, psubs = false, fre = false))

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

      val request = FakeRequest(GET, setupMergedJourneyUrl())

      val result = route(application, request).value

      status(result) mustBe NOT_IMPLEMENTED

      application.stop()
    }
  }
}
