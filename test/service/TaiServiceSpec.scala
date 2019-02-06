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
import models.{IabdUpdateData, TaxCodeRecord, TaiTaxYear}
import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HttpResponse
import play.api.http.Status._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaiServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {

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

  }

}
