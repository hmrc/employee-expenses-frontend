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

package generators

import org.scalacheck.Arbitrary
import pages.*
import pages.authenticated.*
import pages.clothing.*
import pages.construction.*
import pages.docks.DocksOccupationList1Page
import pages.electrical.ElectricalPage
import pages.healthcare.*
import pages.engineering.*
import pages.manufacturing.*
import pages.police.*
import pages.transport.*
import pages.foodCatering.*
import pages.heating.*
import pages.security.*
import pages.printing.*
import pages.shipyard.*
import pages.textiles.TextilesOccupationList1Page

trait PageGenerators {

  given Arbitrary[ConstructionOccupationsPage.type] =
    Arbitrary(ConstructionOccupationsPage)

  given Arbitrary[CabinCrewPage.type] =
    Arbitrary(CabinCrewPage)

  given Arbitrary[TextilesOccupationList1Page.type] =
    Arbitrary(TextilesOccupationList1Page)

  given Arbitrary[DocksOccupationList1Page.type] =
    Arbitrary(DocksOccupationList1Page)

  given Arbitrary[LabourerPage.type] =
    Arbitrary(LabourerPage)

  given Arbitrary[ShipyardApprenticeStorekeeperPage.type] =
    Arbitrary(ShipyardApprenticeStorekeeperPage)

  given Arbitrary[ShipyardOccupationList2Page.type] =
    Arbitrary(ShipyardOccupationList2Page)

  given Arbitrary[ShipyardOccupationList1Page.type] =
    Arbitrary(ShipyardOccupationList1Page)

  given Arbitrary[FifthIndustryOptionsPage.type] =
    Arbitrary(FifthIndustryOptionsPage)

  given Arbitrary[AlreadyClaimingFREDifferentAmountsPage.type] =
    Arbitrary(AlreadyClaimingFREDifferentAmountsPage)

  given Arbitrary[AlreadyClaimingFRESameAmountPage.type] =
    Arbitrary(AlreadyClaimingFRESameAmountPage)

  given Arbitrary[SameEmployerContributionAllYearsPage.type] =
    Arbitrary(SameEmployerContributionAllYearsPage)

  given Arbitrary[ChangeWhichTaxYearsPage.type] =
    Arbitrary(ChangeWhichTaxYearsPage)

  given Arbitrary[RemoveFRECodePage.type] =
    Arbitrary(RemoveFRECodePage)

  given Arbitrary[AluminiumApprenticePage.type] =
    Arbitrary(AluminiumApprenticePage)

  given Arbitrary[IronApprenticePage.type] =
    Arbitrary(IronApprenticePage)

  given Arbitrary[YourAddressPage.type] =
    Arbitrary(YourAddressPage)

  given Arbitrary[YourEmployerPage.type] =
    Arbitrary(YourEmployerPage)

  given Arbitrary[ConstructionalEngineeringList3Page.type] =
    Arbitrary(ConstructionalEngineeringList3Page)

  given Arbitrary[HeatingOccupationListPage.type] =
    Arbitrary(HeatingOccupationListPage)

  given Arbitrary[TaxYearSelectionPage.type] =
    Arbitrary(TaxYearSelectionPage)

  given Arbitrary[FourthIndustryOptionsPage.type] =
    Arbitrary(FourthIndustryOptionsPage)

  given Arbitrary[IronMiningPage.type] =
    Arbitrary(IronMiningPage)

  given Arbitrary[IronMiningListPage.type] =
    Arbitrary(IronMiningListPage)

  given Arbitrary[CommunitySupportOfficerPage.type] =
    Arbitrary(CommunitySupportOfficerPage)

  given Arbitrary[MetropolitanPolicePage.type] =
    Arbitrary(MetropolitanPolicePage)

  given Arbitrary[ElectricalPage.type] =
    Arbitrary(ElectricalPage)

  given Arbitrary[PrintingOccupationList2Page.type] =
    Arbitrary(PrintingOccupationList2Page)

  given Arbitrary[ThirdIndustryOptionsPage.type] =
    Arbitrary(ThirdIndustryOptionsPage)

  given Arbitrary[PrintingOccupationList1Page.type] =
    Arbitrary(PrintingOccupationList1Page)

  given Arbitrary[AluminiumOccupationList3Page.type] =
    Arbitrary(AluminiumOccupationList3Page)

  given Arbitrary[SecurityGuardNHSPage.type] =
    Arbitrary(SecurityGuardNHSPage)

  given Arbitrary[ClothingPage.type] =
    Arbitrary(ClothingPage)

  given Arbitrary[CateringStaffNHSPage.type] =
    Arbitrary(CateringStaffNHSPage)

  given Arbitrary[WoodFurnitureOccupationList2Page.type] =
    Arbitrary(WoodFurnitureOccupationList2Page)

  given Arbitrary[WoodFurnitureOccupationList3Page.type] =
    Arbitrary(WoodFurnitureOccupationList3Page)

  given Arbitrary[WoodFurnitureOccupationList1Page.type] =
    Arbitrary(WoodFurnitureOccupationList1Page)

  given Arbitrary[PoliceOfficerPage.type] =
    Arbitrary(PoliceOfficerPage)

  given Arbitrary[IronSteelOccupationListPage.type] =
    Arbitrary(IronSteelOccupationListPage)

  given Arbitrary[AluminiumOccupationList2Page.type] =
    Arbitrary(AluminiumOccupationList2Page)

  given Arbitrary[SpecialConstablePage.type] =
    Arbitrary(SpecialConstablePage)

  given Arbitrary[AluminiumOccupationList1Page.type] =
    Arbitrary(AluminiumOccupationList1Page)

  given Arbitrary[TypeOfManufacturingPage.type] =
    Arbitrary(TypeOfManufacturingPage)

  given Arbitrary[TransportVehicleTradePage.type] =
    Arbitrary(TransportVehicleTradePage)

  given Arbitrary[FactoryEngineeringApprenticePage.type] =
    Arbitrary(FactoryEngineeringApprenticePage)

  given Arbitrary[TransportCarpenterPage.type] =
    Arbitrary(TransportCarpenterPage)

  given Arbitrary[SecondIndustryOptionsPage.type] =
    Arbitrary(SecondIndustryOptionsPage)

  given Arbitrary[GarageHandOrCleanerPage.type] =
    Arbitrary(GarageHandOrCleanerPage)

  given Arbitrary[WhichRailwayTradePage.type] =
    Arbitrary(WhichRailwayTradePage)

  given Arbitrary[FactoryEngineeringList1Page.type] =
    Arbitrary(FactoryEngineeringList1Page)

  given Arbitrary[FactoryEngineeringList2Page.type] =
    Arbitrary(FactoryEngineeringList2Page)

  given Arbitrary[HealthcareList2Page.type] =
    Arbitrary(HealthcareList2Page)

  given Arbitrary[AncillaryEngineeringWhichTradePage.type] =
    Arbitrary(AncillaryEngineeringWhichTradePage)

  given Arbitrary[ConstructionalEngineeringApprenticePage.type] =
    Arbitrary(ConstructionalEngineeringApprenticePage)

  given Arbitrary[ConstructionalEngineeringList2Page.type] =
    Arbitrary(ConstructionalEngineeringList2Page)

  given Arbitrary[HealthcareList1Page.type] =
    Arbitrary(HealthcareList1Page)

  given Arbitrary[AirlineJobListPage.type] =
    Arbitrary(AirlineJobListPage)

  given Arbitrary[TypeOfTransportPage.type] =
    Arbitrary(TypeOfTransportPage)

  given Arbitrary[ConstructionalEngineeringList1Page.type] =
    Arbitrary(ConstructionalEngineeringList1Page)

  given Arbitrary[TypeOfEngineeringPage.type] =
    Arbitrary(TypeOfEngineeringPage)

  given Arbitrary[AmbulanceStaffPage.type] =
    Arbitrary(AmbulanceStaffPage)

  given Arbitrary[EmployerContributionPage.type] =
    Arbitrary(EmployerContributionPage)

  given Arbitrary[MultipleEmploymentsPage.type] =
    Arbitrary(MultipleEmploymentsPage)

  given Arbitrary[ExpensesEmployerPaidPage.type] =
    Arbitrary(ExpensesEmployerPaidPage)

  given Arbitrary[FirstIndustryOptionsPage.type] =
    Arbitrary(FirstIndustryOptionsPage)

}
