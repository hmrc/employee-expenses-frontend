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
import pages._
import pages.authenticated._
import pages.clothing._
import pages.construction._
import pages.docks.DocksOccupationList1Page
import pages.electrical.ElectricalPage
import pages.healthcare._
import pages.engineering._
import pages.manufacturing._
import pages.police._
import pages.transport._
import pages.foodCatering._
import pages.heating._
import pages.security._
import pages.printing._
import pages.shipyard._
import pages.textiles.TextilesOccupationList1Page

trait PageGenerators {

  given arbitraryConstructionOccupationsPage: Arbitrary[ConstructionOccupationsPage.type] =
    Arbitrary(ConstructionOccupationsPage)

  given arbitraryCabinCrewPage: Arbitrary[CabinCrewPage.type] =
    Arbitrary(CabinCrewPage)

  given arbitraryTextilesOccupationList1Page: Arbitrary[TextilesOccupationList1Page.type] =
    Arbitrary(TextilesOccupationList1Page)

  given arbitraryDocksOccupationList1Page: Arbitrary[DocksOccupationList1Page.type] =
    Arbitrary(DocksOccupationList1Page)

  given arbitraryLabourerPage: Arbitrary[LabourerPage.type] =
    Arbitrary(LabourerPage)

  given arbitraryShipyardApprenticeStorekeeperPage: Arbitrary[ShipyardApprenticeStorekeeperPage.type] =
    Arbitrary(ShipyardApprenticeStorekeeperPage)

  given arbitraryShipyardOccupationList2Page: Arbitrary[ShipyardOccupationList2Page.type] =
    Arbitrary(ShipyardOccupationList2Page)

  given arbitraryShipyardOccupationList1Page: Arbitrary[ShipyardOccupationList1Page.type] =
    Arbitrary(ShipyardOccupationList1Page)

  given arbitraryFifthIndustryOptionsPage: Arbitrary[FifthIndustryOptionsPage.type] =
    Arbitrary(FifthIndustryOptionsPage)

  given arbitraryAlreadyClaimingFREDifferentAmountsPage: Arbitrary[AlreadyClaimingFREDifferentAmountsPage.type] =
    Arbitrary(AlreadyClaimingFREDifferentAmountsPage)

  given arbitraryAlreadyClaimingFREPage: Arbitrary[AlreadyClaimingFRESameAmountPage.type] =
    Arbitrary(AlreadyClaimingFRESameAmountPage)

  given arbitrarySameEmployerContributionAllYearsPage: Arbitrary[SameEmployerContributionAllYearsPage.type] =
    Arbitrary(SameEmployerContributionAllYearsPage)

  given arbitraryChangeWhichTaxYearsPage: Arbitrary[ChangeWhichTaxYearsPage.type] =
    Arbitrary(ChangeWhichTaxYearsPage)

  given arbitraryRemoveFRECodePage: Arbitrary[RemoveFRECodePage.type] =
    Arbitrary(RemoveFRECodePage)

  given arbitraryAluminiumApprenticePage: Arbitrary[AluminiumApprenticePage.type] =
    Arbitrary(AluminiumApprenticePage)

  given arbitraryIronApprenticePage: Arbitrary[IronApprenticePage.type] =
    Arbitrary(IronApprenticePage)

  given arbitraryYourAddressPage: Arbitrary[YourAddressPage.type] =
    Arbitrary(YourAddressPage)

  given arbitraryYourEmployerPage: Arbitrary[YourEmployerPage.type] =
    Arbitrary(YourEmployerPage)

  given arbitraryConstructionalEngineeringList3Page: Arbitrary[ConstructionalEngineeringList3Page.type] =
    Arbitrary(ConstructionalEngineeringList3Page)

  given arbitraryHeatingOccupationListPage: Arbitrary[HeatingOccupationListPage.type] =
    Arbitrary(HeatingOccupationListPage)

  given arbitraryTaxYearSelectionPage: Arbitrary[TaxYearSelectionPage.type] =
    Arbitrary(TaxYearSelectionPage)

  given arbitraryFourthIndustryOptionsPage: Arbitrary[FourthIndustryOptionsPage.type] =
    Arbitrary(FourthIndustryOptionsPage)

  given arbitraryIronMiningPage: Arbitrary[IronMiningPage.type] =
    Arbitrary(IronMiningPage)

  given arbitraryIronMiningListPage: Arbitrary[IronMiningListPage.type] =
    Arbitrary(IronMiningListPage)

  given arbitraryCommunitySupportOfficerPage: Arbitrary[CommunitySupportOfficerPage.type] =
    Arbitrary(CommunitySupportOfficerPage)

  given arbitraryMetropolitanPolicePage: Arbitrary[MetropolitanPolicePage.type] =
    Arbitrary(MetropolitanPolicePage)

  given arbitraryElectricalPage: Arbitrary[ElectricalPage.type] =
    Arbitrary(ElectricalPage)

  given arbitraryPrintingOccupationList2Page: Arbitrary[PrintingOccupationList2Page.type] =
    Arbitrary(PrintingOccupationList2Page)

  given arbitraryThirdIndustryOptionsPage: Arbitrary[ThirdIndustryOptionsPage.type] =
    Arbitrary(ThirdIndustryOptionsPage)

  given arbitraryPrintingOccupationList1Page: Arbitrary[PrintingOccupationList1Page.type] =
    Arbitrary(PrintingOccupationList1Page)

  given arbitraryAluminiumOccupationList3Page: Arbitrary[AluminiumOccupationList3Page.type] =
    Arbitrary(AluminiumOccupationList3Page)

  given arbitrarySecurityGuardNHSPage: Arbitrary[SecurityGuardNHSPage.type] =
    Arbitrary(SecurityGuardNHSPage)

  given arbitraryClothingPage: Arbitrary[ClothingPage.type] =
    Arbitrary(ClothingPage)

  given arbitraryCateringStaffNHSPage: Arbitrary[CateringStaffNHSPage.type] =
    Arbitrary(CateringStaffNHSPage)

  given arbitraryWoodFurnitureOccupationList2Page: Arbitrary[WoodFurnitureOccupationList2Page.type] =
    Arbitrary(WoodFurnitureOccupationList2Page)

  given arbitraryWoodFurnitureOccupationList3Page: Arbitrary[WoodFurnitureOccupationList3Page.type] =
    Arbitrary(WoodFurnitureOccupationList3Page)

  given arbitraryWoodFurnitureOccupationList1Page: Arbitrary[WoodFurnitureOccupationList1Page.type] =
    Arbitrary(WoodFurnitureOccupationList1Page)

  given arbitraryPoliceOfficerPage: Arbitrary[PoliceOfficerPage.type] =
    Arbitrary(PoliceOfficerPage)

  given arbitraryIronSteelOccupationListPage: Arbitrary[IronSteelOccupationListPage.type] =
    Arbitrary(IronSteelOccupationListPage)

  given arbitraryAluminiumOccupationList2Page: Arbitrary[AluminiumOccupationList2Page.type] =
    Arbitrary(AluminiumOccupationList2Page)

  given arbitrarySpecialConstablePage: Arbitrary[SpecialConstablePage.type] =
    Arbitrary(SpecialConstablePage)

  given arbitraryAluminiumOccupationList1Page: Arbitrary[AluminiumOccupationList1Page.type] =
    Arbitrary(AluminiumOccupationList1Page)

  given arbitraryTypeOfManufacturingPage: Arbitrary[TypeOfManufacturingPage.type] =
    Arbitrary(TypeOfManufacturingPage)

  given arbitraryTransportVehicleTradePage: Arbitrary[TransportVehicleTradePage.type] =
    Arbitrary(TransportVehicleTradePage)

  given arbitraryFactoryEngineeringApprenticePage: Arbitrary[FactoryEngineeringApprenticePage.type] =
    Arbitrary(FactoryEngineeringApprenticePage)

  given arbitraryTransportCarpenterPage: Arbitrary[TransportCarpenterPage.type] =
    Arbitrary(TransportCarpenterPage)

  given arbitrarySecondIndustryOptionsPage: Arbitrary[SecondIndustryOptionsPage.type] =
    Arbitrary(SecondIndustryOptionsPage)

  given arbitraryGarageHandOrCleanerPage: Arbitrary[GarageHandOrCleanerPage.type] =
    Arbitrary(GarageHandOrCleanerPage)

  given arbitraryWhichRailwayTradePage: Arbitrary[WhichRailwayTradePage.type] =
    Arbitrary(WhichRailwayTradePage)

  given arbitraryFactoryEngineeringList1Page: Arbitrary[FactoryEngineeringList1Page.type] =
    Arbitrary(FactoryEngineeringList1Page)

  given arbitraryFactoryEngineeringList2Page: Arbitrary[FactoryEngineeringList2Page.type] =
    Arbitrary(FactoryEngineeringList2Page)

  given arbitraryHealthcareList2Page: Arbitrary[HealthcareList2Page.type] =
    Arbitrary(HealthcareList2Page)

  given arbitraryAncillaryEngineeringWhichTradePage: Arbitrary[AncillaryEngineeringWhichTradePage.type] =
    Arbitrary(AncillaryEngineeringWhichTradePage)

  given arbitraryConstructionalEngineeringApprenticePage: Arbitrary[ConstructionalEngineeringApprenticePage.type] =
    Arbitrary(ConstructionalEngineeringApprenticePage)

  given arbitraryConstructionalEngineeringList2Page: Arbitrary[ConstructionalEngineeringList2Page.type] =
    Arbitrary(ConstructionalEngineeringList2Page)

  given arbitraryHealthcareList1Page: Arbitrary[HealthcareList1Page.type] =
    Arbitrary(HealthcareList1Page)

  given arbitraryAirlineJobListPage: Arbitrary[AirlineJobListPage.type] =
    Arbitrary(AirlineJobListPage)

  given arbitraryTypeOfTransportPage: Arbitrary[TypeOfTransportPage.type] =
    Arbitrary(TypeOfTransportPage)

  given arbitraryConstructionalEngineeringList1Page: Arbitrary[ConstructionalEngineeringList1Page.type] =
    Arbitrary(ConstructionalEngineeringList1Page)

  given arbitraryTypeOfEngineeringPage: Arbitrary[TypeOfEngineeringPage.type] =
    Arbitrary(TypeOfEngineeringPage)

  given arbitraryAmbulanceStaffPage: Arbitrary[AmbulanceStaffPage.type] =
    Arbitrary(AmbulanceStaffPage)

  given arbitraryEmployerContributionPage: Arbitrary[EmployerContributionPage.type] =
    Arbitrary(EmployerContributionPage)

  given arbitraryMultipleEmploymentsPage: Arbitrary[MultipleEmploymentsPage.type] =
    Arbitrary(MultipleEmploymentsPage)

  given arbitraryExpensesEmployerPaidPage: Arbitrary[ExpensesEmployerPaidPage.type] =
    Arbitrary(ExpensesEmployerPaidPage)

  given arbitraryFirstIndustryOptionsPage: Arbitrary[FirstIndustryOptionsPage.type] =
    Arbitrary(FirstIndustryOptionsPage)

}
