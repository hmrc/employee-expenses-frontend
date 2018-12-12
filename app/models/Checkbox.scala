/*
 * Copyright 2018 HM Revenue & Customs
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
import viewmodels.CheckboxOption

sealed trait Checkbox

object Checkbox {

  case object Option1 extends WithName("option1") with Checkbox
  case object Option2 extends WithName("option2") with Checkbox

  val values: Set[Checkbox] = Set(
    Option1, Option2
  )

  val options: Set[CheckboxOption] = values.map {
    value =>
      CheckboxOption("checkbox", value.toString)
  }

  implicit val enumerable: Enumerable[Checkbox] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit object CheckboxWrites extends Writes[Checkbox] {
    def writes(checkbox: Checkbox) = Json.toJson(checkbox.toString)
  }

  implicit object CheckboxReads extends Reads[Checkbox] {
    override def reads(json: JsValue): JsResult[Checkbox] = json match {
      case JsString(Option1.toString) => JsSuccess(Option1)
      case JsString(Option2.toString) => JsSuccess(Option2)
      case _                          => JsError("Unknown checkbox")
    }
  }
}
