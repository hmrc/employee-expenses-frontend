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

import models.*
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.*
import pages.authenticated.*
import pages.clothing.ClothingPage
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
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  given arbitraryConstructionOccupationsUserAnswersEntry: Arbitrary[(ConstructionOccupationsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionOccupationsPage.type]
        value <- arbitrary[ConstructionOccupations].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryCabinCrewUserAnswersEntry: Arbitrary[(CabinCrewPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CabinCrewPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryTextilesOccupationList1UserAnswersEntry: Arbitrary[(TextilesOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TextilesOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryApprenticeStorekeeperUserAnswersEntry: Arbitrary[(ShipyardApprenticeStorekeeperPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShipyardApprenticeStorekeeperPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryShipyardOccupationList2UserAnswersEntry: Arbitrary[(ShipyardOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShipyardOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryShipyardOccupationList1UserAnswersEntry: Arbitrary[(ShipyardOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShipyardOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryLabourerUserAnswersEntry: Arbitrary[(LabourerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LabourerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryDocksOccupationList1UserAnswersEntry: Arbitrary[(DocksOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DocksOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryFifthIndustryOptionsUserAnswersEntry: Arbitrary[(FifthIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FifthIndustryOptionsPage.type]
        value <- arbitrary[FifthIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAlreadyClaimingFREDifferentAmountsUserAnswersEntry
      : Arbitrary[(AlreadyClaimingFREDifferentAmountsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AlreadyClaimingFREDifferentAmountsPage.type]
        value <- arbitrary[AlreadyClaimingFREDifferentAmounts].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAlreadyClaimingFREUserAnswersEntry: Arbitrary[(AlreadyClaimingFRESameAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AlreadyClaimingFRESameAmountPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySameEmployerContributionAllYearsUserAnswersEntry
      : Arbitrary[(SameEmployerContributionAllYearsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SameEmployerContributionAllYearsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryRemoveFRECodeUserAnswersEntry: Arbitrary[(RemoveFRECodePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RemoveFRECodePage.type]
        value <- arbitrary[TaxYearSelection].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryYourAddressUserAnswersEntry: Arbitrary[(YourAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourAddressPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAluminiumApprenticeUserAnswersEntry: Arbitrary[(AluminiumApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryIronApprenticeUserAnswersEntry: Arbitrary[(IronApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryYourEmployerUserAnswersEntry: Arbitrary[(YourEmployerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourEmployerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryConstructionalEngineeringList3UserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringList3Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringList3Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryTaxYearSelectionUserAnswersEntry: Arbitrary[(TaxYearSelectionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxYearSelectionPage.type]
        value <- arbitrary[TaxYearSelection].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryHeatingOccupationListUserAnswersEntry: Arbitrary[(HeatingOccupationListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HeatingOccupationListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryFourthIndustryOptionsUserAnswersEntry: Arbitrary[(FourthIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FourthIndustryOptionsPage.type]
        value <- arbitrary[FourthIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryIronMiningUserAnswersEntry: Arbitrary[(IronMiningPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronMiningPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryIronMiningListUserAnswersEntry: Arbitrary[(IronMiningListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronMiningListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryCommunitySupportOfficerUserAnswersEntry: Arbitrary[(CommunitySupportOfficerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CommunitySupportOfficerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryMetropolitanPoliceUserAnswersEntry: Arbitrary[(MetropolitanPolicePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MetropolitanPolicePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryElectricalUserAnswersEntry: Arbitrary[(ElectricalPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ElectricalPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryPrintingOccupationList2UserAnswersEntry: Arbitrary[(PrintingOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrintingOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryThirdIndustryOptionsUserAnswersEntry: Arbitrary[(ThirdIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ThirdIndustryOptionsPage.type]
        value <- arbitrary[ThirdIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySecurityGuardNHSUserAnswersEntry: Arbitrary[(SecurityGuardNHSPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecurityGuardNHSPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryPrintingOccupationList1UserAnswersEntry: Arbitrary[(PrintingOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrintingOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryClothingUserAnswersEntry: Arbitrary[(ClothingPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClothingPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryCateringStaffNHSUserAnswersEntry: Arbitrary[(CateringStaffNHSPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CateringStaffNHSPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryWoodFurnitureOccupationList2UserAnswersEntry
      : Arbitrary[(WoodFurnitureOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WoodFurnitureOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryWoodFurnitureOccupationList3UserAnswersEntry
      : Arbitrary[(WoodFurnitureOccupationList3Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WoodFurnitureOccupationList3Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryWoodFurnitureOccupationList1UserAnswersEntry
      : Arbitrary[(WoodFurnitureOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WoodFurnitureOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryPoliceOfficerUserAnswersEntry: Arbitrary[(PoliceOfficerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PoliceOfficerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryIronSteelOccupationListUserAnswersEntry: Arbitrary[(IronSteelOccupationListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronSteelOccupationListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAluminiumOccupationList1UserAnswersEntry: Arbitrary[(AluminiumOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAluminiumOccupationList2UserAnswersEntry: Arbitrary[(AluminiumOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAluminiumOccupationList3UserAnswersEntry: Arbitrary[(AluminiumOccupationList3Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumOccupationList3Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySpecialConstableUserAnswersEntry: Arbitrary[(SpecialConstablePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SpecialConstablePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryTypeOfManufacturingUserAnswersEntry: Arbitrary[(TypeOfManufacturingPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TypeOfManufacturingPage.type]
        value <- arbitrary[TypeOfManufacturing].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryFactoryEngineeringApprenticeUserAnswersEntry
      : Arbitrary[(FactoryEngineeringApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FactoryEngineeringApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryTransportVehicleTradeUserAnswersEntry: Arbitrary[(TransportVehicleTradePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TransportVehicleTradePage.type]
        value <- arbitrary[TransportVehicleTrade].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryTransportCarpenterUserAnswersEntry: Arbitrary[(TransportCarpenterPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TransportCarpenterPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySecondIndustryOptionsUserAnswersEntry: Arbitrary[(SecondIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecondIndustryOptionsPage.type]
        value <- arbitrary[SecondIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryGarageHandOrCleanerUserAnswersEntry: Arbitrary[(GarageHandOrCleanerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GarageHandOrCleanerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryWhichRailwayTradeUserAnswersEntry: Arbitrary[(WhichRailwayTradePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichRailwayTradePage.type]
        value <- arbitrary[WhichRailwayTrade].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryFactoryEngineeringList1UserAnswersEntry: Arbitrary[(FactoryEngineeringList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FactoryEngineeringList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryFactoryEngineeringList2UserAnswersEntry: Arbitrary[(FactoryEngineeringList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FactoryEngineeringList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAncillaryEngineeringWhichTradeUserAnswersEntry
      : Arbitrary[(AncillaryEngineeringWhichTradePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AncillaryEngineeringWhichTradePage.type]
        value <- arbitrary[AncillaryEngineeringWhichTrade].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryHealthcareList2UserAnswersEntry: Arbitrary[(HealthcareList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HealthcareList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryConstructionalEngineeringApprenticeUserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryConstructionalEngineeringList2UserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryHealthcareList1UserAnswersEntry: Arbitrary[(HealthcareList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HealthcareList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAirlineJobListUserAnswersEntry: Arbitrary[(AirlineJobListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AirlineJobListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryTypeOfTransportUserAnswersEntry: Arbitrary[(TypeOfTransportPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TypeOfTransportPage.type]
        value <- arbitrary[TypeOfTransport].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryConstructionalEngineeringList1UserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryTypeOfEngineeringUserAnswersEntry: Arbitrary[(TypeOfEngineeringPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TypeOfEngineeringPage.type]
        value <- arbitrary[TypeOfEngineering].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAmbulanceStaffUserAnswersEntry: Arbitrary[(AmbulanceStaffPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AmbulanceStaffPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryEmployerContributionUserAnswersEntry: Arbitrary[(EmployerContributionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmployerContributionPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryMultipleEmploymentsUserAnswersEntry: Arbitrary[(MultipleEmploymentsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MultipleEmploymentsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryExpensesEmployerPaidUserAnswersEntry: Arbitrary[(ExpensesEmployerPaidPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ExpensesEmployerPaidPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryFirstIndustryOptionsUserAnswersEntry: Arbitrary[(FirstIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FirstIndustryOptionsPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

}
