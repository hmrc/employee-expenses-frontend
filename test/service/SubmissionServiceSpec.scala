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

package service

import base.SpecBase
import connectors.TaiConnector
import models.{TaiTaxYear, TaxYearSelection}
import models.TaxYearSelection._
import org.joda.time.LocalDate
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmissionServiceSpec extends SpecBase with MockitoSugar with ScalaFutures with BeforeAndAfterEach with IntegrationPatience {

  private val mockTaiService = mock[TaiService]
  private val mockTaiConnector = mock[TaiConnector]
  private val submissionService = new SubmissionService(mockTaiService, mockTaiConnector)

  private val currentTaxYear = Seq(CurrentYear)
  private val taxYearsWithCurrentYear = Seq(CurrentYear, CurrentYearMinus1)
  private val taxYearsWithoutCurrentYear = Seq(CurrentYearMinus1, CurrentYearMinus2)
  private val claimAmount = 100

  override def beforeEach() = reset(mockTaiConnector)


  "SubmissionService" when {
    "getTaxYearsToUpdate" must {
      val beforeApril = new LocalDate(LocalDate.now.getYear, 2, 4)
      val afterApril = new LocalDate(LocalDate.now.getYear, 6, 4)
      val april5th = new LocalDate(LocalDate.now.getYear, 4, 5)

      "return correct taxYears when date is before April 6th and currentYear is passed in and no next year record" in {

        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(400)))

        val result = submissionService.getTaxYearsToUpdate(fakeNino, currentTaxYear, beforeApril)

        whenReady(result) {
          result =>
            result.length mustBe 1
            result.contains(CurrentYear) mustBe true
        }

        verify(mockTaiConnector, times(1)).taiTaxAccountSummary(fakeNino, TaiTaxYear(getTaxYear(CurrentYear) + 1))
      }

      "return correct taxYear when date is before April 6th and currentYear is passed in and next year record available" in {

        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200)))

        val result = submissionService.getTaxYearsToUpdate(fakeNino, taxYearsWithCurrentYear, beforeApril)

        whenReady(result) {
          result =>
            result.length mustBe 3
            result.contains(CurrentYear) mustBe true
            result.contains(NextYear) mustBe true
            result.contains(CurrentYearMinus1) mustBe true
        }

        verify(mockTaiConnector, times(1)).taiTaxAccountSummary(fakeNino, TaiTaxYear(getTaxYear(CurrentYear) + 1))
      }

      "return correct data when date is in April, current year and next year record is available" in {
        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200)))

        val result = submissionService.getTaxYearsToUpdate(fakeNino, currentTaxYear, april5th)

        whenReady(result) {
          result =>
            result.length mustBe 2
            result.contains(CurrentYear) mustBe true
            result.contains(NextYear) mustBe true
        }

        verify(mockTaiConnector, times(1)).taiTaxAccountSummary(fakeNino, TaiTaxYear(getTaxYear(CurrentYear) + 1))
      }

      "return correct data when date is after April, current year selected" in {

        val result = submissionService.getTaxYearsToUpdate(fakeNino, currentTaxYear, afterApril)

        whenReady(result) {
          result =>
            result.length mustBe 1
            result.contains(CurrentYear) mustBe true
        }

        verify(mockTaiConnector, times(0)).taiTaxAccountSummary(fakeNino, TaiTaxYear(getTaxYear(CurrentYear) + 1))
      }

      "return correct data when no current year in selection" in {
        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(500)))

        val result = submissionService.getTaxYearsToUpdate(fakeNino, taxYearsWithoutCurrentYear, beforeApril)

        whenReady(result) {
          result =>
            result.length mustBe 2
            result.contains(CurrentYearMinus1) mustBe true
            result.contains(CurrentYearMinus2) mustBe true
        }
      }
    }

    "submitFRE" must {
      "return true when give 204 response" in {
        when(mockTaiService.updateFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(204)))

        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200)))

        val result: Future[Seq[HttpResponse]] = submissionService.submitFRE(fakeNino, currentTaxYear, claimAmount)

        whenReady(result) {
          res =>
            res mustBe a[Seq[_]]
            res.head.status mustBe 204
        }
      }

      "return false when give 500 response" in {
        when(mockTaiService.updateFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(500)))

        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200)))

        val result = submissionService.submitFRE(fakeNino, currentTaxYear, claimAmount)

        whenReady(result) {
          res =>
            res mustBe a[Seq[_]]
            res.head.status mustBe 500
        }
      }
    }

    "removeFRE" must {
      "return 204 when given 204 response" in {
        when(mockTaiService.updateFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(204)))

        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200)))

        val result = submissionService.removeFRE(fakeNino, currentTaxYear, TaxYearSelection.CurrentYear)

        whenReady(result) {
          res =>
            res mustBe a[Seq[_]]
            res.head.status mustBe 204
        }
      }

      "return false when give 500 response" in {
        when(mockTaiService.updateFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(500)))

        when(mockTaiConnector.taiTaxAccountSummary(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(204)))

        val result = submissionService.removeFRE(fakeNino, currentTaxYear, TaxYearSelection.CurrentYear)

        whenReady(result) {
          res =>
            res mustBe a[Seq[_]]
            res.head.status mustBe 500
        }
      }
    }
  }
}
