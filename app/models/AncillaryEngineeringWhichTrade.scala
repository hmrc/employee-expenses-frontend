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

sealed trait AncillaryEngineeringWhichTrade

object AncillaryEngineeringWhichTrade extends Enumerable.Implicits {

  case object Patternmaker extends WithName("patternMaker") with AncillaryEngineeringWhichTrade
  case object Labourersupervisororunskilledworker extends WithName("labourerSupervisorOrUnskilledWorker") with AncillaryEngineeringWhichTrade

  val values: Seq[AncillaryEngineeringWhichTrade] = Seq(
    Patternmaker, Labourersupervisororunskilledworker
  )

  val options: Seq[RadioOption] = values.map {
    value =>
      RadioOption("ancillaryEngineeringWhichTrade", value.toString)
  }

  implicit val enumerable: Enumerable[AncillaryEngineeringWhichTrade] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
