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

package navigation

import base.SpecBase
import controllers.manufacturing.routes
import models.TypeOfManufacturing._
import models._
import pages.manufacturing._

class ManufacturingNavigatorSpec extends SpecBase {

  val navigator = new ManufacturingNavigator

  "Navigator" when {

    "in Normal mode" must {

      "on TypeOfManufacturingController" must {
        "go to AluminiumOccupationList1 when 'Aluminium' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, Aluminium).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            routes.AluminiumOccupationList1Controller.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController when 'BrassAndCopper' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, BrassCopper).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController when 'Glass' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, Glass).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to IronMiningController when 'IronSteel' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, IronSteel).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            routes.IronMiningController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController when 'PreciousMetals' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, PreciousMetals).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to WoodFurnitureOccupationList1Controller when 'WoodFurniture' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, WoodFurniture).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            routes.WoodFurnitureOccupationList1Controller.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController when 'NoneOfTheAbove' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, NoneOfAbove).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to Session Expired when no option available" in {
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }
      }

      "in Aluminium journey" must {
        "go to EmployerContributionController from AluminiumOccupatinoList1 when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(AluminiumOccupationList1Page, true).success.value
          navigator.nextPage(AluminiumOccupationList1Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to AluminiumOccupationList2 from AluminiumOccupatinoList1 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(AluminiumOccupationList1Page, false).success.value
          navigator.nextPage(AluminiumOccupationList1Page, NormalMode)(answers) mustBe
            controllers.manufacturing.routes.AluminiumOccupationList2Controller.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from AluminiumOccupatinoList2 when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(AluminiumOccupationList2Page, true).success.value
          navigator.nextPage(AluminiumOccupationList2Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to AluminiumOccupationList3 from AluminiumOccupatinoList2 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(AluminiumOccupationList2Page, false).success.value
          navigator.nextPage(AluminiumOccupationList2Page, NormalMode)(answers) mustBe
            controllers.manufacturing.routes.AluminiumOccupationList3Controller.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from AluminiumOccupationList3 when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(AluminiumOccupationList3Page, true).success.value
          navigator.nextPage(AluminiumOccupationList3Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to ManufacturingApprentice from AluminiumOccupationList3 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(AluminiumOccupationList3Page, false).success.value
          navigator.nextPage(AluminiumOccupationList3Page, NormalMode)(answers) mustBe
            controllers.manufacturing.routes.ManufacturingApprenticeController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from ManufacturingApprentice when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(ManufacturingApprenticePage, true).success.value
          navigator.nextPage(ManufacturingApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from ManufacturingApprentice when 'No' is selected" in {
          val answers = emptyUserAnswers.set(ManufacturingApprenticePage, false).success.value
          navigator.nextPage(ManufacturingApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

      "in Iron and Steel journey" must {
        "go to IronMiningListController from IronMiningController when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(IronMiningPage, true).success.value
          navigator.nextPage(IronMiningPage, NormalMode)(answers) mustBe
            routes.IronMiningListController.onPageLoad(NormalMode)
        }

        "go to IronSteelOccupationListController from IronMiningController when 'No' is selected" in {
          val answers = emptyUserAnswers.set(IronMiningPage, false).success.value
          navigator.nextPage(IronMiningPage, NormalMode)(answers) mustBe
            routes.IronSteelOccupationListController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from IronMiningListController when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(IronMiningListPage, true).success.value
          navigator.nextPage(IronMiningListPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from IronMiningListController when 'No' is selected" in {
          val answers = emptyUserAnswers.set(IronMiningListPage, false).success.value
          navigator.nextPage(IronMiningListPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from IronSteelOccupationListController when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(IronSteelOccupationListPage, true).success.value
          navigator.nextPage(IronSteelOccupationListPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to ManufacturingApprentice from IronSteelOccupationListController when 'No' is selected" in {
          val answers = emptyUserAnswers.set(IronSteelOccupationListPage, false).success.value
          navigator.nextPage(IronSteelOccupationListPage, NormalMode)(answers) mustBe
            routes.ManufacturingApprenticeController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from ManufacturingApprentice when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(ManufacturingApprenticePage, true).success.value
          navigator.nextPage(ManufacturingApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from ManufacturingApprentice when 'No' is selected" in {
          val answers = emptyUserAnswers.set(ManufacturingApprenticePage, false).success.value
          navigator.nextPage(ManufacturingApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

      "in Wood Furniture journey" must {
        "go to EmployerContributionController from WoodFurnitureOccupationList1 when 'Yes' is selected" in {
        val answers = emptyUserAnswers.set(WoodFurnitureOccupationList1Page, true).success.value
          navigator.nextPage(WoodFurnitureOccupationList1Page, NormalMode)(answers) mustBe
          controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to WoodFurnitureOccupationList2 from WoodFurnitureOccupationList1 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(WoodFurnitureOccupationList1Page, false).success.value
          navigator.nextPage(WoodFurnitureOccupationList1Page, NormalMode)(answers) mustBe
            routes.WoodFurnitureOccupationList2Controller.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from WoodFurnitureOccupationList2 when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(WoodFurnitureOccupationList2Page, true).success.value
          navigator.nextPage(WoodFurnitureOccupationList2Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to WoodFurnitureOccupationList3 from WoodFurnitureOccupationList2 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(WoodFurnitureOccupationList2Page, false).success.value
          navigator.nextPage(WoodFurnitureOccupationList2Page, NormalMode)(answers) mustBe
            routes.WoodFurnitureOccupationList3Controller.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from WoodFurnitureOccupationList3 when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(WoodFurnitureOccupationList3Page, true).success.value
          navigator.nextPage(WoodFurnitureOccupationList3Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContributionController from WoodFurnitureOccupationList3 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(WoodFurnitureOccupationList3Page, false).success.value
          navigator.nextPage(WoodFurnitureOccupationList3Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }


      }
    }

    "in Check mode" must {


    }
  }
}
