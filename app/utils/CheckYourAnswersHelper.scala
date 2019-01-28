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

package utils

import controllers.routes._
import controllers.engineering.routes._
import controllers.manufacturing.routes._
import controllers.healthcare.routes._
import controllers.transport.routes._
import controllers.police.routes._
import controllers.foodCatering.routes._
import models.{CheckMode, UserAnswers}
import pages._
import pages.engineering._
import pages.foodCatering._
import pages.healthcare._
import pages.manufacturing._
import pages.police._
import pages.transport._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers)(implicit messages: Messages) {

  def secondIndustryOptions: Option[AnswerRow] = userAnswers.get(SecondIndustryOptionsPage) map {
    x => AnswerRow("secondIndustryOptions.checkYourAnswersLabel", s"secondIndustryOptions.$x", true,
      SecondIndustryOptionsController.onPageLoad(CheckMode).url)
  }

  def employerContribution: Option[AnswerRow] = userAnswers.get(EmployerContributionPage) map {
    x => AnswerRow("employerContribution.checkYourAnswersLabel", s"employerContribution.$x", true,
      EmployerContributionController.onPageLoad(CheckMode).url)
  }

  def multipleEmployments: Option[AnswerRow] = userAnswers.get(MultipleEmploymentsPage) map {
    x => AnswerRow("multipleEmployments.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      MultipleEmploymentsController.onPageLoad(CheckMode).url)
  }

  def expensesEmployerPaid: Option[AnswerRow] = userAnswers.get(ExpensesEmployerPaidPage) map {
    x => AnswerRow("expensesEmployerPaid.checkYourAnswersLabel", s"$x", false,
      ExpensesEmployerPaidController.onPageLoad(CheckMode).url)
  }
  def firstIndustryOptions: Option[AnswerRow] = userAnswers.get(FirstIndustryOptionsPage) map {
    x => AnswerRow("firstIndustryOptions.checkYourAnswersLabel", s"$x", false,
      FirstIndustryOptionsController.onPageLoad(CheckMode).url)
  }

  //Engineering

  def typeOfEngineering: Option[AnswerRow] = userAnswers.get(TypeOfEngineeringPage) map {
    x => AnswerRow("typeOfEngineering.checkYourAnswersLabel", s"typeOfEngineering.$x", true,
      TypeOfEngineeringController.onPageLoad(CheckMode).url)
  }

  def factoryEngineeringList1: Option[AnswerRow] = userAnswers.get(FactoryEngineeringList1Page) map {
    x => AnswerRow("factoryEngineeringList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      FactoryEngineeringList1Controller.onPageLoad(CheckMode).url)
  }

  def factoryEngineeringList2: Option[AnswerRow] = userAnswers.get(FactoryEngineeringList2Page) map {
    x => AnswerRow("factoryEngineeringList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      FactoryEngineeringList2Controller.onPageLoad(CheckMode).url)
  }

  def factoryEngineeringApprentice: Option[AnswerRow] = userAnswers.get(FactoryEngineeringApprenticePage) map {
    x => AnswerRow("factoryEngineeringApprentice.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      FactoryEngineeringApprenticeController.onPageLoad(CheckMode).url)
  }

  def ancillaryEngineeringWhichTrade: Option[AnswerRow] = userAnswers.get(AncillaryEngineeringWhichTradePage) map {
    x => AnswerRow("ancillaryEngineeringWhichTrade.checkYourAnswersLabel", s"ancillaryEngineeringWhichTrade.$x", true,
      AncillaryEngineeringWhichTradeController.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringList1: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList1Page) map {
    x => AnswerRow("constructionalEngineeringList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      ConstructionalEngineeringList1Controller.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringList2: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList2Page) map {
    x => AnswerRow("constructionalEngineeringList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      ConstructionalEngineeringList2Controller.onPageLoad(CheckMode).url)
  }

  def constructionalEngineeringApprentice: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringApprenticePage) map {
    x => AnswerRow("constructionalEngineeringApprentice.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      ConstructionalEngineeringApprenticeController.onPageLoad(CheckMode).url)
  }

  //Healthcare

  def healthcareList2: Option[AnswerRow] = userAnswers.get(HealthcareList2Page) map {
    x => AnswerRow("healthcareList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      HealthcareList2Controller.onPageLoad(CheckMode).url)
  }

  def healthcareList1: Option[AnswerRow] = userAnswers.get(HealthcareList1Page) map {
    x => AnswerRow("healthcareList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      HealthcareList1Controller.onPageLoad(CheckMode).url)
  }

  def ambulanceStaff: Option[AnswerRow] = userAnswers.get(AmbulanceStaffPage) map {
    x => AnswerRow("ambulanceStaff.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      AmbulanceStaffController.onPageLoad(CheckMode).url)
  }

  //Transport

  def garageHandOrCleaner: Option[AnswerRow] = userAnswers.get(GarageHandOrCleanerPage) map {
    x => AnswerRow("garageHandOrCleaner.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      GarageHandOrCleanerController.onPageLoad(CheckMode).url)
  }

  def whichRailwayTrade: Option[AnswerRow] = userAnswers.get(WhichRailwayTradePage) map {
    x => AnswerRow("whichRailwayTrade.checkYourAnswersLabel", s"whichRailwayTrade.$x", true,
      WhichRailwayTradeController.onPageLoad(CheckMode).url
    )
  }

  def airlineJobListed: Option[AnswerRow] = userAnswers.get(AirlineJobListPage) map {
    x => AnswerRow("airlineJobListed.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      AirlineJobListController.onPageLoad(CheckMode).url
    )
  }

  def typeOfTransport: Option[AnswerRow] = userAnswers.get(TypeOfTransportPage) map {
    x => AnswerRow("typeOfTransport.checkYourAnswersLabel", s"typeOfTransport.$x", true,
      TypeOfTransportController.onPageLoad(CheckMode).url)
  }

  def transportCarpenter: Option[AnswerRow] = userAnswers.get(TransportCarpenterPage) map {
    x => AnswerRow("transportCarpenter.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      TransportCarpenterController.onPageLoad(CheckMode).url)
  }

  def transportVehicleTrade: Option[AnswerRow] = userAnswers.get(TransportVehicleTradePage) map {
    x => AnswerRow("transportVehicleTrade.checkYourAnswersLabel", s"transportVehicleTrade.$x", true,
      TransportVehicleTradeController.onPageLoad(CheckMode).url)
  }

  //Manufacturing

  def aluminiumOccupationList2: Option[AnswerRow] = userAnswers.get(AluminiumOccupationList2Page) map {
    x => AnswerRow("aluminiumOccupationList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      AluminiumOccupationList2Controller.onPageLoad(CheckMode).url)
  }

  def typeOfManufacturing: Option[AnswerRow] = userAnswers.get(TypeOfManufacturingPage) map {
    x => AnswerRow("typeOfManufacturing.checkYourAnswersLabel", s"typeOfManufacturing.$x", true,
      TypeOfManufacturingController.onPageLoad(CheckMode).url)
  }

  def aluminiumApprentice: Option[AnswerRow] = userAnswers.get(AluminiumApprenticePage) map {
    x => AnswerRow("aluminiumApprentice.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      AluminiumApprenticeController.onPageLoad(CheckMode).url)
  }

  def aluminiumOccupationList1: Option[AnswerRow] = userAnswers.get(AluminiumOccupationList1Page) map {
    x => AnswerRow("aluminiumOccupationList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      AluminiumOccupationList1Controller.onPageLoad(CheckMode).url)
  }

  def ironSteelOccupationList: Option[AnswerRow] = userAnswers.get(IronSteelOccupationListPage) map {
    x => AnswerRow("ironSteelOccupationList.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      IronSteelOccupationListController.onPageLoad(CheckMode).url)
  }

  def woodFurnitureOccupationList2: Option[AnswerRow] = userAnswers.get(WoodFurnitureOccupationList2Page) map {
    x => AnswerRow("woodFurnitureOccupationList2.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      WoodFurnitureOccupationList2Controller.onPageLoad(CheckMode).url)
  }

  def ironSteelOccupation: Option[AnswerRow] = userAnswers.get(IronSteelOccupationPage) map {
    x => AnswerRow("ironSteelOccupation.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      IronSteelOccupationController.onPageLoad(CheckMode).url)
  }

  def woodFurnitureOccupationList1: Option[AnswerRow] = userAnswers.get(WoodFurnitureOccupationList1Page) map {
    x => AnswerRow("woodFurnitureOccupationList1.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      WoodFurnitureOccupationList1Controller.onPageLoad(CheckMode).url)
  }

  def woodFurnitureOccupationList3: Option[AnswerRow] = userAnswers.get(WoodFurnitureOccupationList3Page) map {
    x => AnswerRow("woodFurnitureOccupationList3.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      WoodFurnitureOccupationList3Controller.onPageLoad(CheckMode).url)
  }

  //Police

  def specialConstable: Option[AnswerRow] = userAnswers.get(SpecialConstablePage) map {
    x => AnswerRow("specialConstable.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      SpecialConstableController.onPageLoad(CheckMode).url)
  }

  def policeOccupationList: Option[AnswerRow] = userAnswers.get(PoliceOccupationListPage) map {
    x => AnswerRow("policeOccupationList.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      PoliceOccupationListController.onPageLoad(CheckMode).url)
  }

  //FoodCatering

  def cateringStaffNHS: Option[AnswerRow] = userAnswers.get(CateringStaffNHSPage) map {
    x => AnswerRow("cateringStaffNHS.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true,
      CateringStaffNHSController.onPageLoad(CheckMode).url)
  }

}
