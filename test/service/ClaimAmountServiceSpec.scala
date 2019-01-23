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
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import pages.{ClaimAmount, ExpensesEmployerPaidPage}

import scala.concurrent.ExecutionContext.Implicits.global

class ClaimAmountServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {


  private val claimAmountService = new ClaimAmountService()



  "ClaimAmountService" must {
    "deduction" when {
      "return correct Int when given two claimAmount and deduction" in {
        val claimAmount = 180
        val deduction = 100

        claimAmountService.deduction(claimAmount, deduction) mustBe 80
      }
    }

    "percentage" when {
      "return correct amount as string when given amount and percentage" in {
        val amount: Double = 180
        val percentage: Double = 50

        claimAmountService.percentage(amount, percentage) mustBe "90.00"
      }
    }

    "actualClaimAmount" when {
      "return a deduction amount when claimAmount and expensesEmployerPaid is defined" in {
        val userAnswers = emptyUserAnswers.set(ClaimAmount, 180).success.value
        val newUserAnswers = userAnswers.set(ExpensesEmployerPaidPage, 100).success.value

        claimAmountService.actualClaimAmount(newUserAnswers) mustBe 80
      }

      "return claim amount when expensesEmployerPaid is not defined" in {
        val userAnswers = emptyUserAnswers.set(ClaimAmount, 180).success.value

        claimAmountService.actualClaimAmount(userAnswers) mustBe 180
      }
    }

    "band1" when {
      "return 20% of claim amount as a string" in {
        val userAnswers = emptyUserAnswers.set(ClaimAmount, 180).success.value

        claimAmountService.band1(userAnswers) mustBe "36.00"
      }
    }

    "band2" when {
      "return 40% of claim amount as a string" in {
        val userAnswers = emptyUserAnswers.set(ClaimAmount, 180).success.value

        claimAmountService.band1(userAnswers) mustBe "36.00"
      }
    }
  }

}
