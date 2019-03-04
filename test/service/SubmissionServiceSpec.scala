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

package service

import base.SpecBase
import controllers.routes._
import models.TaxYearSelection
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmissionServiceSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  private val mockTaiService = mock[TaiService]
  private val submissionService = new SubmissionService(mockTaiService)

  private val taxYears = Seq(TaxYearSelection.CurrentYear)
  private val claimAmount = 100

  "SubmissionService" when {
    "submitFRENotInCode" must {
      "return CYA call when give 204 response" in {
        when(mockTaiService.updateFRE(any(),any(),any())(any(),any()))
          .thenReturn(Future.successful(HttpResponse(204)))

        val result = submissionService.submitFRENotInCode(fakeNino, taxYears, claimAmount)

        whenReady(result) {
          _ mustBe CheckYourAnswersController.onPageLoad()
        }
      }

      "return tech difficulties call when give 500 response" in {
        when(mockTaiService.updateFRE(any(),any(),any())(any(),any()))
          .thenReturn(Future.successful(HttpResponse(500)))

        val result = submissionService.submitFRENotInCode(fakeNino, taxYears, claimAmount)

        whenReady(result) {
          _ mustBe TechnicalDifficultiesController.onPageLoad()
        }
      }
    }

    "submissionResult" must {
      "return CYA call when given 204 responses" in {
        val result = submissionService.submissionResult(Future.successful(Seq(HttpResponse(204), HttpResponse(204))))

        whenReady(result) {
          _ mustBe CheckYourAnswersController.onPageLoad()
        }
      }

      "return tech difficulties call when given empty responses" in {
        val result = submissionService.submissionResult(Future.successful(Seq()))

        whenReady(result) {
          _ mustBe TechnicalDifficultiesController.onPageLoad()
        }
      }

      "return tech difficulties call when not given 204 responses" in {
        val result = submissionService.submissionResult(Future.successful(Seq(HttpResponse(500))))

        whenReady(result) {
          _ mustBe TechnicalDifficultiesController.onPageLoad()
        }
      }
    }
  }
}
