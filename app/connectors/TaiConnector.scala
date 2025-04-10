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

import com.google.inject.Inject
import config.FrontendAppConfig
import models._
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import utils.HttpResponseHelper

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait Defaulting extends Logging {

  def withDefaultToEmptySeq[T: ClassTag](response: HttpResponse)(implicit reads: Reads[Seq[T]]): Seq[T] =

    response.status match {
      case 200 =>
        Json.parse(response.body).validate[Seq[T]] match {
          case JsSuccess(records, _) =>
            records
          case JsError(e) =>
            val typeName: String = implicitly[ClassTag[T]].runtimeClass.getCanonicalName
            logger.error(s"[TaiConnector][$typeName][Json.parse] failed $e")
            Seq.empty
        }
      case _ =>
        Seq.empty
    }

}

@Singleton
class TaiConnector @Inject() (appConfig: FrontendAppConfig, httpClient: HttpClientV2)
    extends HttpResponseHelper
    with Defaulting {

  def taiEmployments(
      nino: String,
      taxYear: TaiTaxYear
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[Employment]] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/employments/years/${taxYear.year}"

    httpClient
      .get(url"$taiUrl")
      .execute[HttpResponse]
      .map(withDefaultToEmptySeq[Employment])
  }

  def getFlatRateExpense(
      nino: String,
      taxYear: TaiTaxYear
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[FlatRateExpense]] = {

    val taiUrl: String =
      s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/expenses/employee-expenses/${appConfig.flatRateExpenseId}"

    httpClient
      .get(url"$taiUrl")
      .execute[HttpResponse]
      .map(withDefaultToEmptySeq[FlatRateExpense])
  }

  def taiFREUpdate(nino: String, taxYear: TaiTaxYear, version: Int, grossAmount: Int)(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext
  ): Future[HttpResponse] = {

    val taiUrl: String =
      s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/expenses/employee-expenses/${appConfig.flatRateExpenseId}"

    val body: IabdEditDataRequest = IabdEditDataRequest(version, grossAmount)

    httpClient
      .post(url"$taiUrl")
      .withBody(Json.toJson(body))
      .execute[HttpResponse]
  }

  def taiTaxCodeRecords(
      nino: String,
      taxYear: TaiTaxYear
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[TaxCodeRecord]] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/income/tax-code-incomes"

    httpClient
      .get(url"$taiUrl")
      .execute[HttpResponse]
      .map(withDefaultToEmptySeq[TaxCodeRecord])
  }

  def taiTaxAccountSummary(
      nino: String,
      taxYear: TaiTaxYear
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/summary"

    httpClient
      .get(url"$taiUrl")
      .execute[HttpResponse]
  }

}
