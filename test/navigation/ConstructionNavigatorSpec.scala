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
import models.NormalMode
import pages.construction._
import controllers.construction.routes
import play.api.mvc.Call

class ConstructionNavigatorSpec extends SpecBase {

  val navigator = new ConstructionNavigator


  "Construction Navigator" when {
    "in Normal mode" must {

      "from JoinerCarpenter" must {

        "go to EmployerContribution when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(JoinerCarpenterPage, true).success.value

          navigator.nextPage(JoinerCarpenterPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to StoneMason when 'no' is selected" in {
          val answers = emptyUserAnswers.set(JoinerCarpenterPage, false).success.value

          navigator.nextPage(JoinerCarpenterPage, NormalMode)(answers) mustBe
            routes.StoneMasonController.onPageLoad(NormalMode)
        }

        "go to SessionExpired when no data is available" in {
          val answers = emptyUserAnswers

          navigator.nextPage(JoinerCarpenterPage, NormalMode)(answers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }
      }

      "from StoneMason" must {

        "go to EmployerContribution when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(StoneMasonPage, true).success.value

          navigator.nextPage(StoneMasonPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to ConstructionOccupationList1 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(StoneMasonPage, false).success.value

          navigator.nextPage(StoneMasonPage, NormalMode)(answers) mustBe
            routes.ConstructionOccupationList1Controller.onPageLoad(NormalMode)
        }

        "go to SessionExpired when no data is available" in {
          val answers = emptyUserAnswers

          navigator.nextPage(StoneMasonPage, NormalMode)(answers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }
      }

      "from ConstructionOccupationList1" must {

        "go to EmployerContribution when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList1Page, true).success.value

          navigator.nextPage(ConstructionOccupationList1Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to ConstructionOccupationList2 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList1Page, false).success.value

          navigator.nextPage(ConstructionOccupationList1Page, NormalMode)(answers) mustBe
            routes.ConstructionOccupationList2Controller.onPageLoad(NormalMode)

        }

        "go to SessionExpired when no data is available" in {
          val answers = emptyUserAnswers

          navigator.nextPage(ConstructionOccupationList1Page, NormalMode)(answers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

      }

      "from ConstructionOccupationList2" must {

        "go to EmployerContribution when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList2Page, true).success.value

          navigator.nextPage(ConstructionOccupationList2Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to BuildingMaterials when 'No' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList2Page, false).success.value

          navigator.nextPage(ConstructionOccupationList2Page, NormalMode)(answers) mustBe
            routes.BuildingMaterialsController.onPageLoad(NormalMode)

        }

        "go to SessionExpired when no data is available" in {
          val answers = emptyUserAnswers

          navigator.nextPage(ConstructionOccupationList2Page, NormalMode)(answers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

      }
      "from BuildingMaterials" must {

        "go to EmployerContributions when 'Yes' is Selected" in {
        val answers = emptyUserAnswers.set(BuildingMaterialsPage, true).success.value

        navigator.nextPage(BuildingMaterialsPage, NormalMode)(answers) mustBe
          controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
      }
        "go to EmployerContributions when 'No' is Selected" in {
          val answers = emptyUserAnswers.set(BuildingMaterialsPage, false).success.value

          navigator.nextPage(BuildingMaterialsPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
        "go to SessionExpired when no data is available" in {
          val answers = emptyUserAnswers

          navigator.nextPage(BuildingMaterialsPage, NormalMode)(answers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }
}
