/*
 * Copyright 2023 HM Revenue & Customs
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
import models.TaxCodeStatus.Live
import models.{Rates, ScottishRate, StandardRate, TaxCodeRecord, UserAnswers}
import pages.ExpensesEmployerPaidPage

import scala.math.BigDecimal.RoundingMode

class ClaimAmountService @Inject() (
    appConfig: FrontendAppConfig
) {

  def calculateClaimAmount(userAnswers: UserAnswers, claimAmount: Int): Int =
    userAnswers.get(ExpensesEmployerPaidPage) match {
      case Some(expensesPaid) =>
        claimAmount - expensesPaid
      case None =>
        claimAmount
    }

  def calculateTax(percentage: Int, amount: Int): String = {

    val calculatedResult = BigDecimal((amount.toDouble / 100) * percentage).setScale(2, RoundingMode.UP)

    if (calculatedResult.isWhole) {
      "%.0f".format(calculatedResult)
    } else {
      calculatedResult.toString
    }
  }

  def standardRate(claimAmount: Int): StandardRate =
    StandardRate(
      basicRate = appConfig.taxPercentageBasicRate,
      higherRate = appConfig.taxPercentageHigherRate,
      calculatedBasicRate = calculateTax(appConfig.taxPercentageBasicRate, claimAmount),
      calculatedHigherRate = calculateTax(appConfig.taxPercentageHigherRate, claimAmount)
    )

  def scottishRate(claimAmount: Int): ScottishRate =
    ScottishRate(
      starterRate = appConfig.taxPercentageScotlandStarterRate,
      basicRate = appConfig.taxPercentageScotlandBasicRate,
      intermediateRate = appConfig.taxPercentageScotlandIntermediateRate,
      higherRate = appConfig.taxPercentageScotlandHigherRate,
      advancedRate = appConfig.taxPercentageScotlandAdvancedRate,
      topRate = appConfig.taxPercentageScotlandTopRate,
      calculatedStarterRate = calculateTax(appConfig.taxPercentageScotlandStarterRate, claimAmount),
      calculatedBasicRate = calculateTax(appConfig.taxPercentageScotlandBasicRate, claimAmount),
      calculatedIntermediateRate = calculateTax(appConfig.taxPercentageScotlandIntermediateRate, claimAmount),
      calculatedHigherRate = calculateTax(appConfig.taxPercentageScotlandHigherRate, claimAmount),
      calculatedAdvancedRate = calculateTax(appConfig.taxPercentageScotlandAdvancedRate, claimAmount),
      calculatedTopRate = calculateTax(appConfig.taxPercentageScotlandTopRate, claimAmount)
    )

  def getRates(taxCodeRecords: Seq[TaxCodeRecord], claimAmount: Int): Seq[Rates] = {

    val liveRecords = filterRecords(taxCodeRecords)

    liveRecords match {
      case Some(taxCodeRecord) if taxCodeRecord.taxCode(0).toUpper != 'S' =>
        Seq(standardRate(claimAmount))
      case Some(taxCodeRecord) if taxCodeRecord.taxCode(0).toUpper == 'S' =>
        Seq(scottishRate(claimAmount))
      case _ =>
        Seq(standardRate(claimAmount), scottishRate(claimAmount))
    }
  }

  def filterRecords(taxCodeRecord: Seq[TaxCodeRecord]): Option[TaxCodeRecord] =
    taxCodeRecord.find(_.status == Live) match {
      case Some(liveTaxCodeRecord) => Some(liveTaxCodeRecord)
      case None                    => taxCodeRecord.headOption
    }

}
