/*
 * Copyright 2020 HM Revenue & Customs
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

package utils

import com.github.tototoshi.play2.scalate.Scalate
import com.google.inject.Inject
import play.api.Environment
import play.twirl.api.{Html, HtmlFormat}

import scala.collection.immutable

class MockScalate @Inject() (environment: Environment) extends Scalate(environment) {

  override def render(template: String, params: Map[String, Any]): Html = {

    val titleTag: Html = Html(s"<title>${params("pageTitle")}</title>")

    val parameters: Html = HtmlFormat.fill(
      params.map {
        case (key, value) =>
          Html(s"<div id=$key>$value</div>")
      }.toList
    )

    HtmlFormat.fill(immutable.Seq(titleTag, parameters))
  }
}
