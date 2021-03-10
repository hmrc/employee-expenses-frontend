/*
 * Copyright 2021 HM Revenue & Customs
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

package viewmodels

import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}

case class Message(key: String, args: Any*) {

  def html(implicit messages: Messages): HtmlFormat.Appendable =
    Html(string)

  def string(implicit messages: Messages): String =
    messages(key, args: _*)
}

case class RadioCheckboxOption(id: String, value: String, message: Message)

object RadioCheckboxOption {
  def apply(keyPrefix: String, option: String, messageArgs: Any*): RadioCheckboxOption =
    RadioCheckboxOption(
      id = s"$keyPrefix.$option",
      value = option,
      message = Message(s"$keyPrefix.$option", messageArgs: _*)
    )
}
