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
import models.FlatRateExpenseOptions._
import models._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class TaiService @Inject()(taiConnector: TaiConnector,
                           citizenDetailsConnector: CitizenDetailsConnector
                          ) {

  def employments(nino: String, taxYearSelection: TaxYearSelection)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[Employment]] = {

    val taxYear: TaiTaxYear = TaiTaxYear(TaxYearSelection.getTaxYear(taxYearSelection))

    taiConnector.taiEmployments(nino, taxYear)
  }

  def updateFRE(nino: String, year: TaiTaxYear, grossAmount: Int)
               (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    citizenDetailsConnector.getEtag(nino).flatMap {
      etag =>
        taiConnector.taiFREUpdate(nino, year, etag, grossAmount)
    }
  }


  def getFREAmount(taxYearSelection: Seq[TaxYearSelection], nino: String)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[FlatRateExpenseAmounts]] = {

    val taxYears: Seq[TaiTaxYear] = taxYearSelection.map(x => TaiTaxYear(TaxYearSelection.getTaxYear(x)))

    Future.sequence(taxYears map {
      taxYear =>
        taiConnector.getFlatRateExpense(nino, taxYear).map {
          freAmount =>
            FlatRateExpenseAmounts(freAmount.headOption, taxYear)
        }
    })
  }

  def freResponse(taxYears: Seq[TaxYearSelection], nino: String, claimAmount: Int)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FlatRateExpenseOptions] = {

    getFREAmount(taxYears, nino).map {
      case freSeq if freSeq.forall(_.freAmount.isEmpty) =>
        FRENoYears
      case freSeq if freSeq.forall(_.freAmount.isDefined) =>
        freResponseLogic(freSeq.flatMap(_.freAmount), claimAmount)
      case freSeq if freSeq.exists(_.freAmount.isDefined) && freSeq.exists(_.freAmount.isEmpty) =>
        ComplexClaim
      case _ =>
        TechnicalDifficulties
    }
  }

  def freResponseLogic(flatRateExpenses: Seq[FlatRateExpense], claimAmount: Int)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): FlatRateExpenseOptions = {

    if (flatRateExpenses.forall(_.grossAmount == claimAmount)) {
      FREAllYearsAllAmountsSameAsClaimAmount
    } else {
      FREAllYearsAllAmountsDifferent
    }
  }
}
