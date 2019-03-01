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
import connectors.{CitizenDetailsConnector, TaiConnector}
import models.FlatRateExpenseOptions._
import models.{FlatRateExpense, FlatRateExpenseAmounts, IabdUpdateData, TaiTaxYear, TaxYearSelection}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class TaiServiceSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  private val mockTaiConnector = mock[TaiConnector]
  private val mockCitizenDetailsConnector = mock[CitizenDetailsConnector]
  private val taiService = new TaiService(mockTaiConnector, mockCitizenDetailsConnector)

  private val taxYear = TaiTaxYear()
  private val etag = 1

  private val iabdUpdateData = IabdUpdateData(sequenceNumber = 1, grossAmount = 100)

  "TaiService" must {
    "taiEmployments" when {
      "must return a sequence of employment" in {
        when(mockTaiConnector.taiEmployments(fakeNino, TaiTaxYear(TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYear))))
          .thenReturn(Future.successful(taiEmployment))

        val result = taiService.employments(fakeNino, TaxYearSelection.CurrentYear)

        whenReady(result) {
          result =>
            result mustBe taiEmployment
        }
      }

      "must exception on future failed" in {
        when(mockTaiConnector.taiEmployments(fakeNino, TaiTaxYear(TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYear))))
          .thenReturn(Future.failed(new RuntimeException))

        val result = taiService.employments(fakeNino, TaxYearSelection.CurrentYear)

        whenReady(result.failed) {
          result =>
            result mustBe a[RuntimeException]
        }
      }
    }

    "updateFRE" when {
      "must return a 204 on successful update" in {
        when(mockTaiConnector.taiFREUpdate(fakeNino, taxYear, etag, iabdUpdateData))
          .thenReturn(Future.successful(HttpResponse(NO_CONTENT)))
        when(mockCitizenDetailsConnector.getEtag(fakeNino))
          .thenReturn(Future.successful(etag))

        val result = taiService.updateFRE(fakeNino, taxYear, iabdUpdateData)

        whenReady(result) {
          result =>
            result.status mustBe NO_CONTENT
        }
      }

      "must exception on failed tai FRE update" in {
        when(mockTaiConnector.taiFREUpdate(fakeNino, taxYear, etag, iabdUpdateData))
          .thenReturn(Future.failed(new RuntimeException))
        when(mockCitizenDetailsConnector.getEtag(fakeNino))
          .thenReturn(Future.successful(etag))

        val result = taiService.updateFRE(fakeNino, taxYear, iabdUpdateData)

        whenReady(result.failed) {
          result =>
            result mustBe a[RuntimeException]
        }
      }

      "must exception on failed citizen details ETag request" in {
        when(mockTaiConnector.taiFREUpdate(fakeNino, taxYear, etag, iabdUpdateData))
          .thenReturn(Future.successful(HttpResponse(NO_CONTENT)))
        when(mockCitizenDetailsConnector.getEtag(fakeNino))
          .thenReturn(Future.failed(new RuntimeException))

        val result = taiService.updateFRE(fakeNino, taxYear, iabdUpdateData)

        whenReady(result.failed) {
          result =>
            result mustBe a[RuntimeException]
        }
      }
    }

    "getFREAmount" must {

      "return a Future[Seq[(Option[FlatRateExpenseAmounts], TaiTaxYear)]]" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(Seq(FlatRateExpense(100))))

        val result: Future[Seq[FlatRateExpenseAmounts]] = taiService.getFREAmount(Seq(TaxYearSelection.CurrentYear), fakeNino)

        whenReady(result) {
          result =>
            result mustBe Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))
        }
      }
    }

    "freResponse" must {
      "return FRENoYears when only 200 empty FRE array is returned for all tax years" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(Seq.empty))

        val result = taiService.freResponse(Seq(TaxYearSelection.CurrentYear), fakeNino, claimAmount = 100)

        whenReady(result) {
          result =>
            result mustBe FRENoYears
        }
      }

      "return FREAllYearsAllAmountsSameAsClaimAmount when only 200 is returned and the grossAmount is the same as claimAmount for all tax years" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(Seq(FlatRateExpense(100))))

        val result = taiService.freResponse(Seq(TaxYearSelection.CurrentYear), fakeNino, claimAmount = 100)

        whenReady(result) {
          result =>
            result mustBe FREAllYearsAllAmountsSameAsClaimAmount
        }
      }

      "return FREAllYearsAllAmountsDifferentToClaimAmount when only 200 is returned and the grossAmount is not the same as claimAmount for all tax years" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(Seq(FlatRateExpense(100))))

        val result = taiService.freResponse(Seq(TaxYearSelection.CurrentYear), fakeNino, claimAmount = 200)

        whenReady(result) {
          result =>
            result mustBe FREAllYearsAllAmountsDifferentToClaimAmount
        }
      }

      "return ComplexClaim when only 200 is returned and some tax years are defined and some are empty" in {
        when(mockTaiConnector.getFlatRateExpense(anyString(), any[TaiTaxYear]())(any[HeaderCarrier](),any[ExecutionContext]()))
          .thenReturn(Future.successful(Seq(FlatRateExpense(100))))
          .thenReturn(Future.successful(Seq.empty))

        val result = taiService.freResponse(
          Seq(TaxYearSelection.CurrentYear, TaxYearSelection.CurrentYearMinus1), fakeNino, claimAmount = 200
        )

        whenReady(result) {
          result =>
            result mustBe ComplexClaim
        }
      }
    }

    "freResponseLogic" must {

      "return FREAllYearsAllAmountsSameAsClaimAmount when claimAmount is the same as grossAmount" in {
        val result = taiService.freResponseLogic(Seq(FlatRateExpense(100)), claimAmount = 100)

        result mustBe FREAllYearsAllAmountsSameAsClaimAmount
      }

      "return FREAllYearsAllAmountsDifferentToClaimAmount when claimAmount is not the same as grossAmount" in {
        val result = taiService.freResponseLogic(Seq(FlatRateExpense(100)), claimAmount = 200)

        result mustBe FREAllYearsAllAmountsDifferentToClaimAmount
      }

      "return ComplexClaim when multiple grossAmounts are the same and different to claimAmount" in {
        val result = taiService.freResponseLogic(Seq(FlatRateExpense(100), FlatRateExpense(200)), claimAmount = 200)

        result mustBe ComplexClaim
      }
    }

    "currentPrimaryEmployer" must {

      "return an employer when available" in {
        when(mockTaiConnector.taiEmployments(fakeNino, TaiTaxYear(TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYear))))
          .thenReturn(Future.successful(taiEmployment))

        val result = taiService.employments(fakeNino, TaxYearSelection.CurrentYear)

        whenReady(result) {
          result =>
            result.head.name mustBe "HMRC LongBenton"
        }
      }
    }
  }

}
