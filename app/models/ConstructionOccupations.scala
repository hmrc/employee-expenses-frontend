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

sealed trait ConstructionOccupations

object ConstructionOccupations extends Enumerable.Implicits {

  case object JoinerOrCarpenter extends WithName("joinerOrCarpenter") with ConstructionOccupations
  case object StoneMason extends WithName("stoneMason") with ConstructionOccupations
  case object AsphaltOrCement extends WithName("asphaltOrCement") with ConstructionOccupations
  case object RoofingFelt extends WithName("roofingFelt") with ConstructionOccupations
  case object LabourerOrNavvy extends WithName("labourerOrNavvy") with ConstructionOccupations
  case object Tilemaker extends WithName("tilemaker") with ConstructionOccupations
  case object BuildingMaterials extends WithName("buildingMaterials") with ConstructionOccupations
  case object NoneOfAbove extends WithName("none") with ConstructionOccupations

  val values: Seq[ConstructionOccupations] = Seq(
    JoinerOrCarpenter, StoneMason, AsphaltOrCement, RoofingFelt, LabourerOrNavvy, Tilemaker, BuildingMaterials, NoneOfAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("constructionOccupations", value.toString)
  }

  implicit val enumerable: Enumerable[ConstructionOccupations] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
