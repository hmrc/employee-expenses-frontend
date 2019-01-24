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

sealed trait TransportVehicleTrade

object TransportVehicleTrade extends Enumerable.Implicits {

  case object Builder extends WithName("builder") with TransportVehicleTrade
  case object Vehiclerepairerwagonlifter extends WithName("vehicleRepairerWagonLifter") with TransportVehicleTrade
  case object RailwayVehiclePainter extends WithName("railwayVehiclePainter") with TransportVehicleTrade
  case object Letterer extends WithName("letterer") with TransportVehicleTrade
  case object BuildersAssistantOrRepairersAssistant extends WithName("buildersAssistantOrRepairersAssistant") with TransportVehicleTrade
  case object NoneOfTheAbove extends WithName("noneOfTheAbove") with TransportVehicleTrade



  val values: Seq[TransportVehicleTrade] = Seq(
    Builder, Vehiclerepairerwagonlifter,RailwayVehiclePainter, Letterer, BuildersAssistantOrRepairersAssistant, NoneOfTheAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("transportVehicleTrade", value.toString)
  }

  implicit val enumerable: Enumerable[TransportVehicleTrade] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
