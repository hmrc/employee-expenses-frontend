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

sealed trait AncillaryEngineeringWhichTrade

object AncillaryEngineeringWhichTrade extends Enumerable.Implicits {

  case object PatternMaker extends WithName("patternMaker") with AncillaryEngineeringWhichTrade
  case object LabourerSupervisorOrUnskilledWorker extends WithName("labourerSupervisorOrUnskilledWorker") with AncillaryEngineeringWhichTrade
  case object ApprenticeOrStorekeeper extends WithName("apprenticeOrStorekeeper") with AncillaryEngineeringWhichTrade
  case object NoneOfTheAbove extends WithName("noneOfTheAbove") with AncillaryEngineeringWhichTrade

  val values: Seq[AncillaryEngineeringWhichTrade] = Seq(
    ApprenticeOrStorekeeper, LabourerSupervisorOrUnskilledWorker, PatternMaker, NoneOfTheAbove
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("ancillaryEngineeringWhichTrade", value.toString)
  }

  implicit val enumerable: Enumerable[AncillaryEngineeringWhichTrade] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
