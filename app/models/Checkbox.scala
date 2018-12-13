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

object Checkbox extends Enumeration {

  type Checkbox = Value

  val Option1: Checkbox.Value = Value("option1")
  val Option2: Checkbox.Value = Value("option2")

  val options: Set[CheckboxOption] = values.map {
    value =>
      CheckboxOption("checkbox", value.toString)
  }

  implicit val format: Format[Checkbox] = new Format[Checkbox] {
    def reads(json: JsValue) = JsSuccess(Checkbox.withName(json.as[String]))
    def writes(checkbox: Checkbox) = JsString(Checkbox.toString)
  }

//  implicit object CheckboxWrites extends Writes[Checkbox.Value] {
//    def writes(checkbox: Checkbox.Value): JsValue = Json.toJson(checkbox.toString)
//  }
//
//  implicit object CheckboxReads extends Reads[Checkbox.Value] {
//    override def reads(json: JsValue): JsResult[Checkbox.Value] = json match {
//      case Option1 => JsSuccess(Option1)
//      case Option2 => JsSuccess(Option2)
//      case _                          => JsError("Unknown checkbox")
//    }
//  }
//
//  implicit def formats: Format[Checkbox.Value] = Format(CheckboxReads, CheckboxWrites)
}
