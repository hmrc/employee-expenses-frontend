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

import com.google.inject.{ImplementedBy, Inject}
import config.FrontendAppConfig
import javax.inject.Singleton
import models._
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaiConnectorImpl @Inject()(appConfig: FrontendAppConfig, httpClient: HttpClient) extends TaiConnector {

  override def taiEmployments(nino: String, year: TaiTaxYear)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[Employment]] = {

    val taiUrl: String = s"${appConfig.taiUrl}/tai/$nino/employments/years/${year.year}"

    httpClient.GET[Seq[Employment]](taiUrl)
  }

  override def getFlatRateExpense(nino: String, year: TaiTaxYear)
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[FlatRateExpense]] = {

    val taiUrl: String = s"${appConfig.taiUrl}/tai/$nino/tax-account/${year.year}/expenses/flat-rate-expenses"

    httpClient.GET[Seq[FlatRateExpense]](taiUrl)
  }

  override def taiFREUpdate(nino: String, year: TaiTaxYear, version: Int, grossAmount: Int)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val taiUrl: String = s"${appConfig.taiUrl}/tai/$nino/tax-account/${year.year}/expenses/flat-rate-expenses"

    val body: IabdEditDataRequest = IabdEditDataRequest(version, grossAmount)

    httpClient.POST[IabdEditDataRequest, HttpResponse](taiUrl, body)
  }

  override def taiTaxCodeRecords(nino: String, taxYear: Int)
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[TaxCodeRecord]] = {

    val taiTaxCodeIncomesUrl: String = s"${appConfig.taiUrl}/tai/$nino/tax-account/$taxYear/income/tax-code-incomes"

    httpClient.GET[Seq[TaxCodeRecord]](taiTaxCodeIncomesUrl)
  }

  override def taiTaxAccountSummary(nino: String, year: TaiTaxYear)
                                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val taiUrl: String = s"${appConfig.taiUrl}/tai/$nino/tax-account/${year.year}/summary"

    httpClient.GET(taiUrl)
  }
}

@ImplementedBy(classOf[TaiConnectorImpl])
trait TaiConnector {
  def taiEmployments(nino: String, year: TaiTaxYear)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[Employment]]

  def getFlatRateExpense(nino: String, year: TaiTaxYear)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[FlatRateExpense]]

  def taiFREUpdate(nino: String, year: TaiTaxYear, version: Int, grossAmount: Int)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]

  def taiTaxCodeRecords(nino: String, year: Int)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[TaxCodeRecord]]

  def taiTaxAccountSummary(nino: String, year: TaiTaxYear)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]
}
