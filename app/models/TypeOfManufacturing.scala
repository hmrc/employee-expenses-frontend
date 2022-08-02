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

sealed trait TypeOfManufacturing

object TypeOfManufacturing extends Enumerable.Implicits {

  case object Aluminium extends WithName("aluminium") with TypeOfManufacturing
  case object BrassCopper extends WithName("brassCopper") with TypeOfManufacturing
  case object Glass extends WithName("glass") with TypeOfManufacturing
  case object IronSteel extends WithName("ironSteel") with TypeOfManufacturing
  case object PreciousMetals extends WithName("preciousMetals") with TypeOfManufacturing
  case object WoodFurniture extends WithName("woodFurniture") with TypeOfManufacturing
  case object Or extends WithName("or") with TypeOfManufacturing
  case object NoneOfAbove extends WithName("none") with TypeOfManufacturing

  val values: Seq[TypeOfManufacturing] = Seq(
    Aluminium, BrassCopper, Glass, IronSteel, PreciousMetals, WoodFurniture, Or, NoneOfAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("typeOfManufacturing", value.toString)
  }

  implicit val enumerable: Enumerable[TypeOfManufacturing] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
