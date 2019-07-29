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

import play.api.mvc.{JavascriptLiteral, PathBindable}

sealed trait ExperimentalVariant

object ExperimentalVariant {
  case object Standard extends ExperimentalVariant
  case object IndustryTypesVariant extends ExperimentalVariant

  implicit val pathBindable : PathBindable[ExperimentalVariant] =  new PathBindable[ExperimentalVariant] {
    override def bind(key: String, value: String): Either[String, ExperimentalVariant] = {
      if (value == "IndustryTypesVariant") Right(IndustryTypesVariant)
      else Right(Standard)
    }

    override def unbind(key: String, value: ExperimentalVariant): String = {value match {
      case Standard => "standard"
      case IndustryTypesVariant => "IndustryTypesVariant"
    }}
  }

  implicit val jsLiteral: JavascriptLiteral[ExperimentalVariant] = new JavascriptLiteral[ExperimentalVariant] {
    override def to(value: ExperimentalVariant): String = value match {
      case Standard => "standard"
      case IndustryTypesVariant => "IndustryTypesVariant"
    }
  }
}
