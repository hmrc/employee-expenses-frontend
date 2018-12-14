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

import viewmodels.CheckboxOption

sealed trait Checkbox

object Checkbox extends Enumerable.Implicits {

  case object Option1 extends WithName("value1") with Checkbox
  case object Option2 extends WithName("value2") with Checkbox
  case object Option3 extends WithName("value3") with Checkbox

  val options: Map[String, WithName with Checkbox] = Map(
    "value1" -> Option1,
    "value2" -> Option2,
    "value3" -> Option3
  )

  val inputs: Set[CheckboxOption] = Set(
    CheckboxOption(Option1.toString, "message1"),
    CheckboxOption(Option2.toString, "message2"),
    CheckboxOption(Option3.toString, "message3")
  )

  implicit lazy val enumerable: Enumerable[Checkbox] =
    Enumerable(options.toList: _*)
}
