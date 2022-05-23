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
import controllers.routes.SessionExpiredController
import models._
import org.scalatestplus.mockito.MockitoSugar
import pages.Page
import pages.shipyard._


class ShipyardNavigatorSpec extends SpecBase with MockitoSugar {
  private val modes = Seq(NormalMode, CheckMode)
  private val navigator = new ShipyardNavigator

  "ShipyardNavigator" when {

    for (mode <- modes) {
      s"in $mode" must {

        "go to SessionExpired when not a shipyard page" in {
          navigator.nextPage(mock[Page], mode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad()
        }

        "go to EmployerContributionPage when Appprentice 'yes' is selected" in {
          val answers = emptyUserAnswers.set(ShipyardApprenticeStorekeeperPage, true).success.value

          navigator.nextPage(ShipyardApprenticeStorekeeperPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to ShipyardOccupationList1Page when Appprentice 'no' selected" in {
          val answers = emptyUserAnswers.set(ShipyardApprenticeStorekeeperPage, false).success.value

          navigator.nextPage(ShipyardApprenticeStorekeeperPage, mode)(answers) mustBe
            controllers.shipyard.routes.ShipyardOccupationList1Controller.onPageLoad(mode)
        }

        "go to SessionExpired when Appprentice None" in {
          navigator.nextPage(ShipyardApprenticeStorekeeperPage, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

        "go to EmployerContributionPage when ShipyardOccupationList1 'Yes selected" in {
          val answers = emptyUserAnswers.set(ShipyardOccupationList1Page, true).success.value

          navigator.nextPage(ShipyardOccupationList1Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to ShipyardOccupationList2Page when ShipyardOccupationList1 'No selected" in {
          val answers = emptyUserAnswers.set(ShipyardOccupationList1Page, false).success.value

          navigator.nextPage(ShipyardOccupationList1Page, mode)(answers) mustBe
            controllers.shipyard.routes.ShipyardOccupationList2Controller.onPageLoad(mode)
        }

        "go to SessionExpired when ShipyardOccupationList1 None" in {
          navigator.nextPage(ShipyardOccupationList1Page, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

        "go to EmployerContributionPage when ShipyardOccupationList2 'Yes selected" in {
          val answers = emptyUserAnswers.set(ShipyardOccupationList2Page, true).success.value

          navigator.nextPage(ShipyardOccupationList2Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to LabourerPage when ShipyardOccupationList2 'No selected" in {
          val answers = emptyUserAnswers.set(ShipyardOccupationList2Page, false).success.value

          navigator.nextPage(ShipyardOccupationList2Page, mode)(answers) mustBe
            controllers.shipyard.routes.LabourerController.onPageLoad(mode)
        }

        "go to SessionExpired when ShipyardOccupationList2 None" in {
          navigator.nextPage(ShipyardOccupationList2Page, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

        "go to EmployerContributionPage when Labourer 'Yes selected" in {
          val answers = emptyUserAnswers.set(LabourerPage, true).success.value

          navigator.nextPage(LabourerPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to EmployerContributionPage when Labourer 'No selected" in {
          val answers = emptyUserAnswers.set(LabourerPage, false).success.value

          navigator.nextPage(LabourerPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }
      }
    }
  }
}