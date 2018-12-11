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
import play.api.libs.json.{JsResultException, Json}
import scala.concurrent.duration.Duration.Inf
import uk.gov.hmrc.http.HttpResponse
import utils.WireMockHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class TaiConnectorSpec extends SpecBase with MockitoSugar with WireMockHelper with GuiceOneAppPerSuite with ScalaFutures with IntegrationPatience {

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        conf = "microservice.services.tai.port" -> server.port
      )
      .build()

  private lazy val taiConnector: TaiConnector = app.injector.instanceOf[TaiConnector]

  private val nino = "AB123456A"
  private val taxYear = TaxYear()

  private val personalTaxRecord = PersonalTaxRecord(
    etag = ETag("123"),
    taxCodeRecord = Seq(TaxCodeRecord(
      taxCode = "830L",
      employerName = "Employer Name",
      startDate = LocalDate.parse("2018-06-27"),
      endDate = LocalDate.parse("2019-04-05"),
      payrollNumber = Some("1"),
      pensionIndicator = true,
      primary = true
    ))
  )

  "taiTaxCode" must {
    "return a tax code record on success" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$nino/tax-account/tax-code-change"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(validJson.toString)
              .withHeader("ETag","123")
          )
      )

      val result: Future[PersonalTaxRecord] = taiConnector.taiTaxCode(nino)

      whenReady(result) {
        result =>
          result mustBe personalTaxRecord
      }

    }

    "return an exception when no ETag in header" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$nino/tax-account/tax-code-change"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(validJson.toString)
          )
      )

      val result: Future[PersonalTaxRecord] = taiConnector.taiTaxCode(nino)

      whenReady(result.failed) {
        result =>
          result mustBe an[RuntimeException]
      }
    }

    "return 500 on failure" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$nino/tax-account/tax-code-change"))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val result: Future[PersonalTaxRecord] = taiConnector.taiTaxCode(nino)

      whenReady(result.failed) {
        result =>
          result mustBe an[Exception]
      }

    }
  }

  "taiFREUpdate" must {
    "return a 200 on success" in {
      server.stubFor(
        post(urlEqualTo(s"/tai/$nino/tax-account/$taxYear/expenses/flat-rate-expenses"))
          .willReturn(
            aResponse()
              .withStatus(OK)
          )
      )

      val result: Future[HttpResponse] = taiConnector.taiFREUpdate(nino, taxYear, 1, IabdUpdateData(1 ,100))

      whenReady(result) {
        result =>
          result.status mustBe OK
      }

    }

    "return 500 on failure" in {
      server.stubFor(
        post(urlEqualTo(s"/tai/$nino/tax-account/$taxYear/expenses/flat-rate-expenses"))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val result: Future[HttpResponse] = taiConnector.taiFREUpdate(nino, taxYear, 1,  IabdUpdateData(1 ,100))

      whenReady(result.failed) {
        result =>
          result mustBe an[Exception]
      }

    }
  }

  val validJson = Json.parse("""{
															 |  "data" : {
															 |    "current": [{
															 |      "taxCode": "830L",
															 |      "employerName": "Employer Name",
															 |      "operatedTaxCode": true,
															 |      "p2Issued": true,
															 |      "startDate": "2018-06-27",
															 |      "endDate": "2019-04-05",
															 |      "payrollNumber": "1",
															 |      "pensionIndicator": true,
															 |      "primary": true
															 |    }],
															 |    "previous": [{
															 |      "taxCode": "1150L",
															 |      "employerName": "Employer Name",
															 |      "operatedTaxCode": true,
															 |      "p2Issued": true,
															 |      "startDate": "2018-04-06",
															 |      "endDate": "2018-06-26",
															 |      "payrollNumber": "1",
															 |      "pensionIndicator": true,
															 |      "primary": true
															 |    }]
															 |  },
															 |  "links" : [ ]
															 |}""".stripMargin)

}
