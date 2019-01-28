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
import models._
import models.TypeOfManufacturing._
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

        "go to IronSteelOccupationListController when 'IronSteel' is selected" in {
          val answers = emptyUserAnswers.set(TypeOfManufacturingPage, IronSteel).success.value
          navigator.nextPage(TypeOfManufacturingPage, NormalMode)(answers) mustBe
            routes.IronSteelOccupationListController.onPageLoad(NormalMode)
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
    }


    "in Check mode" must {


    }
  }
}
