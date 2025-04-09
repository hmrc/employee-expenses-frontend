/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json._

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

  def answeredLines(a: Address): Seq[String] =
    Seq(a.line1, a.line2, a.line3, a.line4, a.line5, a.postcode, a.country).flatten

  def asString(a: Address) = s"<p>${answeredLines(a).mkString("<br>")}</p>"

  def asLabel(a: Address): String = s"${answeredLines(a).mkString(", ")}"

  implicit lazy val reads: Reads[Address] =
    (__ \ "address" \ "line1")
      .readNullable[String]
      .and((__ \ "address" \ "line2").readNullable[String])
      .and((__ \ "address" \ "line3").readNullable[String])
      .and((__ \ "address" \ "line4").readNullable[String])
      .and((__ \ "address" \ "line5").readNullable[String])
      .and((__ \ "address" \ "postcode").readNullable[String])
      .and((__ \ "address" \ "country").readNullable[String])(Address.apply _)

  implicit lazy val writes: Writes[Address] =
    (__ \ "address" \ "line1")
      .writeNullable[String]
      .and((__ \ "address" \ "line2").writeNullable[String])
      .and((__ \ "address" \ "line3").writeNullable[String])
      .and((__ \ "address" \ "line4").writeNullable[String])
      .and((__ \ "address" \ "line5").writeNullable[String])
      .and((__ \ "address" \ "postcode").writeNullable[String])
      .and((__ \ "address" \ "country").writeNullable[String])(unlift(Address.unapply))

}
