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
import controllers.construction.routes
import models.{CheckMode, NormalMode}
import pages.construction._

class ConstructionNavigatorSpec extends SpecBase {
  private val modes = Seq(NormalMode, CheckMode)
  private val navigator = new ConstructionNavigator

  "Construction Navigator" when {
    for (mode <- modes) {
      s"in $mode" must {

        //from JoinerCarpenter

        "go to EmployerContribution from JoinerCarpenter when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(JoinerCarpenterPage, true).success.value

          navigator.nextPage(JoinerCarpenterPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to StoneMason from JoinerCarpenter when 'no' is selected" in {
          val answers = emptyUserAnswers.set(JoinerCarpenterPage, false).success.value

          navigator.nextPage(JoinerCarpenterPage, mode)(answers) mustBe
            routes.StoneMasonController.onPageLoad(mode)
        }

        "go to SessionExpired from JoinerCarpenter when no data is available" in {
          navigator.nextPage(JoinerCarpenterPage, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }


        //from StoneMason

        "go to EmployerContribution from StoneMason when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(StoneMasonPage, true).success.value

          navigator.nextPage(StoneMasonPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to ConstructionOccupationList1 from StoneMason  when 'No' is selected" in {
          val answers = emptyUserAnswers.set(StoneMasonPage, false).success.value

          navigator.nextPage(StoneMasonPage, mode)(answers) mustBe
            routes.ConstructionOccupationList1Controller.onPageLoad(mode)
        }

        "go to SessionExpired from StoneMason  when no data is available" in {
          navigator.nextPage(StoneMasonPage, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }


        //from ConstructionOccupationList1

        "go to EmployerContribution from ConstructionOccupationList1 when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList1Page, true).success.value

          navigator.nextPage(ConstructionOccupationList1Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to ConstructionOccupationList2 from ConstructionOccupationList1 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList1Page, false).success.value

          navigator.nextPage(ConstructionOccupationList1Page, mode)(answers) mustBe
            routes.ConstructionOccupationList2Controller.onPageLoad(mode)

        }

        "go to SessionExpired  from ConstructionOccupationList1when no data is available" in {
          navigator.nextPage(ConstructionOccupationList1Page, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

        //from ConstructionOccupationList2

        "go to EmployerContribution from ConstructionOccupationList2 when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList2Page, true).success.value

          navigator.nextPage(ConstructionOccupationList2Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to BuildingMaterials from ConstructionOccupationList2 when 'No' is selected" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationList2Page, false).success.value

          navigator.nextPage(ConstructionOccupationList2Page, mode)(answers) mustBe
            routes.BuildingMaterialsController.onPageLoad(mode)

        }

        "go to SessionExpired from ConstructionOccupationList2 when no data is available" in {
          navigator.nextPage(ConstructionOccupationList2Page, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

        //from BuildingMaterials

        "go to EmployerContributions from BuildingMaterials when 'Yes' is Selected" in {
          val answers = emptyUserAnswers.set(BuildingMaterialsPage, true).success.value

          navigator.nextPage(BuildingMaterialsPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to EmployerContributions from BuildingMaterials when 'No' is Selected" in {
          val answers = emptyUserAnswers.set(BuildingMaterialsPage, false).success.value

          navigator.nextPage(BuildingMaterialsPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to SessionExpired from BuildingMaterials when no data is available" in {
          navigator.nextPage(BuildingMaterialsPage, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }
}
