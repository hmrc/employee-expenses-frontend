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

sealed trait SecondIndustryOptions

object SecondIndustryOptions extends Enumerable.Implicits {

  case object ManufacturingWarehousing extends WithName("manufacturingWarehousing") with SecondIndustryOptions
  case object Council extends WithName("council") with SecondIndustryOptions
  case object Police extends WithName("police") with SecondIndustryOptions
  case object ClothingTextiles extends WithName("clothingTextiles") with SecondIndustryOptions
  case object Construction extends WithName("construction") with SecondIndustryOptions
  case object NoneOfAbove extends WithName("none") with SecondIndustryOptions

  val values: Seq[SecondIndustryOptions] = Seq(
    ClothingTextiles, Construction, Council, ManufacturingWarehousing, Police, NoneOfAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("secondIndustryOptions", value.toString)
  }

  implicit val enumerable: Enumerable[SecondIndustryOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
