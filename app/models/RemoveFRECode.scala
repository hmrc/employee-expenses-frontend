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

sealed trait RemoveFRECode

object RemoveFRECode extends Enumerable.Implicits {

  case object Option1 extends WithName("option1") with RemoveFRECode
  case object Option2 extends WithName("option2") with RemoveFRECode

  val values: Seq[RemoveFRECode] = Seq(
    Option1, Option2
  )

  val options: Seq[RadioCheckboxOption] = values.map {
    value =>
      RadioCheckboxOption("removeFRECode", value.toString)
  }

  implicit val enumerable: Enumerable[RemoveFRECode] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
