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
import config.FrontendAppConfig
import models.{Rates, ScottishRate, StandardRate, TaxCodeRecord, UserAnswers}
import pages.ExpensesEmployerPaidPage

import scala.concurrent.ExecutionContext
import scala.math.BigDecimal.RoundingMode

class ClaimAmountService @Inject()(
                                    appConfig: FrontendAppConfig
                                  )(implicit ec: ExecutionContext) {

  def calculateClaimAmount(userAnswers: UserAnswers, claimAmount: Int): Int = {
    userAnswers.get(ExpensesEmployerPaidPage) match {
      case Some(expensesPaid) =>
        claimAmount - expensesPaid
      case None =>
        claimAmount
    }
  }
  
  def calculateTax(percentage: Int, amount: Int): String = {

    val calculatedResult = BigDecimal((amount.toDouble / 100) * percentage).setScale(2, RoundingMode.DOWN)

    if (calculatedResult.isWhole) {
      "%.0f".format(calculatedResult)
    } else {
      calculatedResult.toString
    }
  }

  def standardRate(claimAmount: Int): StandardRate = {
    StandardRate(
      basicRate = appConfig.taxPercentageBand1,
      higherRate = appConfig.taxPercentageBand2,
      calculatedBasicRate = calculateTax(appConfig.taxPercentageBand1, claimAmount),
      calculatedHigherRate = calculateTax(appConfig.taxPercentageBand2, claimAmount)
    )
  }

  def scottishRate(claimAmount: Int): ScottishRate = {
    ScottishRate(
      starterRate = appConfig.taxPercentageScotlandBand1,
      basicRate = appConfig.taxPercentageScotlandBand2,
      higherRate = appConfig.taxPercentageScotlandBand3,
      calculatedStarterRate = calculateTax(appConfig.taxPercentageScotlandBand1, claimAmount),
      calculatedBasicRate = calculateTax(appConfig.taxPercentageScotlandBand2, claimAmount),
      calculatedHigherRate = calculateTax(appConfig.taxPercentageScotlandBand3, claimAmount)
    )
  }

  def getRates(taxCodeRecords: Seq[TaxCodeRecord], claimAmount: Int): Seq[Rates] = {
    taxCodeRecords.headOption match {
      case Some(taxCodeRecord) if taxCodeRecord.taxCode(0).toUpper != 'S' =>
        Seq(standardRate(claimAmount))
      case Some(taxCodeRecord) if taxCodeRecord.taxCode(0).toUpper == 'S' =>
        Seq(scottishRate(claimAmount))
      case _ =>
        Seq(standardRate(claimAmount), scottishRate(claimAmount))
    }
  }
}
