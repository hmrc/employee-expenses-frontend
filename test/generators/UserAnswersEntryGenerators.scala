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

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.authenticated._
import pages.clothing.ClothingPage
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
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryConstructionOccupationsUserAnswersEntry
      : Arbitrary[(ConstructionOccupationsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionOccupationsPage.type]
        value <- arbitrary[ConstructionOccupations].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCabinCrewUserAnswersEntry: Arbitrary[(CabinCrewPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CabinCrewPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTextilesOccupationList1UserAnswersEntry
      : Arbitrary[(TextilesOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TextilesOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryApprenticeStorekeeperUserAnswersEntry
      : Arbitrary[(ShipyardApprenticeStorekeeperPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShipyardApprenticeStorekeeperPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryShipyardOccupationList2UserAnswersEntry
      : Arbitrary[(ShipyardOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShipyardOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryShipyardOccupationList1UserAnswersEntry
      : Arbitrary[(ShipyardOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ShipyardOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLabourerUserAnswersEntry: Arbitrary[(LabourerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LabourerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDocksOccupationList1UserAnswersEntry: Arbitrary[(DocksOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DocksOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFifthIndustryOptionsUserAnswersEntry: Arbitrary[(FifthIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FifthIndustryOptionsPage.type]
        value <- arbitrary[FifthIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAlreadyClaimingFREDifferentAmountsUserAnswersEntry
      : Arbitrary[(AlreadyClaimingFREDifferentAmountsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AlreadyClaimingFREDifferentAmountsPage.type]
        value <- arbitrary[AlreadyClaimingFREDifferentAmounts].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAlreadyClaimingFREUserAnswersEntry
      : Arbitrary[(AlreadyClaimingFRESameAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AlreadyClaimingFRESameAmountPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySameEmployerContributionAllYearsUserAnswersEntry
      : Arbitrary[(SameEmployerContributionAllYearsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SameEmployerContributionAllYearsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRemoveFRECodeUserAnswersEntry: Arbitrary[(RemoveFRECodePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RemoveFRECodePage.type]
        value <- arbitrary[TaxYearSelection].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryYourAddressUserAnswersEntry: Arbitrary[(YourAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourAddressPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAluminiumApprenticeUserAnswersEntry: Arbitrary[(AluminiumApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIronApprenticeUserAnswersEntry: Arbitrary[(IronApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryYourEmployerUserAnswersEntry: Arbitrary[(YourEmployerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourEmployerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConstructionalEngineeringList3UserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringList3Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringList3Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTaxYearSelectionUserAnswersEntry: Arbitrary[(TaxYearSelectionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxYearSelectionPage.type]
        value <- arbitrary[TaxYearSelection].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHeatingOccupationListUserAnswersEntry
      : Arbitrary[(HeatingOccupationListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HeatingOccupationListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFourthIndustryOptionsUserAnswersEntry
      : Arbitrary[(FourthIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FourthIndustryOptionsPage.type]
        value <- arbitrary[FourthIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIronMiningUserAnswersEntry: Arbitrary[(IronMiningPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronMiningPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIronMiningListUserAnswersEntry: Arbitrary[(IronMiningListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronMiningListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCommunitySupportOfficerUserAnswersEntry
      : Arbitrary[(CommunitySupportOfficerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CommunitySupportOfficerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMetropolitanPoliceUserAnswersEntry: Arbitrary[(MetropolitanPolicePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MetropolitanPolicePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryElectricalUserAnswersEntry: Arbitrary[(ElectricalPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ElectricalPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPrintingOccupationList2UserAnswersEntry
      : Arbitrary[(PrintingOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrintingOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryThirdIndustryOptionsUserAnswersEntry: Arbitrary[(ThirdIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ThirdIndustryOptionsPage.type]
        value <- arbitrary[ThirdIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySecurityGuardNHSUserAnswersEntry: Arbitrary[(SecurityGuardNHSPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecurityGuardNHSPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPrintingOccupationList1UserAnswersEntry
      : Arbitrary[(PrintingOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrintingOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClothingUserAnswersEntry: Arbitrary[(ClothingPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClothingPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCateringStaffNHSUserAnswersEntry: Arbitrary[(CateringStaffNHSPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CateringStaffNHSPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWoodFurnitureOccupationList2UserAnswersEntry
      : Arbitrary[(WoodFurnitureOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WoodFurnitureOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWoodFurnitureOccupationList3UserAnswersEntry
      : Arbitrary[(WoodFurnitureOccupationList3Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WoodFurnitureOccupationList3Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWoodFurnitureOccupationList1UserAnswersEntry
      : Arbitrary[(WoodFurnitureOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WoodFurnitureOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPoliceOfficerUserAnswersEntry: Arbitrary[(PoliceOfficerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PoliceOfficerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIronSteelOccupationListUserAnswersEntry
      : Arbitrary[(IronSteelOccupationListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IronSteelOccupationListPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAluminiumOccupationList1UserAnswersEntry
      : Arbitrary[(AluminiumOccupationList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumOccupationList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAluminiumOccupationList2UserAnswersEntry
      : Arbitrary[(AluminiumOccupationList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumOccupationList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAluminiumOccupationList3UserAnswersEntry
      : Arbitrary[(AluminiumOccupationList3Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AluminiumOccupationList3Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySpecialConstableUserAnswersEntry: Arbitrary[(SpecialConstablePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SpecialConstablePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTypeOfManufacturingUserAnswersEntry: Arbitrary[(TypeOfManufacturingPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TypeOfManufacturingPage.type]
        value <- arbitrary[TypeOfManufacturing].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFactoryEngineeringApprenticeUserAnswersEntry
      : Arbitrary[(FactoryEngineeringApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FactoryEngineeringApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTransportVehicleTradeUserAnswersEntry
      : Arbitrary[(TransportVehicleTradePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TransportVehicleTradePage.type]
        value <- arbitrary[TransportVehicleTrade].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTransportCarpenterUserAnswersEntry: Arbitrary[(TransportCarpenterPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TransportCarpenterPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySecondIndustryOptionsUserAnswersEntry
      : Arbitrary[(SecondIndustryOptionsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SecondIndustryOptionsPage.type]
        value <- arbitrary[SecondIndustryOptions].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryGarageHandOrCleanerUserAnswersEntry: Arbitrary[(GarageHandOrCleanerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[GarageHandOrCleanerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichRailwayTradeUserAnswersEntry: Arbitrary[(WhichRailwayTradePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichRailwayTradePage.type]
        value <- arbitrary[WhichRailwayTrade].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFactoryEngineeringList1UserAnswersEntry
      : Arbitrary[(FactoryEngineeringList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FactoryEngineeringList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFactoryEngineeringList2UserAnswersEntry
      : Arbitrary[(FactoryEngineeringList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FactoryEngineeringList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAncillaryEngineeringWhichTradeUserAnswersEntry
      : Arbitrary[(AncillaryEngineeringWhichTradePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AncillaryEngineeringWhichTradePage.type]
        value <- arbitrary[AncillaryEngineeringWhichTrade].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHealthcareList2UserAnswersEntry: Arbitrary[(HealthcareList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HealthcareList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConstructionalEngineeringApprenticeUserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringApprenticePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringApprenticePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConstructionalEngineeringList2UserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringList2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConstructionalEngineeringList2Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHealthcareList1UserAnswersEntry: Arbitrary[(HealthcareList1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HealthcareList1Page.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

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

  implicit lazy val arbitraryConstructionalEngineeringList1UserAnswersEntry
      : Arbitrary[(ConstructionalEngineeringList1Page.type, JsValue)] =
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
        value <- arbitrary[Boolean].map(Json.toJson(_))
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
