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

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

case class Address(
                    line1: Option[String],
                    line2: Option[String],
                    line3: Option[String],
                    line4: Option[String],
                    line5: Option[String],
                    postcode: Option[String],
                    country: Option[String]
                  )

object Address {
  implicit lazy val reads: Reads[Address] = (
    (JsPath \ "address" \ "line1").readNullable[String] and
    (JsPath \ "address" \ "line2").readNullable[String] and
    (JsPath \ "address" \ "line3").readNullable[String] and
    (JsPath \ "address" \ "line4").readNullable[String] and
    (JsPath \ "address" \ "line5").readNullable[String] and
    (JsPath \ "address" \ "postcode").readNullable[String] and
    (JsPath \ "address" \ "country").readNullable[String]
  )(Address.apply _)

  implicit lazy val writes: Writes[Address] = (
    (JsPath \ "address" \ "line1").writeNullable[String] and
    (JsPath \ "address" \ "line2").writeNullable[String] and
    (JsPath \ "address" \ "line3").writeNullable[String] and
    (JsPath \ "address" \ "line4").writeNullable[String] and
    (JsPath \ "address" \ "line5").writeNullable[String] and
    (JsPath \ "address" \ "postcode").writeNullable[String] and
    (JsPath \ "address" \ "country").writeNullable[String]
  )(unlift(Address.unapply))
}
