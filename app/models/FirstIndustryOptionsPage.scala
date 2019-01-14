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

import viewmodels.RadioOption

sealed trait FirstIndustryOptionsPage

object FirstIndustryOptionsPage extends Enumerable.Implicits {

  case object Healthcare extends WithName("healthcare") with FirstIndustryOptionsPage
  case object FoodAndCatering extends WithName("foodAndCatering") with FirstIndustryOptionsPage
  case object Retail extends WithName("retail") with FirstIndustryOptionsPage
  case object Engineering extends WithName("engineering") with FirstIndustryOptionsPage
  case object TransportAndDistribution extends WithName("transportAndDistribution") with FirstIndustryOptionsPage
  case object NoneOfTheAbove extends WithName("noneOfTheAbove") with FirstIndustryOptionsPage

  val values:Set[FirstIndustryOptionsPage] = Set(
    Healthcare,
    FoodAndCatering,
    Retail,
    Engineering,
    TransportAndDistribution,
    NoneOfTheAbove
  )

  val options:Set[RadioOption] = values.map{
    value =>
      RadioOption("firstIndustryOptionsPage", value.toString)
  }

  implicit val enumerable:Enumerable[FirstIndustryOptionsPage] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
