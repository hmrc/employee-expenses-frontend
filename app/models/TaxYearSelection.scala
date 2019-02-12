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

import uk.gov.hmrc.time.TaxYear
import viewmodels.RadioCheckboxOption


sealed trait TaxYearSelection

object TaxYearSelection extends Enumerable.Implicits {

  case object CurrentYear extends WithName("currentYear") with TaxYearSelection
  case object CurrentYearMinus1 extends WithName("currentYearMinus1") with TaxYearSelection
  case object CurrentYearMinus2 extends WithName("currentYearMinus2") with TaxYearSelection
  case object CurrentYearMinus3 extends WithName("currentYearMinus3") with TaxYearSelection
  case object CurrentYearMinus4 extends WithName("currentYearMinus4") with TaxYearSelection

  val values: Seq[TaxYearSelection] = Seq(
    CurrentYear,
    CurrentYearMinus1,
    CurrentYearMinus2,
    CurrentYearMinus3,
    CurrentYearMinus4
  )

  val options: Seq[RadioCheckboxOption] = Seq(
    taxYearRadioCheckboxOption(TaxYear.current, CurrentYear),
    taxYearRadioCheckboxOption(TaxYear.current.back(1), CurrentYearMinus1),
    taxYearRadioCheckboxOption(TaxYear.current.back(2), CurrentYearMinus2),
    taxYearRadioCheckboxOption(TaxYear.current.back(3), CurrentYearMinus3),
    taxYearRadioCheckboxOption(TaxYear.current.back(4), CurrentYearMinus4)
  )

  private def taxYearRadioCheckboxOption(taxYear: TaxYear, option: TaxYearSelection) =
    RadioCheckboxOption(
      keyPrefix = "taxYearSelection",
      option = s"$option",
      messageArgs = Seq(taxYear.startYear.toString.format("YYYY"), taxYear.finishYear.toString.format("YYYY")): _*
    )

  implicit val enumerable: Enumerable[TaxYearSelection] =
    Enumerable(values.map(v => v.toString -> v): _*)

  def getTaxYear(year: TaxYearSelection): Int = year match {
    case CurrentYear       => TaxYear.current.startYear
    case CurrentYearMinus1 => TaxYear.current.back(1).startYear
    case CurrentYearMinus2 => TaxYear.current.back(2).startYear
    case CurrentYearMinus3 => TaxYear.current.back(3).startYear
    case CurrentYearMinus4 => TaxYear.current.back(4).startYear
    case _                 => throw new IllegalArgumentException("Invalid tax year selected")
  }
}
