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

import java.time.{LocalDate, LocalDateTime}

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.Json

class MongoDateTimeFormatsSpec extends AnyFreeSpec with Matchers with OptionValues with MongoDateTimeFormats {

  "a LocalDateTime" - {

    val date = LocalDate.of(2018, 2, 1).atStartOfDay

    val dateMillis = 1517443200000L

    val json = Json.obj(
      "$" + "date" -> dateMillis
    )

    "must serialise to json" in {
      val result = Json.toJson(date)
      result mustEqual json
    }

    "must deserialise from json" in {
      val result = json.as[LocalDateTime]
      result mustEqual date
    }

    "must serialise/deserialise to the same value" in {
      val result = Json.toJson(date).as[LocalDateTime]
      result mustEqual date
    }
  }

}
