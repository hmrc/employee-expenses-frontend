/*
 * Copyright 2022 HM Revenue & Customs
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

sealed trait ThirdIndustryOptions

object ThirdIndustryOptions extends Enumerable.Implicits {

  case object BanksBuildingSocieties extends WithName("banksBuildingSocieties") with ThirdIndustryOptions
  case object Electrical extends WithName("electrical") with ThirdIndustryOptions
  case object Leisure extends WithName("leisure") with ThirdIndustryOptions
  case object Prisons extends WithName("prisons") with ThirdIndustryOptions
  case object Security extends WithName("security") with ThirdIndustryOptions
  case object NoneOfAbove extends WithName("none") with ThirdIndustryOptions

  val values: Seq[ThirdIndustryOptions] = Seq(
    BanksBuildingSocieties, Electrical, Leisure, Prisons, Security, NoneOfAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("thirdIndustryOptions", value.toString)
  }

  implicit val enumerable: Enumerable[ThirdIndustryOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
