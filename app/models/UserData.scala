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

import java.time.LocalDateTime

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class UserData(
                     id: String,
                     data: JsObject,
                     lastUpdated: LocalDateTime = LocalDateTime.now
                   ) {

  def getEntry[T](key: String)(implicit reads: Reads[T]): Option[T] = {
    (data \ key).validate[T].asOpt
  }
}

object UserData {

  implicit lazy val reads: Reads[UserData] = {

    (
      (__ \ "_id").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "lastUpdated").read[LocalDateTime]
    ) (UserData.apply _)
  }

  implicit lazy val writes: Writes[UserData] = {

    (
      (__ \ "_id").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "lastUpdated").write[LocalDateTime]
    ) (unlift(UserData.unapply))
  }
}
