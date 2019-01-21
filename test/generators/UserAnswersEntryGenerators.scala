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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.transport.{AirlineJobListPage, TypeOfTransportPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryAirlineJobListUserAnswersEntry: Arbitrary[(AirlineJobListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AirlineJobListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTypeOfTransportUserAnswersEntry: Arbitrary[(TypeOfTransportPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TypeOfTransportPage.type]
        value <- arbitrary[TypeOfTransport].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConstructionalEngineeringList1UserAnswersEntry: Arbitrary[(ConstructionalEngineeringList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTypeOfEngineeringUserAnswersEntry: Arbitrary[(TypeOfEngineeringPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TypeOfEngineeringPage.type]
        value <- arbitrary[TypeOfEngineering].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAmbulanceStaffUserAnswersEntry: Arbitrary[(AmbulanceStaffPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AmbulanceStaffPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmployerContributionUserAnswersEntry: Arbitrary[(EmployerContributionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmployerContributionPage.type]
        value <- arbitrary[EmployerContribution].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMultipleEmploymentsUserAnswersEntry: Arbitrary[(MultipleEmploymentsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MultipleEmploymentsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryExpensesEmployerPaidUserAnswersEntry: Arbitrary[(ExpensesEmployerPaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ExpensesEmployerPaidPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFirstIndustryOptionsUserAnswersEntry: Arbitrary[(FirstIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FirstIndustryOptionsPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }
}
