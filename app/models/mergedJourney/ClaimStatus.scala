/*
 * Copyright 2024 HM Revenue & Customs
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

package models.mergedJourney

import play.api.libs.json.{Format, JsString, Reads, Writes}
import play.api.mvc.QueryStringBindable

sealed trait ClaimStatus {
  val key: String = toString
}

abstract class ClaimComplete extends ClaimStatus
case object ClaimCompleteCurrent extends ClaimComplete
case object ClaimCompletePrevious extends ClaimComplete
case object ClaimCompleteCurrentPrevious extends ClaimComplete

case object ClaimSkipped extends ClaimStatus
case object ClaimPending extends ClaimStatus
case object ClaimStopped extends ClaimStatus
case object ClaimNotChanged extends ClaimStatus
case object ClaimUnsuccessful extends ClaimStatus

object ClaimStatus {
  private val mapping: String => ClaimStatus = {
    case ClaimCompleteCurrent.key => ClaimCompleteCurrent
    case ClaimCompletePrevious.key => ClaimCompletePrevious
    case ClaimCompleteCurrentPrevious.key => ClaimCompleteCurrentPrevious
    case ClaimSkipped.key => ClaimSkipped
    case ClaimPending.key => ClaimPending
    case ClaimStopped.key => ClaimStopped
    case ClaimNotChanged.key => ClaimNotChanged
    case ClaimUnsuccessful.key => ClaimUnsuccessful
  }

  val reads: Reads[ClaimStatus] = Reads { json => json.validate[String].map(mapping) }
  val writes: Writes[ClaimStatus] = Writes { state => JsString(state.toString) }
  implicit val format: Format[ClaimStatus] = Format(reads, writes)

  implicit def urlBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[ClaimStatus] = new QueryStringBindable[ClaimStatus] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ClaimStatus]] =
      stringBinder.bind("status", params) map {
        case Right(status) => try {
          Right(mapping(status))
        } catch {
          case _: Throwable => Left(s"Unknown value '$status' for ClaimStatus")
        }
        case _ => Left(s"Unable to bind a ClaimStatus")
      }

    override def unbind(key: String, value: ClaimStatus): String =
      stringBinder.unbind("status", value.key)
  }
}