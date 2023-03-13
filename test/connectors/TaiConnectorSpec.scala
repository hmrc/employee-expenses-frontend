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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import models.TaxCodeStatus._
import models._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
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
    "return a sequence of Employment on OK" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/employments/years/${taxYear.year}"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(validEmploymentsJson.toString)
          )
      )

      val result: Future[Seq[Employment]] = taiConnector.taiEmployments(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe taiEmployment
      }
    }

    "return an empty sequence on INTERNAL_SERVER_ERROR" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/employments/years/${taxYear.year}"))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val result: Future[Seq[Employment]] = taiConnector.taiEmployments(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on NOT_FOUND" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/employments/years/${taxYear.year}"))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

      val result: Future[Seq[Employment]] = taiConnector.taiEmployments(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on UNAUTHORIZED" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/employments/years/${taxYear.year}"))
          .willReturn(
            aResponse()
              .withStatus(UNAUTHORIZED)
          )
      )

      val result: Future[Seq[Employment]] = taiConnector.taiEmployments(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on OK when empty array returned" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/employments/years/${taxYear.year}"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(emptyEmploymentsSeqJson.toString)
          )
      )

      val result: Future[Seq[Employment]] = taiConnector.taiEmployments(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on OK for Json parse error" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/employments/years/${taxYear.year}"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(invalidEmploymentsJson.toString)
          )
      )

      val result: Future[Seq[Employment]] = taiConnector.taiEmployments(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }
  }

  "taiTaxCodeRecords" must {
    "return a sequence of TaxCodeRecord on OK" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/income/tax-code-incomes"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(validTaxCodeJson.toString)
          )
      )

      val result: Future[Seq[TaxCodeRecord]] = taiConnector.taiTaxCodeRecords(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq(TaxCodeRecord("1150L", Live), TaxCodeRecord("1100L", PotentiallyCeased), TaxCodeRecord("830L", Ceased))
      }
    }

    "return an empty sequence on INTERNAL_SERVER_ERROR" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/income/tax-code-incomes"))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val result: Future[Seq[TaxCodeRecord]] = taiConnector.taiTaxCodeRecords(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on NOT_FOUND" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/income/tax-code-incomes"))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

      val result: Future[Seq[TaxCodeRecord]] = taiConnector.taiTaxCodeRecords(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on UNAUTHORIZED" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/income/tax-code-incomes"))
          .willReturn(
            aResponse()
              .withStatus(UNAUTHORIZED)
          )
      )

      val result: Future[Seq[TaxCodeRecord]] = taiConnector.taiTaxCodeRecords(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on OK when empty array returned" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/income/tax-code-incomes"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(emptySeqInObjectJson.toString)
          )
      )

      val result: Future[Seq[TaxCodeRecord]] = taiConnector.taiTaxCodeRecords(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on OK for Json parse error" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/income/tax-code-incomes"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(invalidJson.toString)
          )
      )

      val result: Future[Seq[TaxCodeRecord]] = taiConnector.taiTaxCodeRecords(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }
  }

  "getFlatRateExpense" must {
    "return a sequence of FlatRateExpense on OK" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/employee-expenses/${frontendAppConfig.flatRateExpenseId}"))
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

    "return an empty sequence on INTERNAL_SERVER_ERROR" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/employee-expenses/${frontendAppConfig.flatRateExpenseId}"))
          .willReturn(
            aResponse()
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )

      val result: Future[Seq[FlatRateExpense]] = taiConnector.getFlatRateExpense(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on NOT_FOUND" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/employee-expenses/${frontendAppConfig.flatRateExpenseId}"))
          .willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
          )
      )

      val result: Future[Seq[FlatRateExpense]] = taiConnector.getFlatRateExpense(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on UNAUTHORIZED" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/employee-expenses/${frontendAppConfig.flatRateExpenseId}"))
          .willReturn(
            aResponse()
              .withStatus(UNAUTHORIZED)
          )
      )

      val result: Future[Seq[FlatRateExpense]] = taiConnector.getFlatRateExpense(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on OK when empty array returned" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/employee-expenses/${frontendAppConfig.flatRateExpenseId}"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(emptySeqJson.toString)
          )
      )

      val result: Future[Seq[FlatRateExpense]] = taiConnector.getFlatRateExpense(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }

    "return an empty sequence on OK for Json parse error" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/employee-expenses/${frontendAppConfig.flatRateExpenseId}"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(invalidJson.toString)
          )
      )

      val result: Future[Seq[FlatRateExpense]] = taiConnector.getFlatRateExpense(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result mustBe Seq.empty
      }
    }
  }

  "taiFREUpdate" must {
    "return a 200 on success" in {
      server.stubFor(
        post(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/expenses/employee-expenses/${frontendAppConfig.flatRateExpenseId}"))
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

  "taiTaxAccountSummary" must {
    "return a 200 on success" in {
      server.stubFor(
        get(urlEqualTo(s"/tai/$fakeNino/tax-account/${taxYear.year}/summary"))
          .willReturn(
            aResponse()
              .withStatus(OK)
          )
      )

      val result: Future[HttpResponse] = taiConnector.taiTaxAccountSummary(fakeNino, taxYear)

      whenReady(result) {
        result =>
          result.status mustBe OK
      }
    }
  }

  val emptySeqInObjectJson: JsValue = Json.parse(
    """{
      |  "data" : []
      |}""".stripMargin)

  val emptySeqJson: JsValue = Json.parse(
    """
      |[]
      |""".stripMargin)

  val invalidJson: JsValue = Json.parse(
    """{
      |  "x" : []
      |}""".stripMargin)

  val emptyEmploymentsSeqJson: JsValue = Json.parse(
    """{
      |  "data" : {
      |    "employments": []
      |  }
      |}""".stripMargin)

  val validEmploymentsJson: JsValue = Json.parse(
    """{
      |  "data" : {
      |    "employments": [{
      |      "name": "HMRC LongBenton",
      |      "startDate": "2018-06-27"
      |    }]
      |  }
      |}""".stripMargin)

  val invalidEmploymentsJson: JsValue = Json.parse(
    """{
      |  "data" : {
      |     "x": [{
      |       "name": "HMRC LongBenton",
      |       "startDate": "2018-06-27"
      |     }]
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
      |{
      |  "data" : [ {
      |    "componentType" : "EmploymentIncome",
      |    "employmentId" : 1,
      |    "amount" : 1100,
      |    "description" : "EmploymentIncome",
      |    "taxCode" : "1150L",
      |    "name" : "Employer1",
      |    "basisOperation" : "Week1Month1BasisOperation",
      |    "status" : "Live",
      |    "inYearAdjustment" : 0
      |  }, {
      |    "componentType" : "EmploymentIncome",
      |    "employmentId" : 2,
      |    "amount" : 0,
      |    "description" : "EmploymentIncome",
      |    "taxCode" : "1100L",
      |    "name" : "Employer2",
      |    "basisOperation" : "OtherBasisOperation",
      |    "status" : "PotentiallyCeased",
      |    "inYearAdjustment" : 321.12
      |  }, {
      |    "componentType" : "EmploymentIncome",
      |    "employmentId" : 3,
      |    "amount" : 0,
      |    "description" : "EmploymentIncome",
      |    "taxCode" : "830L",
      |    "name" : "Employer3",
      |    "basisOperation" : "OtherBasisOperation",
      |    "status" : "Ceased",
      |    "inYearAdjustment" : 400.00
      |  }  ],
      |  "links" : [ ]
      |}
    """.stripMargin
  )
}
