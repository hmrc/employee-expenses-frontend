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

import viewmodels.RadioCheckboxOption

sealed trait FourthIndustryOptions

object FourthIndustryOptions extends Enumerable.Implicits {

  case object Agriculture extends WithName("agriculture") with FourthIndustryOptions
  case object FireService extends WithName("fireService") with FourthIndustryOptions
  case object Heating extends WithName("heating") with FourthIndustryOptions
  case object Leisure extends WithName("leisure") with FourthIndustryOptions
  case object Prisons extends WithName("prisons") with FourthIndustryOptions
  case object ShowMoreIndustries extends WithName("showMoreIndustries") with FourthIndustryOptions

  val values: Seq[FourthIndustryOptions] = Seq(
    Agriculture, FireService, Heating, Leisure, Prisons, ShowMoreIndustries
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("fourthIndustryOptions", value.toString)
  }

  implicit val enumerable: Enumerable[FourthIndustryOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
