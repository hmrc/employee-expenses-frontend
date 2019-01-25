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
import pages.healthcare._
import pages.engineering._
import pages.manufacturing.{AluminiumApprenticePage, TypeOfManufacturingPage}
import pages.police.SpecialConstablePage
import pages.manufacturing._
import pages.police._
import pages.transport._

trait PageGenerators {

  implicit lazy val arbitraryPoliceOccupationListPage: Arbitrary[PoliceOccupationListPage.type] =
    Arbitrary(PoliceOccupationListPage)

  implicit lazy val arbitraryAluminiumApprenticePage: Arbitrary[AluminiumApprenticePage.type] =
    Arbitrary(AluminiumApprenticePage)

  implicit lazy val arbitrarySpecialConstablePage: Arbitrary[SpecialConstablePage.type] =
    Arbitrary(SpecialConstablePage)

  implicit lazy val arbitraryAluminiumOccupationList1Page: Arbitrary[AluminiumOccupationList1Page.type] =
    Arbitrary(AluminiumOccupationList1Page)

  implicit lazy val arbitraryTypeOfManufacturingPage: Arbitrary[TypeOfManufacturingPage.type] =
    Arbitrary(TypeOfManufacturingPage)

  implicit lazy val arbitraryTransportVehicleTradePage: Arbitrary[TransportVehicleTradePage.type] =
    Arbitrary(TransportVehicleTradePage)

  implicit lazy val arbitraryFactoryEngineeringApprenticePage: Arbitrary[FactoryEngineeringApprenticePage.type] =
    Arbitrary(FactoryEngineeringApprenticePage)

  implicit lazy val arbitraryTransportCarpenterPage: Arbitrary[TransportCarpenterPage.type] =
    Arbitrary(TransportCarpenterPage)

  implicit lazy val arbitrarySecondIndustryOptionsPage: Arbitrary[SecondIndustryOptionsPage.type] =
    Arbitrary(SecondIndustryOptionsPage)

  implicit lazy val arbitraryGarageHandOrCleanerPage: Arbitrary[GarageHandOrCleanerPage.type] =
    Arbitrary(GarageHandOrCleanerPage)

  implicit lazy val arbitraryWhichRailwayTradePage: Arbitrary[WhichRailwayTradePage.type] =
    Arbitrary(WhichRailwayTradePage)

  implicit lazy val arbitraryFactoryEngineeringList1Page: Arbitrary[FactoryEngineeringList1Page.type] =
    Arbitrary(FactoryEngineeringList1Page)

  implicit lazy val arbitraryFactoryEngineeringList2Page: Arbitrary[FactoryEngineeringList2Page.type] =
    Arbitrary(FactoryEngineeringList2Page)

  implicit lazy val arbitraryHealthcareList2Page: Arbitrary[HealthcareList2Page.type] =
    Arbitrary(HealthcareList2Page)

  implicit lazy val arbitraryAncillaryEngineeringWhichTradePage: Arbitrary[AncillaryEngineeringWhichTradePage.type] =
    Arbitrary(AncillaryEngineeringWhichTradePage)

  implicit lazy val arbitraryConstructionalEngineeringApprenticePage: Arbitrary[ConstructionalEngineeringApprenticePage.type] =
    Arbitrary(ConstructionalEngineeringApprenticePage)

  implicit lazy val arbitraryConstructionalEngineeringList2Page: Arbitrary[ConstructionalEngineeringList2Page.type] =
    Arbitrary(ConstructionalEngineeringList2Page)

  implicit lazy val arbitraryHealthcareList1Page: Arbitrary[HealthcareList1Page.type] =
    Arbitrary(HealthcareList1Page)

  implicit lazy val arbitraryAirlineJobListPage: Arbitrary[AirlineJobListPage.type] =
    Arbitrary(AirlineJobListPage)

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
