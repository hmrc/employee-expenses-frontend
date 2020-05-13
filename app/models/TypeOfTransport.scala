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

sealed trait TypeOfTransport

object TypeOfTransport extends Enumerable.Implicits {

  case object Airlines extends WithName("airlines") with TypeOfTransport
  case object PublicTransport extends WithName("publicTransport") with TypeOfTransport
  case object Railways extends WithName("railways") with TypeOfTransport
  case object SeamanCarpenter extends WithName("seamanCarpenter") with TypeOfTransport
  case object Vehicles extends WithName("vehicles") with TypeOfTransport
  case object NoneOfTheAbove extends WithName("noneOfTheAbove") with TypeOfTransport


  val values: Seq[TypeOfTransport] = Seq(
    Airlines, PublicTransport, Railways, SeamanCarpenter, Vehicles, NoneOfTheAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("typeOfTransport", value.toString)
  }

  implicit val enumerable: Enumerable[TypeOfTransport] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
