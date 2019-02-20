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

sealed trait FirstIndustryOptions

object FirstIndustryOptions extends Enumerable.Implicits {

  case object Healthcare extends WithName("healthcare") with FirstIndustryOptions
  case object FoodAndCatering extends WithName("foodAndCatering") with FirstIndustryOptions
  case object Retail extends WithName("retail") with FirstIndustryOptions
  case object Engineering extends WithName("engineering") with FirstIndustryOptions
  case object TransportAndDistribution extends WithName("transportAndDistribution") with FirstIndustryOptions
  case object NoneOfAbove extends WithName("none") with FirstIndustryOptions

  val values: Seq[FirstIndustryOptions] = Seq(
    Healthcare,
    FoodAndCatering,
    Retail,
    Engineering,
    TransportAndDistribution,
    NoneOfAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map{
    value =>
      RadioCheckboxOption("firstIndustryOptions", value.toString)
  }

  implicit val enumerable:Enumerable[FirstIndustryOptions] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
