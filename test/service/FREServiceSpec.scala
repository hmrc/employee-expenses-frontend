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
import connectors.TaiConnector
import models.TaxCodeRecord
import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import pages.ClaimAmount

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



class FREServiceSpec extends SpecBase with MockitoSugar {

  val taiConnectorMock = mock[TaiConnector]

  "FREService" must {
    "When single year is selected"  in {
      "returns same FRE" in {
        val taxCodeRecords = Seq(TaxCodeRecord(
          taxCode = "830L",
          employerName = "Employer Name",
          startDate = LocalDate.parse("2018-06-27"),
          endDate = LocalDate.parse("2019-04-05"),
          payrollNumber = Some("1"),
          pensionIndicator = true,
          primary = true
        ))
        val claimAmount = 80
        val taxYear = 2016
        when (taiConnectorMock.taiTaxCodeRecords(fakeNino)).thenReturn (Future.successful(taxCodeRecords))
        val result = FREService.getFRERespose(FakeNino, taxYear, claimAmount)

      }

      "returns none FRE" in {

      }

      "returns different FRE" in {

      }
    }

  }
}
