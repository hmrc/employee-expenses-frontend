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

import base.SpecBase
import play.api.libs.json._

class UserDataSpec extends SpecBase {
  "UserData" should {
    "getEntry" should {
      "get correct value" in {
        val userData: UserData = UserData(
          id = "1234",
          data = JsObject(
            Seq(
              "some" -> JsString("data"),
              "someMore" -> JsString("moreData")
            )
          ),
          lastUpdated = LocalDateTime.parse("2018-11-27T13:00:41")
        )

        userData.getEntry[String]("some") mustBe Some("data")
        userData.getEntry[String]("someMore") mustBe Some("moreData")
      }
    }

    "read correctly" in {
      val userDataJson: JsValue = Json.parse(
        """
          | {
          |   "_id": "1234",
          |   "data": {
          |     "some": "data",
          |     "someMore": "data"
          |     },
          |   "lastUpdated": "2018-11-27T13:00:41"
          | }
        """.stripMargin
      )

      val userData: UserData = userDataJson.as[UserData]

      userData.id mustBe "1234"
      userData.data mustBe a[JsObject]
      userData.lastUpdated mustBe a[LocalDateTime]
    }

    "write correctly" in {
      val userData: UserData = UserData(
        id = "1234",
        data = JsObject(
          Seq(
            "some" -> JsString("data"),
            "someMore" -> JsString("data")
          )
        ),
        lastUpdated = LocalDateTime.parse("2018-11-27T13:00:41")
      )

      val userDataJson: JsValue = Json.toJson(userData)

      userDataJson mustBe Json.obj(
        "_id" -> "1234",
        "data" -> JsObject(
          Seq(
            "some" -> JsString("data"),
            "someMore" -> JsString("data")
          )
        ),
        "lastUpdated" -> LocalDateTime.parse("2018-11-27T13:00:41")
      )
    }
  }
}
