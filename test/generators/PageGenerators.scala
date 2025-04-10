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

  implicit lazy val arbitraryConstructionOccupationsPage: Arbitrary[ConstructionOccupationsPage.type] =
    Arbitrary(ConstructionOccupationsPage)

  implicit lazy val arbitraryCabinCrewPage: Arbitrary[CabinCrewPage.type] =
    Arbitrary(CabinCrewPage)

  implicit lazy val arbitraryTextilesOccupationList1Page: Arbitrary[TextilesOccupationList1Page.type] =
    Arbitrary(TextilesOccupationList1Page)

  implicit lazy val arbitraryDocksOccupationList1Page: Arbitrary[DocksOccupationList1Page.type] =
    Arbitrary(DocksOccupationList1Page)

  implicit lazy val arbitraryLabourerPage: Arbitrary[LabourerPage.type] =
    Arbitrary(LabourerPage)

  implicit lazy val arbitraryShipyardApprenticeStorekeeperPage: Arbitrary[ShipyardApprenticeStorekeeperPage.type] =
    Arbitrary(ShipyardApprenticeStorekeeperPage)

  implicit lazy val arbitraryShipyardOccupationList2Page: Arbitrary[ShipyardOccupationList2Page.type] =
    Arbitrary(ShipyardOccupationList2Page)

  implicit lazy val arbitraryShipyardOccupationList1Page: Arbitrary[ShipyardOccupationList1Page.type] =
    Arbitrary(ShipyardOccupationList1Page)

  implicit lazy val arbitraryFifthIndustryOptionsPage: Arbitrary[FifthIndustryOptionsPage.type] =
    Arbitrary(FifthIndustryOptionsPage)

  implicit lazy val arbitraryAlreadyClaimingFREDifferentAmountsPage
      : Arbitrary[AlreadyClaimingFREDifferentAmountsPage.type] =
    Arbitrary(AlreadyClaimingFREDifferentAmountsPage)

  implicit lazy val arbitraryAlreadyClaimingFREPage: Arbitrary[AlreadyClaimingFRESameAmountPage.type] =
    Arbitrary(AlreadyClaimingFRESameAmountPage)

  implicit lazy val arbitrarySameEmployerContributionAllYearsPage
      : Arbitrary[SameEmployerContributionAllYearsPage.type] =
    Arbitrary(SameEmployerContributionAllYearsPage)

  implicit lazy val arbitraryChangeWhichTaxYearsPage: Arbitrary[ChangeWhichTaxYearsPage.type] =
    Arbitrary(ChangeWhichTaxYearsPage)

  implicit lazy val arbitraryRemoveFRECodePage: Arbitrary[RemoveFRECodePage.type] =
    Arbitrary(RemoveFRECodePage)

  implicit lazy val arbitraryAluminiumApprenticePage: Arbitrary[AluminiumApprenticePage.type] =
    Arbitrary(AluminiumApprenticePage)

  implicit lazy val arbitraryIronApprenticePage: Arbitrary[IronApprenticePage.type] =
    Arbitrary(IronApprenticePage)

  implicit lazy val arbitraryYourAddressPage: Arbitrary[YourAddressPage.type] =
    Arbitrary(YourAddressPage)

  implicit lazy val arbitraryYourEmployerPage: Arbitrary[YourEmployerPage.type] =
    Arbitrary(YourEmployerPage)

  implicit lazy val arbitraryConstructionalEngineeringList3Page: Arbitrary[ConstructionalEngineeringList3Page.type] =
    Arbitrary(ConstructionalEngineeringList3Page)

  implicit lazy val arbitraryHeatingOccupationListPage: Arbitrary[HeatingOccupationListPage.type] =
    Arbitrary(HeatingOccupationListPage)

  implicit lazy val arbitraryTaxYearSelectionPage: Arbitrary[TaxYearSelectionPage.type] =
    Arbitrary(TaxYearSelectionPage)

  implicit lazy val arbitraryFourthIndustryOptionsPage: Arbitrary[FourthIndustryOptionsPage.type] =
    Arbitrary(FourthIndustryOptionsPage)

  implicit lazy val arbitraryIronMiningPage: Arbitrary[IronMiningPage.type] =
    Arbitrary(IronMiningPage)

  implicit lazy val arbitraryIronMiningListPage: Arbitrary[IronMiningListPage.type] =
    Arbitrary(IronMiningListPage)

  implicit lazy val arbitraryCommunitySupportOfficerPage: Arbitrary[CommunitySupportOfficerPage.type] =
    Arbitrary(CommunitySupportOfficerPage)

  implicit lazy val arbitraryMetropolitanPolicePage: Arbitrary[MetropolitanPolicePage.type] =
    Arbitrary(MetropolitanPolicePage)

  implicit lazy val arbitraryElectricalPage: Arbitrary[ElectricalPage.type] =
    Arbitrary(ElectricalPage)

  implicit lazy val arbitraryPrintingOccupationList2Page: Arbitrary[PrintingOccupationList2Page.type] =
    Arbitrary(PrintingOccupationList2Page)

  implicit lazy val arbitraryThirdIndustryOptionsPage: Arbitrary[ThirdIndustryOptionsPage.type] =
    Arbitrary(ThirdIndustryOptionsPage)

  implicit lazy val arbitraryPrintingOccupationList1Page: Arbitrary[PrintingOccupationList1Page.type] =
    Arbitrary(PrintingOccupationList1Page)

  implicit lazy val arbitraryAluminiumOccupationList3Page: Arbitrary[AluminiumOccupationList3Page.type] =
    Arbitrary(AluminiumOccupationList3Page)

  implicit lazy val arbitrarySecurityGuardNHSPage: Arbitrary[SecurityGuardNHSPage.type] =
    Arbitrary(SecurityGuardNHSPage)

  implicit lazy val arbitraryClothingPage: Arbitrary[ClothingPage.type] =
    Arbitrary(ClothingPage)

  implicit lazy val arbitraryCateringStaffNHSPage: Arbitrary[CateringStaffNHSPage.type] =
    Arbitrary(CateringStaffNHSPage)

  implicit lazy val arbitraryWoodFurnitureOccupationList2Page: Arbitrary[WoodFurnitureOccupationList2Page.type] =
    Arbitrary(WoodFurnitureOccupationList2Page)

  implicit lazy val arbitraryWoodFurnitureOccupationList3Page: Arbitrary[WoodFurnitureOccupationList3Page.type] =
    Arbitrary(WoodFurnitureOccupationList3Page)

  implicit lazy val arbitraryWoodFurnitureOccupationList1Page: Arbitrary[WoodFurnitureOccupationList1Page.type] =
    Arbitrary(WoodFurnitureOccupationList1Page)

  implicit lazy val arbitraryPoliceOfficerPage: Arbitrary[PoliceOfficerPage.type] =
    Arbitrary(PoliceOfficerPage)

  implicit lazy val arbitraryIronSteelOccupationListPage: Arbitrary[IronSteelOccupationListPage.type] =
    Arbitrary(IronSteelOccupationListPage)

  implicit lazy val arbitraryAluminiumOccupationList2Page: Arbitrary[AluminiumOccupationList2Page.type] =
    Arbitrary(AluminiumOccupationList2Page)

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

  implicit lazy val arbitraryConstructionalEngineeringApprenticePage
      : Arbitrary[ConstructionalEngineeringApprenticePage.type] =
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
