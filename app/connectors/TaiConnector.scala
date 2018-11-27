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

import javax.inject.Singleton
import com.google.inject.{ImplementedBy, Inject}
import config.FrontendAppConfig
import models.{IabdEditDataRequest, TaxCodeRecord, TaxYear}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import models.TaxCodeRecord
import play.api.libs.json.Reads
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaiConnectorImpl @Inject()(appConfig: FrontendAppConfig, httpClient: HttpClient) extends TaiConnector {

  override def taiTaxCode(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[TaxCodeRecord]] = {

    val taiUrl = s"${appConfig.taiUrl}/tai/$nino/tax-account/tax-code-change"

		implicit val taxCodeReads: Reads[Seq[TaxCodeRecord]] = TaxCodeRecord.format

    httpClient.GET[Seq[TaxCodeRecord]](taiUrl)
  }

  override def taiFREUpdate(nino: String, year: TaxYear, version: Int, newAmount: Int)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val taiUrl = s"${appConfig.taiUrl}/tai/$nino/tax-account/$year/expenses/flat-rate-expenses"
    val body = IabdEditDataRequest(version, newAmount)

    httpClient.POST[IabdEditDataRequest, HttpResponse](taiUrl, body)

  }

}

@ImplementedBy(classOf[TaiConnectorImpl])
trait TaiConnector {
  def taiTaxCode(nino:String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[TaxCodeRecord]]
  def taiFREUpdate(nino: String, year: TaxYear, version: Int, newAmount: Int)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]
}
