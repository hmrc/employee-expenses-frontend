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

import scala.util.{Success, Failure}
import com.google.inject.Inject
import connectors.{CitizenDetailsConnector, TaiConnector}
import models.FlatRateExpenseOptions._
import models.{FlatRateExpense, FlatRateExpenseOptions, IabdUpdateData, TaiTaxYear, TaxCodeRecord, TaxYearSelection}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class TaiService @Inject()(taiConnector: TaiConnector,
                           citizenDetailsConnector: CitizenDetailsConnector
                          ) {

  def taxCodeRecords(nino: String)
                    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[TaxCodeRecord]] = {
    taiConnector.taiTaxCodeRecords(nino)
  }

  def updateFRE(nino: String, year: TaiTaxYear, expensesData: IabdUpdateData)
               (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    citizenDetailsConnector.getEtag(nino).flatMap {
      etag => taiConnector.taiFREUpdate(nino, year, etag, expensesData)
    }
  }

  def getAllFlatRateExpenses(nino: String, taxYears: Seq[TaiTaxYear])
                            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[Option[FlatRateExpense]]] = {
    val getAllFRE = taxYears map {
      taxYear =>
        taiConnector.getFlatRateExpense(nino, taxYear)
    }

    Future.sequence(getAllFRE).map {
      _.map(_.json.as[Seq[FlatRateExpense]].headOption)
    }
  }

  def freResponse(taxYearSelection: Seq[TaxYearSelection], nino: String, claimAmount: Int)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FlatRateExpenseOptions] = {

    val taxYears: Seq[TaiTaxYear] = taxYearSelection.map(x => TaiTaxYear(TaxYearSelection.getTaxYear(x)))

    getAllFlatRateExpenses(nino, taxYears).map {
      case freSequence: Seq[Option[FlatRateExpense]] if freSequence.forall(_.isEmpty) =>
        FRENoYears
      case freSequence: Seq[Option[FlatRateExpense]] if freSequence.forall(_.isDefined) =>
        freResponseLogic(freSequence.flatten: Seq[FlatRateExpense], claimAmount: Int)
      case freSequence: Seq[Option[FlatRateExpense]] if freSequence.exists(_.isDefined) && freSequence.exists(_.isEmpty) =>
        ComplexClaim
      case _ =>
        TechnicalDifficulties
    }
  }

  def freResponseLogic(flatRateExpenses: Seq[FlatRateExpense], claimAmount: Int)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): FlatRateExpenseOptions = {

    if (flatRateExpenses.forall(_.grossAmount == claimAmount)) {
      FREAllYearsAllAmountsSameAsClaimAmount
    } else if (flatRateExpenses.forall(_.grossAmount != claimAmount)) {
      FREAllYearsAllAmountsDifferentToClaimAmount
    } else {
      ComplexClaim
    }
  }
}
