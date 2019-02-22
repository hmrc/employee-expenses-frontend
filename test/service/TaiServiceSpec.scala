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
import models.{FlatRateExpense, FlatRateExpenseOptions, IabdUpdateData, TaiTaxYear, TaxCodeRecord, TaxYearSelection}
import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HttpResponse
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaiServiceSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  private val mockTaiConnector = mock[TaiConnector]
  private val mockCitizenDetailsConnector = mock[CitizenDetailsConnector]
  private val taiService = new TaiService(mockTaiConnector, mockCitizenDetailsConnector)

  private val taxYear = TaiTaxYear()
  private val etag = 1
  private val taxCodeRecords = Seq(TaxCodeRecord(
      taxCode = "830L",
      employerName = "Employer Name",
      startDate = LocalDate.parse("2018-06-27"),
      endDate = LocalDate.parse("2019-04-05"),
      payrollNumber = Some("1"),
      pensionIndicator = true,
      primary = true
    ))

  private val validFlatRateJson: JsValue = Json.parse(
    """
      |   {
      |        "nino": "AB123456A",
      |        "sequenceNumber": 201600003,
      |        "taxYear": 2016,
      |        "type": 56,
      |        "source": 26,
      |        "grossAmount": 100,
      |        "receiptDate": null,
      |        "captureDate": null,
      |        "typeDescription": "Flat Rate Job Expenses",
      |        "netAmount": null
      |   }
      |""".stripMargin)

  private val iabdUpdateData = IabdUpdateData(sequenceNumber = 1, grossAmount = 100)

  "TaiService" must {
    "taiTaxCodeRecords" when {
      "must return a sequence of tax code records" in {
        when(mockTaiConnector.taiTaxCodeRecords(fakeNino))
          .thenReturn(Future.successful(taxCodeRecords))

        val result = taiService.taxCodeRecords(fakeNino)

        whenReady(result) {
          result =>
            result mustBe taxCodeRecords
        }
      }

      "must exception on future failed" in {
        when(mockTaiConnector.taiTaxCodeRecords(fakeNino))
          .thenReturn(Future.failed(new RuntimeException))

        val result = taiService.taxCodeRecords(fakeNino)

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

        val result = taiService.updateFRE(fakeNino,taxYear,iabdUpdateData)

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

        val result = taiService.updateFRE(fakeNino,taxYear,iabdUpdateData)

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

        val result = taiService.updateFRE(fakeNino,taxYear,iabdUpdateData)

        whenReady(result.failed) {
          result =>
            result mustBe a[RuntimeException]
        }
      }
    }

    "freResponse" must {

      "return FRENoYears when only 404 is returned for all tax years" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(HttpResponse(NOT_FOUND)))

        val result: Future[FlatRateExpenseOptions] = taiService.freResponse(Seq(TaxYearSelection.CurrentYear), fakeNino, claimAmount = 100)

        whenReady(result) {
          result =>
            result mustBe FRENoYears
        }
      }

      "return FREAllYearsAllAmountsSameAsClaimAmount when only 200 is returned and the grossAmount is the same as claimAmount for all tax years" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(HttpResponse(OK, Some(validFlatRateJson))))

        val result: Future[FlatRateExpenseOptions] = taiService.freResponse(Seq(TaxYearSelection.CurrentYear), fakeNino, claimAmount = 100)

        whenReady(result) {
          result =>
            result mustBe FREAllYearsAllAmountsSameAsClaimAmount
        }
      }

      "return FREAllYearsAllAmountsDifferentToClaimAmount when only 200 is returned and the grossAmount is not the same as claimAmount for all tax years" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(HttpResponse(OK, Some(validFlatRateJson))))

        val result: Future[FlatRateExpenseOptions] = taiService.freResponse(Seq(TaxYearSelection.CurrentYear), fakeNino, claimAmount = 200)

        whenReady(result) {
          result =>
            result mustBe FREAllYearsAllAmountsDifferentToClaimAmount
        }
      }

      "return TechnicalDifficulties when a 500 is returned" in {
        when(mockTaiConnector.getFlatRateExpense(fakeNino, TaiTaxYear()))
          .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR)))

        val result: Future[FlatRateExpenseOptions] = taiService.freResponse(Seq(TaxYearSelection.CurrentYear), fakeNino, claimAmount = 200)

        whenReady(result) {
          result =>
            result mustBe TechnicalDifficulties
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
  }

}
