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

import com.google.inject.Inject
import connectors.{CitizenDetailsConnector, TaiConnector}
import models.{IabdUpdateData, TaxCodeRecord, TaxYear}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaiService @Inject()(taiConnector: TaiConnector,
                           citizenDetailsConnector: CitizenDetailsConnector
                          ) {

  def taxCodeRecords(nino: String)(implicit hc: HeaderCarrier): Future[Seq[TaxCodeRecord]] = {
    taiConnector.taiTaxCodeRecords(nino)
  }

  def updateFRE(nino: String, year: TaxYear, expensesData: IabdUpdateData)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    citizenDetailsConnector.getEtag(nino).flatMap {
      etag => taiConnector.taiFREUpdate(nino, year, etag, expensesData)
    }
  }

  def getFRE(nino: String, year: TaxYear)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    taiConnector.getIabds(nino, year)
  }

}
