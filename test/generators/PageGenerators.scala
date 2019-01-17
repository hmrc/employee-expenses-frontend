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

import org.scalacheck.Arbitrary
import pages._
import pages.transport.TypeOfTransportPage

trait PageGenerators {

  implicit lazy val arbitraryAirlineJobListedPage: Arbitrary[AirlineJobListedPage.type] =
    Arbitrary(AirlineJobListedPage)

  implicit lazy val arbitraryTypeOfTransportPage: Arbitrary[TypeOfTransportPage.type] =
    Arbitrary(TypeOfTransportPage)

  implicit lazy val arbitraryConstructionalEngineeringList1Page: Arbitrary[ConstructionalEngineeringList1Page.type] =
    Arbitrary(ConstructionalEngineeringList1Page)

  implicit lazy val arbitraryTypeOfEngineeringPage: Arbitrary[TypeOfEngineeringPage.type] =
    Arbitrary(TypeOfEngineeringPage)

  implicit lazy val arbitraryAmbulanceStaffPage: Arbitrary[AmbulanceStaffPage.type] =
    Arbitrary(AmbulanceStaffPage)

  implicit lazy val arbitraryEmployerContributionPage: Arbitrary[EmployerContributionPage.type] =
    Arbitrary(EmployerContributionPage)

  implicit lazy val arbitraryMultipleEmploymentsPage: Arbitrary[MultipleEmploymentsPage.type] =
    Arbitrary(MultipleEmploymentsPage)

  implicit lazy val arbitraryExpensesEmployerPaidPage: Arbitrary[ExpensesEmployerPaidPage.type] =
    Arbitrary(ExpensesEmployerPaidPage)

  implicit lazy val arbitraryFirstIndustryOptionsPage: Arbitrary[FirstIndustryOptionsPage.type] =
    Arbitrary(FirstIndustryOptionsPage)
}
