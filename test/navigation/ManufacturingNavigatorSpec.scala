/*
 * Copyright 2022 HM Revenue & Customs
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
import org.scalatestplus.mockito.MockitoSugar
import pages.Page
import pages.manufacturing._

class ManufacturingNavigatorSpec extends SpecBase with MockitoSugar {

  private val navigator = new ManufacturingNavigator
  private val modes = Seq(NormalMode, CheckMode)


  "ManufacturingNavigator" when {

    for (mode <- modes) {
      s"in $mode" must {

        "go to SessionExpired when not a ManufacturingPage" in {
          navigator.nextPage(mock[Page], mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

        "on TypeOfManufacturingController" must {
          "go to AluminiumApprentice when 'Aluminium' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfManufacturingPage, Aluminium).success.value
            navigator.nextPage(TypeOfManufacturingPage, mode)(answers) mustBe
              routes.AluminiumApprenticeController.onPageLoad(mode)
          }

          "go to EmployerContributionController when 'BrassAndCopper' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfManufacturingPage, BrassCopper).success.value
            navigator.nextPage(TypeOfManufacturingPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContributionController when 'Glass' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfManufacturingPage, Glass).success.value
            navigator.nextPage(TypeOfManufacturingPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to IronApprentice when 'IronSteel' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfManufacturingPage, IronSteel).success.value
            navigator.nextPage(TypeOfManufacturingPage, mode)(answers) mustBe
              routes.IronApprenticeController.onPageLoad(mode)
          }

          "go to EmployerContributionController when 'PreciousMetals' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfManufacturingPage, PreciousMetals).success.value
            navigator.nextPage(TypeOfManufacturingPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to WoodFurnitureOccupationList1Controller when 'WoodFurniture' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfManufacturingPage, WoodFurniture).success.value
            navigator.nextPage(TypeOfManufacturingPage, mode)(answers) mustBe
              routes.WoodFurnitureOccupationList1Controller.onPageLoad(mode)
          }

          "go to EmployerContributionController when 'NoneOfTheAbove' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfManufacturingPage, NoneOfAbove).success.value
            navigator.nextPage(TypeOfManufacturingPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to Session Expired when no option available" in {
            navigator.nextPage(TypeOfManufacturingPage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "in Aluminium journey" must {

          "go to EmployerContributionController from AluminiumApprentice when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumApprenticePage, true).success.value
            navigator.nextPage(AluminiumApprenticePage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to AluminiumOccupatinoList1 from AluminiumApprentice when 'No' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumApprenticePage, false).success.value
            navigator.nextPage(AluminiumApprenticePage, mode)(answers) mustBe
              routes.AluminiumOccupationList1Controller.onPageLoad(mode)
          }

          "go to SessionExpired from AluminiumApprentice when AluminiumApprenticePage when empty user answers" in {
            navigator.nextPage(AluminiumApprenticePage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }

          "go to EmployerContributionController from AluminiumOccupationList1 when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumOccupationList1Page, true).success.value
            navigator.nextPage(AluminiumOccupationList1Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to AluminiumOccupationList2 from AluminiumOccupationList1 when 'No' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumOccupationList1Page, false).success.value
            navigator.nextPage(AluminiumOccupationList1Page, mode)(answers) mustBe
              controllers.manufacturing.routes.AluminiumOccupationList2Controller.onPageLoad(mode)
          }

          "go to SessionExpired from AluminiumOccupationList1 when empty user answers" in {
            navigator.nextPage(AluminiumOccupationList1Page, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }

          "go to EmployerContributionController from AluminiumOccupatinoList2 when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumOccupationList2Page, true).success.value
            navigator.nextPage(AluminiumOccupationList2Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to AluminiumOccupationList3 from AluminiumOccupatinoList2 when 'No' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumOccupationList2Page, false).success.value
            navigator.nextPage(AluminiumOccupationList2Page, mode)(answers) mustBe
              controllers.manufacturing.routes.AluminiumOccupationList3Controller.onPageLoad(mode)
          }

          "go to SessionExpired from AluminiumOccupationList2 when empty user answers" in {
            navigator.nextPage(AluminiumOccupationList2Page, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }

          "go to EmployerContributionController from AluminiumOccupationList3 when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumOccupationList3Page, true).success.value
            navigator.nextPage(AluminiumOccupationList3Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContributionController from AluminiumOccupationList3 when 'No' is selected" in {
            val answers = emptyUserAnswers.set(AluminiumOccupationList3Page, false).success.value
            navigator.nextPage(AluminiumOccupationList3Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }

        "in Iron and Steel journey" must {
          "go to IronMiningListController from IronMiningController when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(IronMiningPage, true).success.value
            navigator.nextPage(IronMiningPage, mode)(answers) mustBe
              routes.IronMiningListController.onPageLoad(mode)
          }

          "go to IronSteelOccupationListController from IronMiningController when 'No' is selected" in {
            val answers = emptyUserAnswers.set(IronMiningPage, false).success.value
            navigator.nextPage(IronMiningPage, mode)(answers) mustBe
              routes.IronSteelOccupationListController.onPageLoad(mode)
          }

          "go to SessionExpired from IronMiningController when empty user answers" in {
            navigator.nextPage(IronMiningPage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }

          "go to EmployerContributionController from IronMiningListController when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(IronMiningListPage, true).success.value
            navigator.nextPage(IronMiningListPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContributionController from IronMiningListController when 'No' is selected" in {
            val answers = emptyUserAnswers.set(IronMiningListPage, false).success.value
            navigator.nextPage(IronMiningListPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContributionController from IronSteelOccupationListController when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(IronSteelOccupationListPage, true).success.value
            navigator.nextPage(IronSteelOccupationListPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContribution from IronSteelOccupationListController when 'No' is selected" in {
            val answers = emptyUserAnswers.set(IronSteelOccupationListPage, false).success.value
            navigator.nextPage(IronSteelOccupationListPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to IronMining from IronApprentice when 'No' is selected" in {
            val answers = emptyUserAnswers.set(IronApprenticePage, false).success.value
            navigator.nextPage(IronApprenticePage, mode)(answers) mustBe
              routes.IronMiningController.onPageLoad(mode)
          }

          "go to EmployerContribution from IronApprentice when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(IronApprenticePage, true).success.value
            navigator.nextPage(IronApprenticePage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to SessionExpired from IronApprentice when empty user answers" in {
            navigator.nextPage(IronApprenticePage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "in Wood Furniture journey" must {
          "go to EmployerContributionController from WoodFurnitureOccupationList1 when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(WoodFurnitureOccupationList1Page, true).success.value
            navigator.nextPage(WoodFurnitureOccupationList1Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to WoodFurnitureOccupationList2 from WoodFurnitureOccupationList1 when 'No' is selected" in {
            val answers = emptyUserAnswers.set(WoodFurnitureOccupationList1Page, false).success.value
            navigator.nextPage(WoodFurnitureOccupationList1Page, mode)(answers) mustBe
              routes.WoodFurnitureOccupationList2Controller.onPageLoad(mode)
          }

          "go to SessionExpired from WoodFurnitureOccupationList1 when empty user answers" in {
            navigator.nextPage(WoodFurnitureOccupationList1Page, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }

          "go to EmployerContributionController from WoodFurnitureOccupationList2 when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(WoodFurnitureOccupationList2Page, true).success.value
            navigator.nextPage(WoodFurnitureOccupationList2Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to WoodFurnitureOccupationList3 from WoodFurnitureOccupationList2 when 'No' is selected" in {
            val answers = emptyUserAnswers.set(WoodFurnitureOccupationList2Page, false).success.value
            navigator.nextPage(WoodFurnitureOccupationList2Page, mode)(answers) mustBe
              routes.WoodFurnitureOccupationList3Controller.onPageLoad(mode)
          }

          "go to SessionExpired from WoodFurnitureOccupationList2 when empty user answers" in {
            navigator.nextPage(WoodFurnitureOccupationList2Page, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }

          "go to EmployerContributionController from WoodFurnitureOccupationList3 when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(WoodFurnitureOccupationList3Page, true).success.value
            navigator.nextPage(WoodFurnitureOccupationList3Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContributionController from WoodFurnitureOccupationList3 when 'No' is selected" in {
            val answers = emptyUserAnswers.set(WoodFurnitureOccupationList3Page, false).success.value
            navigator.nextPage(WoodFurnitureOccupationList3Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }
      }
    }
  }
}
