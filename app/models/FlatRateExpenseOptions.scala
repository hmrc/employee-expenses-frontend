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

package models

sealed trait FlatRateExpenseOptions

object FlatRateExpenseOptions extends Enumerable.Implicits {
  case object FRENoYears extends WithName("freNoYears") with FlatRateExpenseOptions
  case object FREAllYearsAllAmountsSameAsClaimAmount extends WithName("freAllYearsAllAmountsSameAsClaimAmount") with FlatRateExpenseOptions
  case object FREAllYearsAllAmountsDifferentToClaimAmount extends WithName("freAllYearsAllAmountsDifferentToClaimAmount") with FlatRateExpenseOptions
  case object ComplexClaim extends WithName("complexClaim") with FlatRateExpenseOptions
  case object TechnicalDifficulties extends WithName("technicalDifficulties") with FlatRateExpenseOptions

  val values: Seq[FlatRateExpenseOptions] = Seq(
    FRENoYears,
    FREAllYearsAllAmountsSameAsClaimAmount,
    FREAllYearsAllAmountsDifferentToClaimAmount,
    ComplexClaim,
    TechnicalDifficulties
  )

  implicit val enumerable: Enumerable[FlatRateExpenseOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
