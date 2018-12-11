/*
 * Copyright 2018 HM Revenue & Customs
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
import models.TaxCodeRecord
import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaiServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {

  private val mockTaiConnector = mock[TaiConnector]
  val taiService = new TaiService(mockTaiConnector)

  private val nino = "AB123456A"
  private val taxCodeRecords = Seq(TaxCodeRecord(
      taxCode = "830L",
      employerName = "Employer Name",
      startDate = LocalDate.parse("2018-06-27"),
      endDate = LocalDate.parse("2019-04-05"),
      payrollNumber = Some("1"),
      pensionIndicator = true,
      primary = true
    ))

  "service.TaiService" must {
    "taiTaxCodeRecords" when {
      "must return a sequence of tax code records" in {
        when(mockTaiConnector.taiTaxCode(nino)) thenReturn Future.successful(taxCodeRecords)

        val result = taiService.taiTaxCodeRecords(nino)

        whenReady(result) {
          result =>
            result mustBe taxCodeRecords
        }
      }

      "must exception on future failed" in {
        when(mockTaiConnector.taiTaxCode(nino)) thenReturn Future.failed(new RuntimeException)

        val result = taiService.taiTaxCodeRecords(nino)

        whenReady(result.failed) {
          result =>
            result mustBe a[RuntimeException]
        }
      }
    }
  }

}
