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

package controllers.authenticated

import base.SpecBase
import connectors.{CitizenDetailsConnector, TaiConnector}
import controllers.authenticated.routes._
import controllers.routes._
import models.FlatRateExpenseOptions
import models.FlatRateExpenseOptions._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.FREResponse
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.SubmissionService
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

class CheckYourAnswersControllerSpec
    extends SpecBase
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with BeforeAndAfterEach {

  private val mockSubmissionService       = mock[SubmissionService]
  private val mockAuditConnector          = mock[AuditConnector]
  private val mockTaiConnector            = mock[TaiConnector]
  private val mockCitizenDetailsConnector = mock[CitizenDetailsConnector]

  override def beforeEach(): Unit = {
    reset(mockAuditConnector)
    reset(mockSubmissionService)
    reset(mockTaiConnector)
    reset(mockCitizenDetailsConnector)
  }

  "Check Your Answers Controller" when {
    "onPageLoad" must {
      "return OK for a GET for a stopped claim" in {
        val userAnswers = minimumUserAnswers.set(FREResponse, FRENoYears).success.value
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
        val request     = FakeRequest(GET, CheckYourAnswersController.onPageLoad.url)
        val result      = route(application, request).value

        status(result) mustEqual OK

        application.stop()
      }

      "return OK for a GET for a changed claim" in {
        val userAnswers = currentYearFullUserAnswers
          .set(FREResponse, FlatRateExpenseOptions.FRESomeYears)
          .success
          .value
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()
        val request     = FakeRequest(GET, CheckYourAnswersController.onPageLoad.url)
        val result      = route(application, request).value

        status(result) mustEqual OK

        application.stop()
      }

      "return OK for a GET for a new claim" in {
        val application = applicationBuilder(userAnswers = Some(currentYearFullUserAnswers)).build()
        val request     = FakeRequest(GET, CheckYourAnswersController.onPageLoad.url)
        val result      = route(application, request).value

        status(result) mustEqual OK

        application.stop()
      }

      "redirect to session expired when no freResponse is found" in {
        val application = applicationBuilder(userAnswers = Some(minimumUserAnswers)).build()
        val request     = FakeRequest(GET, CheckYourAnswersController.onPageLoad.url)
        val result      = route(application, request).value

        status(result) mustEqual SEE_OTHER

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {
        val application = applicationBuilder(userAnswers = None).build()
        val request     = FakeRequest(GET, CheckYourAnswersController.onPageLoad.url)
        val result      = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

        application.stop()
      }
    }

    "acceptAndClaim" must {
      "redirect to next page" in {
        val onwardRoute: Call = Call("GET", "/foo")
        val application       = applicationBuilder(Some(emptyUserAnswers), onwardRoute = Some(onwardRoute)).build()
        val request           = FakeRequest(GET, CheckYourAnswersController.acceptAndClaim.url)
        val result            = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }
    }
  }

}
