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
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
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
      response =>
        response.status match {
          case 200 =>
            Json.parse(response.body).validate[ETag] match {
              case JsSuccess(body, _) =>
                taiConnector.taiFREUpdate(nino, year, body.etag.toInt, grossAmount)
              case JsError(e) =>
                Logger.error(s"[TaiService.updateFRE][CitizenDetailsConnector.getEtag][Json.parse] failed $e")
                Future.successful(response)
            }
          case _ =>
            Future.successful(response)
        }
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
      case freSeq if freSeq.exists(_.freAmount.isEmpty) && freSeq.filterNot(_.freAmount.isEmpty).forall(_.freAmount.get.grossAmount == 0) =>
        FRENoYears
      case freSeq if freSeq.forall(_.freAmount.isDefined) && freSeq.forall(_.freAmount.get.grossAmount == 0) =>
        FRENoYears
      case freSeq if freSeq.forall(_.freAmount.isDefined) && freSeq.forall(_.freAmount.get.grossAmount == claimAmount) =>
        FREAllYearsAllAmountsSameAsClaimAmount
      case freSeq if freSeq.forall(_.freAmount.isDefined) && freSeq.forall(_.freAmount.get.grossAmount != claimAmount) =>
        FREAllYearsAllAmountsDifferent
      case freSeq if freSeq.forall(_.freAmount.isDefined) && freSeq.exists(_.freAmount.get.grossAmount != claimAmount) =>
        FREAllYearsSomeAmountsDifferent
      case freSeq if freSeq.exists(_.freAmount.isDefined) && freSeq.exists(_.freAmount.isEmpty) =>
//      case freSeq if freSeq.exists(_.freAmount.isEmpty) && freSeq.filterNot(_.freAmount.isEmpty).forall(_.freAmount.get.grossAmount > 0) =>
        ComplexClaim
      case _ =>
        TechnicalDifficulties
    }
  }
}
