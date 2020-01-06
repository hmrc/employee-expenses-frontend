/*
 * Copyright 2020 HM Revenue & Customs
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

sealed trait TypeOfEngineering

object TypeOfEngineering extends Enumerable.Implicits {

  case object ConstructionalEngineering extends WithName("constructionalEngineering") with TypeOfEngineering
  case object TradesRelatingToEngineering extends WithName("tradesRelatingToEngineering") with TypeOfEngineering
  case object FactoryOrWorkshopEngineering extends WithName("factoryOrWorkshopEngineering") with TypeOfEngineering
  case object NoneOfTheAbove extends WithName("noneOfTheAbove") with TypeOfEngineering

  val values: Seq[TypeOfEngineering] = Seq(
    ConstructionalEngineering, FactoryOrWorkshopEngineering, TradesRelatingToEngineering, NoneOfTheAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("typeOfEngineering", value.toString)
  }

  implicit val enumerable: Enumerable[TypeOfEngineering] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
