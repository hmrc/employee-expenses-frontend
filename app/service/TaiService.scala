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
import models.{FlatRateExpense, FlatRateExpenseOptions, IabdUpdateData, TaiTaxYear, TaxCodeRecord, UserAnswers}
import pages.AllFlatRateExpenses
import repositories.SessionRepository
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class TaiService @Inject()(taiConnector: TaiConnector,
                           citizenDetailsConnector: CitizenDetailsConnector,
                           sessionRepository: SessionRepository
                          )(implicit hc: HeaderCarrier) {

  def taxCodeRecords(nino: String): Future[Seq[TaxCodeRecord]] = {
    taiConnector.taiTaxCodeRecords(nino)
  }

  def getAllFlatRateExpenses(nino: String, taxYears: Seq[TaiTaxYear]): Future[Seq[HttpResponse]] = {
    val getAllFRE = taxYears map {
      taxYear =>
        taiConnector.getFlatRateExpense(nino, taxYear)
    }

    Future.sequence(getAllFRE)
  }

  def updateFRE(nino: String, year: TaiTaxYear, expensesData: IabdUpdateData): Future[HttpResponse] = {
    citizenDetailsConnector.getEtag(nino).flatMap {
      etag => taiConnector.taiFREUpdate(nino, year, etag, expensesData)
    }
  }

  def freResponse(taxYears: Seq[TaiTaxYear], nino: String, claimAmount: Int, userAnswers: UserAnswers): Future[FlatRateExpenseOptions] = {

    getAllFlatRateExpenses(nino, taxYears).map {
      case flatRateExpenses if flatRateExpenses.forall(_.status == 404) => FRENoYears
      case flatRateExpenses if flatRateExpenses.forall(_.status == 200) =>
        freResponseLogic(flatRateExpenses.map(_.json.as[FlatRateExpense]), claimAmount, userAnswers)
      case _ => TechnicalDifficulties
    }
  }

  def freResponseLogic(fre: Seq[FlatRateExpense], claimAmount: Int, userAnswers: UserAnswers): FlatRateExpenseOptions = {
    fre match {
      case flatRateExpenses if flatRateExpenses.forall(_.grossAmount == claimAmount) =>
        val updatedUserAnswers: Try[UserAnswers] = userAnswers.set(AllFlatRateExpenses, fre)

        updatedUserAnswers match {
          case Success(value) =>
            sessionRepository.set(value)
            FREAllYearsAllAmountsSameAsClaimAmount
          case Failure(_) =>
            TechnicalDifficulties
        }
      case flatRateExpenses if flatRateExpenses.forall(_.grossAmount != claimAmount) =>
        FREAllYearsAllAmountsDifferentToClaimAmount
      case _ => ComplexClaim
    }
  }
}
