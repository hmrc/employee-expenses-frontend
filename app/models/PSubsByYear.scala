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
import models.TaxYearSelection.getTaxYearPeriod

final case class PSubsByYear(subscriptions: Map[Int, Seq[PSub]])

object PSubsByYear {

  def orderTaxYears(PSubsByYear: Map[Int, Seq[PSub]]): Seq[TaxYearSelection] = {
    PSubsByYear.map {
      psubsByYear =>
        getTaxYearPeriod(psubsByYear._1)
    }.toSeq.sortWith(_.toString < _.toString)
  }

  implicit lazy val formats: Format[Map[Int, Seq[PSub]]] = {
    new Format[Map[Int, Seq[PSub]]] {

      def writes(m: Map[Int, Seq[PSub]]): JsValue = {
        Json.toJson(m.map {
          case (key, value) => key.toString -> value
        })
      }

      def reads(json: JsValue): JsResult[Map[Int, Seq[PSub]]] = {
        json.validate[Map[String, Seq[JsValue]]].map (psubsByYear =>
          psubsByYear.map{ psubsForYear =>
            (
              psubsForYear._1.toInt,
              psubsForYear._2.map(_.validate[PSub]).collect {case JsSuccess(validPsub, _) => validPsub}
            )
          }
        )
      }
    }
  }

  implicit lazy val reads: Reads[PSubsByYear] = Json.reads[PSubsByYear]

  implicit lazy val writes: Writes[PSubsByYear] = Json.writes[PSubsByYear]
}
