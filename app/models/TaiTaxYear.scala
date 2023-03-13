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

import org.joda.time.LocalDate
import play.api.libs.json.{Format, Json}

import scala.util.matching.Regex

object TaxYearDates {
  val startDay = 6
  val startMonth = 4
  val endDay = 5
  val endMonth = 4
}

case class TaiTaxYear(year: Int) extends Ordered[TaiTaxYear] {
  import TaxYearDates._

  require(year.toString.length == 4, "Invalid year")

  def start: LocalDate = new LocalDate(year, startMonth, startDay)
  def end: LocalDate = new LocalDate(year + 1, endMonth , endDay)
  def next = TaiTaxYear(year + 1)
  def prev = TaiTaxYear(year - 1)
  def startPrev: LocalDate = new LocalDate(prev.year, endMonth, startDay)
  def endPrev: LocalDate = new LocalDate(prev.year + 1, startMonth, endDay)
  def compare(that: TaiTaxYear): Int = this.year compare that.year
  def twoDigitRange = s"${start.year.get % 100}-${end.year.get % 100}"
  def fourDigitRange = s"${start.year.get}-${end.year.get}"
}

object TaiTaxYear {

  implicit val format: Format[TaiTaxYear] = Json.format[TaiTaxYear]

  def apply(from: LocalDate = new LocalDate): TaiTaxYear = {
    val naiveYear = TaiTaxYear(from.year.get)
    if (from.isBefore(naiveYear.start)) {
      naiveYear.prev
    }
    else {
      naiveYear
    }
  }

  def apply(from: String): TaiTaxYear = {
    val YearRange = "([0-9]+)-([0-9]+)".r

    object Year {
      val SimpleYear: Regex = "([12][0-9])?([0-9]{2})".r
      def unapply(in: String): Option[Int] = in match {
        case SimpleYear(cenStr, yearStr) =>
          val year = yearStr.toInt
          val century = Option(cenStr).filter(_.nonEmpty) match {
            case None if year > 70 => 1900
            case None => 2000
            case Some(x) => x.toInt * 100
          }
          Some(century + year)
        case _ => None
      }
    }

    from match {
      case Year(year) => TaiTaxYear(year)
      case YearRange(Year(fYear),Year(tYear)) if tYear == fYear + 1 =>
        TaiTaxYear(fYear)
      case x => throw new IllegalArgumentException(s"Cannot parse $x")
    }
  }
}
