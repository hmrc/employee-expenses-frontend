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
    taxYearRadioCheckboxOption(TaxYear.current, CurrentYearMinus1),
    taxYearRadioCheckboxOption(TaxYear.current, CurrentYearMinus2),
    taxYearRadioCheckboxOption(TaxYear.current, CurrentYearMinus3),
    taxYearRadioCheckboxOption(TaxYear.current, CurrentYearMinus4)
  )

  private def taxYearRadioCheckboxOption(taxYear: TaxYear, option: TaxYearSelection) =
    RadioCheckboxOption(
      keyPrefix = "taxYearSelection",
      option = s"$option",
      messageArgs = Seq("taxYearSelection", taxYear.startYear.toString, taxYear.finishYear.toString): _*
    )

  implicit val enumerable: Enumerable[TaxYearSelection] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
