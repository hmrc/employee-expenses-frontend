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

import com.google.inject.{ImplementedBy, Inject}
import config.FrontendAppConfig
import javax.inject.Singleton
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.HttpResponseHelper

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CitizenDetailsConnectorImpl @Inject()(appConfig: FrontendAppConfig, httpClient: HttpClient) extends CitizenDetailsConnector with HttpResponseHelper {
  override def getEtag(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val etagUrl: String = s"${appConfig.citizenDetailsUrl}/citizen-details/$nino/etag"

    httpClient.GET(etagUrl)
  }

  override def getAddress(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val designatoryDetailsUrl: String = s"${appConfig.citizenDetailsUrl}/citizen-details/$nino/designatory-details"

    httpClient.GET(designatoryDetailsUrl)
  }
}

@ImplementedBy(classOf[CitizenDetailsConnectorImpl])
trait CitizenDetailsConnector {
  def getEtag(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]

  def getAddress(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]
}
