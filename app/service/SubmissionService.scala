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
import models.{TaiTaxYear, TaxYearSelection}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class SubmissionService @Inject()(taiService: TaiService) {

  def submitFRENotInCode(nino: String, taxYears: Seq[TaxYearSelection], claimAmount: Int)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {

    val responses: Future[Seq[HttpResponse]] =
      futureSequence(taxYears) {
        taxYearSelection =>
          val taiTaxYear = TaiTaxYear(TaxYearSelection.getTaxYear(taxYearSelection))
          taiService.updateFRE(nino, taiTaxYear, claimAmount)
      }

    submissionResult(responses)

  }

  def submitRemoveFREFromCode(nino: String, taxYears: Seq[TaxYearSelection], removeYear: TaxYearSelection)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {

    val removeTaxYears = taxYears.take(TaxYearSelection.values.indexOf(removeYear) + 1)

    val responses: Future[Seq[HttpResponse]] =
      futureSequence(removeTaxYears) {
        taxYearSelection =>
          val taiTaxYear = TaiTaxYear(TaxYearSelection.getTaxYear(taxYearSelection))
          taiService.updateFRE(nino, taiTaxYear, 0)
      }

    submissionResult(responses)

  }

  def submitChangeFREFromCode(nino: String, taxYears: Seq[TaxYearSelection], claimAmount: Int, changeYears: Seq[TaxYearSelection])
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {

    val responses: Future[Seq[HttpResponse]] =
      futureSequence(changeYears) {
        taxYearSelection =>
          val taiTaxYear = TaiTaxYear(TaxYearSelection.getTaxYear(taxYearSelection))
          taiService.updateFRE(nino, taiTaxYear, claimAmount)
      }

    submissionResult(responses)

  }

  private def futureSequence[I, O](inputs: Seq[I])(flatMapFunction: I => Future[O])
                                  (implicit ec: ExecutionContext): Future[Seq[O]] =
    inputs.foldLeft(Future.successful(Seq.empty[O]))(
      (previousFutureResult, nextInput) =>
        for {
          futureSeq <- previousFutureResult
          future <- flatMapFunction(nextInput)
        } yield futureSeq :+ future
    )

  def submissionResult(response: Future[Seq[HttpResponse]])
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    response.map {
      responses =>
        responses.nonEmpty && responses.forall(_.status == 204)
    }
  }
}
