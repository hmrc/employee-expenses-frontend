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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import models._
import org.joda.time.LocalDate
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http._
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaiConnectorSpec extends SpecBase with MockitoSugar with WireMockHelper with GuiceOneAppPerSuite with ScalaFutures with IntegrationPatience {

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        conf = "microservice.services.tai.port" -> server.port
      )
      .build()

  private lazy val taiConnector: TaiConnector = app.injector.instanceOf[TaiConnector]

  private val taxYear = TaiTaxYear()

  "taiEmployments" must {
    "return a taiEmployment on success" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/employments/years/${taxYear.year}"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(validEmploymentsJson.toString)
          )
      )

      val result: Future[Seq[Employment]] = taiConnector.taiEmployments(fakeNino, TaiTaxYear(TaxYearSelection.getTaxYear(TaxYearSelection.CurrentYear)))

      whenReady(result) {
        result =>
          result mustBe taiEmployment
      }
    }
  }

  "taiTaxCodeRecords" must {
    "return a taxCodeRecord on success" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/tax-code-change"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(validTaxCodeJson.toString)
          )
      )

      val result: Future[Seq[TaxCodeRecord]] = taiConnector.taiTaxCodeRecords(fakeNino)

      whenReady(result) {
        result =>
          result mustBe Seq(TaxCodeRecord(taxCode = "830L"))
      }
    }
  }

  "getFlatRateExpense" must {
    "return a flatRateExpense on a 200 response" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/flat-rate-expenses"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(validFlatRateJson.toString)
          )
      )

      val result: Future[Seq[FlatRateExpense]] = taiConnector.getFlatRateExpense(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq(FlatRateExpense(120))
      }
    }
  }

  "taiFREUpdate" must {
    "return a 200 on success" in {
      server.stubFor(
        post(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/flat-rate-expenses"))
          .willReturn(
            aResponse()
              .withStatus(OK)
          )
      )

      val result: Future[HttpResponse] = taiConnector.taiFREUpdate(fakeNino, taxYear, 1, 100)

      whenReady(result) {
        result =>
          result.status mustBe OK
      }
    }
  }

  val validEmploymentsJson: JsValue = Json.parse(
    """{
     |  "data" : {
     |    "employments": [{
     |      "name": "HMRC LongBenton",
     |      "startDate": "2018-06-27"
     |    }]
     |  }
     |}""".stripMargin)

  val validFlatRateJson: JsValue = Json.parse(
    """
      |[
      |   {
      |        "nino": "AB123456A",
      |        "sequenceNumber": 201600003,
      |        "taxYear": 2018,
      |        "type": 56,
      |        "source": 26,
      |        "grossAmount": 120,
      |        "receiptDate": null,
      |        "captureDate": null,
      |        "typeDescription": "Flat Rate Job Expenses",
      |        "netAmount": null
      |   }
      |]
      |""".stripMargin)

  val validTaxCodeJson: JsValue = Json.parse(
    """
      | {
      |   "data" : {
      |     "current": [{
      |       "taxCode": "830L",
      |       "employerName": "Employer Name",
      |       "operatedTaxCode": true,
      |       "p2Issued": true,
      |       "startDate": "2018-06-27",
      |       "endDate": "2019-04-05",
      |       "payrollNumber": "1",
      |       "pensionIndicator": true,
      |       "primary": true
      |     }],
      |     "previous": [{
      |       "taxCode": "1150L",
      |       "employerName": "Employer Name",
      |       "operatedTaxCode": true,
      |       "p2Issued": true,
      |       "startDate": "2018-04-06",
      |       "endDate": "2018-06-26",
      |       "payrollNumber": "1",
      |       "pensionIndicator": true,
      |       "primary": true
      |     }]
      |   },
      |   "links" : [ ]
      | }
    """.stripMargin
  )
}
