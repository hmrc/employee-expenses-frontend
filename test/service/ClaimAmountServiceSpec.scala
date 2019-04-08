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

import base.SpecBase
import models.{Rates, ScottishRate, StandardRate, TaxCodeRecord}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.ExpensesEmployerPaidPage

import scala.concurrent.ExecutionContext.Implicits.global

class ClaimAmountServiceSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  private val claimAmountService = new ClaimAmountService(frontendAppConfig)

  "ClaimAmountService" must {


    "calculateTax" when {
      "return a string without decimals when a whole number" in {

        claimAmountService.calculateTax(10, 100) mustBe "10"
        claimAmountService.calculateTax(20, 250) mustBe "50"
      }

      "return a string with decimals when not a whole number" in {

        claimAmountService.calculateTax(15, 10) mustBe "1.50"
        claimAmountService.calculateTax(5, 125) mustBe "6.25"
      }
    }


    "band1" when {
      "return 20% of claim amount as a string with contribution from employer" in {
        val userAnswers = emptyUserAnswers.set(ExpensesEmployerPaidPage, 50).success.value
        val actualClaimAmount = claimAmountService.calculateClaimAmount(userAnswers, 100)

        actualClaimAmount mustBe 50

        claimAmountService.calculateTax(
          percentage = frontendAppConfig.taxPercentageBand1,
          amount = actualClaimAmount
        ) mustBe "10"
      }

      "return 20% of claim amount as a string" in {
        val userAnswers = emptyUserAnswers
        val actualClaimAmount = claimAmountService.calculateClaimAmount(userAnswers, 100)

        actualClaimAmount mustBe 100

        claimAmountService.calculateTax(
          percentage = frontendAppConfig.taxPercentageBand1,
          amount = actualClaimAmount
        ) mustBe "20"
      }
    }

    "band2" when {
      "return 40% of claim amount as a string" in {
        claimAmountService.calculateTax(
          percentage = frontendAppConfig.taxPercentageBand2,
          amount = 180
        ) mustBe "72"
      }
    }

    "getRates" when {
      "english tax code record must return english rates" in {
        val claimAmount = 100
        val rates = claimAmountService.getRates(Seq(TaxCodeRecord("850L")), claimAmount)

        rates mustBe Seq(StandardRate(
          basicRate = frontendAppConfig.taxPercentageBand1,
          higherRate = frontendAppConfig.taxPercentageBand2,
          calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
          calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount)
        ))
      }

      "scottish tax code record must return scottish rates" in {
        val claimAmount = 100
        val rates = claimAmountService.getRates(Seq(TaxCodeRecord("S850L")), claimAmount)

        rates mustBe Seq(ScottishRate(
          starterRate = frontendAppConfig.taxPercentageScotlandBand1,
          basicRate = frontendAppConfig.taxPercentageScotlandBand2,
          higherRate = frontendAppConfig.taxPercentageScotlandBand3,
          calculatedStarterRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand1, claimAmount),
          calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand2, claimAmount),
          calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand3, claimAmount)
        ))
      }

      "no tax code record must return both english and scottish rates" in {
        val claimAmount = 100
        val rates = claimAmountService.getRates(Seq(), claimAmount)

        rates mustBe Seq(
          StandardRate(
            basicRate = frontendAppConfig.taxPercentageBand1,
            higherRate = frontendAppConfig.taxPercentageBand2,
            calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand1, claimAmount),
            calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageBand2, claimAmount)
          ),
          ScottishRate(
            starterRate = frontendAppConfig.taxPercentageScotlandBand1,
            basicRate = frontendAppConfig.taxPercentageScotlandBand2,
            higherRate = frontendAppConfig.taxPercentageScotlandBand3,
            calculatedStarterRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand1, claimAmount),
            calculatedBasicRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand2, claimAmount),
            calculatedHigherRate = claimAmountService.calculateTax(frontendAppConfig.taxPercentageScotlandBand3, claimAmount)
          ))
      }
    }
  }
}
