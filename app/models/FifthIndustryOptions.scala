/*
 * Copyright 2021 HM Revenue & Customs
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

sealed trait FifthIndustryOptions

object FifthIndustryOptions extends Enumerable.Implicits {

  case object Armedforces extends WithName("armedForces") with FifthIndustryOptions
  case object Dockswaterways extends WithName("docksWaterways") with FifthIndustryOptions
  case object Forestry extends WithName("forestry") with FifthIndustryOptions
  case object Shipyard extends WithName("shipyard") with FifthIndustryOptions
  case object Textiles extends WithName("textiles") with FifthIndustryOptions
  case object NoneOfAbove extends WithName("none") with FifthIndustryOptions

  val values: Seq[FifthIndustryOptions] = Seq(
    Armedforces,
    Dockswaterways,
    Forestry,
    Shipyard,
    Textiles,
    NoneOfAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("fifthIndustryOptions", value.toString)
  }

  implicit val enumerable: Enumerable[FifthIndustryOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
