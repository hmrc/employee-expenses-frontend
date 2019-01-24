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
import viewmodels.RadioCheckboxOption

sealed trait EmployerContribution

object EmployerContribution extends Enumerable.Implicits {

  case object All extends WithName("all") with EmployerContribution
  case object Some extends WithName("some") with EmployerContribution
  case object None extends WithName("none") with EmployerContribution


  val values: Seq[EmployerContribution] = Seq(
    All, Some, None
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("employerContribution", value.toString)
  }

  implicit val enumerable: Enumerable[EmployerContribution] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
