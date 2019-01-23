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
import models.UserAnswers
import pages.{ClaimAmount, ExpensesEmployerPaidPage}

import scala.concurrent.ExecutionContext
import scala.math.BigDecimal.RoundingMode

class ClaimAmountService @Inject()()(implicit ec: ExecutionContext) {

  def deduction(claimAmount: Int, expensesEmployerPaid: Int): Int =
    claimAmount - expensesEmployerPaid

  def percentage(amount: Double, percentage: Double): String =
    BigDecimal((amount / 100) * percentage).setScale(2, RoundingMode.DOWN).toString

  def actualClaimAmount(userAnswers: UserAnswers): Int = {

    val claimAmount: Option[Int] = userAnswers.get(ClaimAmount)

    val expensesEmployerPaid: Option[Int] = userAnswers.get(ExpensesEmployerPaidPage)

    if (claimAmount.isDefined && expensesEmployerPaid.isDefined) {
      deduction(claimAmount.get, expensesEmployerPaid.get)
    } else {
      claimAmount.get
    }
  }

  def band1(userAnswers: UserAnswers): String =
    percentage(actualClaimAmount(userAnswers), 20)

  def band2(userAnswers: UserAnswers): String =
    percentage(actualClaimAmount(userAnswers), 40)
}
