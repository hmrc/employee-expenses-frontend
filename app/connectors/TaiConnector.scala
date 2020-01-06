/*
 * Copyright 2020 HM Revenue & Customs
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
import javax.inject.Singleton
import models._
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.HttpResponseHelper
import scala.reflect.ClassTag

import scala.concurrent.{ExecutionContext, Future}

trait Defaulting {
  def withDefaultToEmptySeq[T: ClassTag](response: HttpResponse)
                                        (implicit reads: Reads[Seq[T]]): Seq[T] = {

    response.status match {
      case 200 =>
        Json.parse(response.body).validate[Seq[T]] match {
          case JsSuccess(records, _) =>
            records
          case JsError(e) =>
            val typeName: String = implicitly[ClassTag[T]].runtimeClass.getCanonicalName
            Logger.error(s"[TaiConnector][$typeName][Json.parse] failed $e")
            Seq.empty
        }
      case _ =>
        Seq.empty
    }
  }
}

@Singleton
class TaiConnector @Inject()(appConfig: FrontendAppConfig, httpClient: HttpClient) extends HttpResponseHelper with Defaulting {

  def taiEmployments(nino: String, taxYear: TaiTaxYear)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[Employment]] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/employments/years/${taxYear.year}"

    httpClient.GET(taiUrl).map(withDefaultToEmptySeq[Employment])
  }

  def getFlatRateExpense(nino: String, taxYear: TaiTaxYear)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[FlatRateExpense]] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/expenses/employee-expenses/${appConfig.flatRateExpenseId}"

    httpClient.GET(taiUrl).map(withDefaultToEmptySeq[FlatRateExpense])
  }

  def taiFREUpdate(nino: String, taxYear: TaiTaxYear, version: Int, grossAmount: Int)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/expenses/employee-expenses/${appConfig.flatRateExpenseId}"

    val body: IabdEditDataRequest = IabdEditDataRequest(version, grossAmount)

    httpClient.POST[IabdEditDataRequest, HttpResponse](taiUrl, body)
  }

  def taiTaxCodeRecords(nino: String, taxYear: TaiTaxYear)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[TaxCodeRecord]] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/income/tax-code-incomes"

    httpClient.GET(taiUrl).map(withDefaultToEmptySeq[TaxCodeRecord])
  }

  def taiTaxAccountSummary(nino: String, taxYear: TaiTaxYear)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val taiUrl: String = s"${appConfig.taiHost}/tai/$nino/tax-account/${taxYear.year}/summary"

    httpClient.GET(taiUrl)
  }
}
