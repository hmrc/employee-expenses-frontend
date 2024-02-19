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
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.WireMockHelper


class EmployeeWfhExpensesConnectorSpec extends SpecBase with MockitoSugar with WireMockHelper with GuiceOneAppPerSuite
  with ScalaFutures with IntegrationPatience {

  override implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.employee-wfh-expenses-frontend.port" -> server.port
      )
      .build()

  private lazy val employeeWfhExpensesConnector = app.injector.instanceOf[EmployeeWfhExpensesConnector]

  def stubCall(response: ResponseDefinitionBuilder): StubMapping = {
    server.stubFor(
      get(urlPathMatching(s"/employee-working-from-home-expenses/claimed-all-years-status"))
        .willReturn(
          response
        )
    )
  }

  "checkIfAllYearsClaimed" must {
    "handle http 200 response with true as success" in {
      stubCall(
        response = aResponse()
          .withStatus(OK)
          .withBody(Json.obj(
            "claimedAllYearsStatus" -> true
          ).toString())
      )

      whenReady(employeeWfhExpensesConnector.checkIfAllYearsClaimed(hc)) { result =>
        result mustBe true
      }
    }

    "handle http 200 response with false as success" in {
      stubCall(
        response = aResponse()
          .withStatus(OK)
          .withBody(Json.obj(
            "claimedAllYearsStatus" -> false
          ).toString())
      )

      whenReady(employeeWfhExpensesConnector.checkIfAllYearsClaimed(hc)) { result =>
        result mustBe false
      }
    }

    "handle exception as a false response" in {
      whenReady(employeeWfhExpensesConnector.checkIfAllYearsClaimed(hc)) { result =>
        result mustBe false
      }
    }
  }
}
