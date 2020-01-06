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

package models

import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec

class TaiTaxYearSpec extends PlaySpec {

  "TaiTaxYear" should {

    "choose the correct year value" when {

      "constructed from a LocalDate that falls before tax year start date" in {
        TaiTaxYear(LocalDate.parse("2017-04-05")).start mustBe LocalDate.parse("2016-04-06")
      }

      "constructed from a LocalDate that falls on the tax year start date" in {
        TaiTaxYear(LocalDate.parse("2017-04-06")).start mustBe LocalDate.parse("2017-04-06")
      }

      "constructed from a LocalDate that falls after the tax year start date" in {
        TaiTaxYear(LocalDate.parse("2017-04-07")).start mustBe LocalDate.parse("2017-04-06")
      }

      "constructed from a year string value of 4 chars (century & year)" in {
        TaiTaxYear("2014").start mustBe LocalDate.parse("2014-04-06")
        TaiTaxYear("1855").start mustBe LocalDate.parse("1855-04-06")
      }

      "constructed from a string value representing a year range (fromCCYY-toCCYY)" in {
        TaiTaxYear("2014-2015").start mustBe LocalDate.parse("2014-04-06")
      }

      "constructed from a string value representing a year range in mixed notation (fromCCYY-toYY)" in {
        TaiTaxYear("2014-15").start mustBe LocalDate.parse("2014-04-06")
      }
    }

    "derive the appropriate century value" when {

      "constructed from a year string value of 2 chars (year only - where year is > 70)" in {
        TaiTaxYear("72").start mustBe LocalDate.parse("1972-04-06")
      }

      "constructed from a year string value of 2 chars (year only - where year is <= 70)" in {
        TaiTaxYear("70").start mustBe LocalDate.parse("2070-04-06")
        TaiTaxYear("14").start mustBe LocalDate.parse("2014-04-06")
      }

      "constructed from a string value representing a year range (fromYY-toYY)" in {
        TaiTaxYear("71-72").start mustBe LocalDate.parse("1971-04-06")
        TaiTaxYear("12-13").start mustBe LocalDate.parse("2012-04-06")
      }

      "constructed from a string value representing a year range in mixed notation (fromYY-toCCYY)" in {
        TaiTaxYear("71-1972").start mustBe LocalDate.parse("1971-04-06")
        TaiTaxYear("12-2013").start mustBe LocalDate.parse("2012-04-06")
      }
    }

    "throw an illegal argument exception" when {

      "constructed from an invalid year string" in {
        val thrown = the[IllegalArgumentException] thrownBy TaiTaxYear("noChance")
        thrown.getMessage mustBe "Cannot parse noChance"
      }

      "constructed from an invalid year range string (years are not contiguous)" in {
        val thrown = the[IllegalArgumentException] thrownBy TaiTaxYear("2014-2017")
        thrown.getMessage mustBe "Cannot parse 2014-2017"
      }

      "constructed from an invalid year range string (mixed notation & years are not contiguous)" in {
        val thrown = intercept[IllegalArgumentException] {
          TaiTaxYear("2010-12")
        }
        thrown.getMessage mustBe "Cannot parse 2010-12"
      }
    }

    val SUT = TaiTaxYear("2017")

    "expoose tax year start and end dates" in {
      SUT.start mustBe LocalDate.parse("2017-04-06")
      SUT.end mustBe LocalDate.parse("2018-04-05")
    }

    "expose next and previous years as new TaiTaxYear instances" in {
      SUT.next.start mustBe LocalDate.parse("2018-04-06")
      SUT.prev.start mustBe LocalDate.parse("2016-04-06")
    }

    "expose previous tax year start and end dates" in {
      SUT.startPrev mustBe LocalDate.parse("2016-04-06")
      SUT.endPrev mustBe LocalDate.parse("2017-04-05")
    }

    "implement comparison behaviour" in {
      SUT.compare(TaiTaxYear("2018")) mustBe -1
      SUT.compare(TaiTaxYear("2017")) mustBe 0
      SUT.compare(TaiTaxYear("2016")) mustBe 1
    }

    "expose a two digit range notation in string form ('<fromYY>-<toYY>')" in {
      SUT.twoDigitRange mustBe "17-18"
    }

    "expose a four digit range notation in string form ('<fromCCYY>-<toCCYY>')" in {
      SUT.fourDigitRange mustBe "2017-2018"
    }

    "thrown an exception when year is an invalid" in {
      val ex = the[IllegalArgumentException] thrownBy TaiTaxYear(17)
      ex.getMessage mustBe "requirement failed: Invalid year"
    }

    "not thrown an exception when year is valid" in{
      TaiTaxYear("17") mustBe TaiTaxYear(2017)
    }
  }
}
