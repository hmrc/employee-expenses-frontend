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

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait FirstIndustryOptions

object FirstIndustryOptions extends Enumerable.Implicits {

  case object Healthcare extends WithName("healthcare") with FirstIndustryOptions
  case object FoodAndCatering extends WithName("foodAndCatering") with FirstIndustryOptions
  case object Retail extends WithName("retail") with FirstIndustryOptions
  case object Engineering extends WithName("engineering") with FirstIndustryOptions
  case object TransportAndDistribution extends WithName("transportAndDistribution") with FirstIndustryOptions
  case object NoneOfTheAbove extends WithName("noneOfTheAbove") with FirstIndustryOptions

  val values:Set[FirstIndustryOptions] = Set(
    Healthcare,
    FoodAndCatering,
    Retail,
    Engineering,
    TransportAndDistribution,
    NoneOfTheAbove
  )

  val options:Set[RadioOption] = values.map{
    value =>
      RadioOption("firstIndustryOptions", value.toString)
  }

  implicit val enumerable:Enumerable[FirstIndustryOptions] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
