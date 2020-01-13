/*
 * Copyright 2020 HM Revenue & Customs
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
import models.AncillaryEngineeringWhichTrade._
import models.TypeOfEngineering._
import models._
import org.scalatestplus.mockito.MockitoSugar
import pages.Page
import pages.engineering._

class EngineeringNavigatorSpec extends SpecBase with MockitoSugar {
  private val modes = Seq(NormalMode, CheckMode)
  private val navigator = new EngineeringNavigator

  "EngineeringNavigator" when {
    for (mode <- modes) {
      s"in $mode" must {

        "from TypeOfEngineering" must {

          "go to ConstructionalEngineeringApprentice when ConstructionalEngineering is selected" in {
            val answers = emptyUserAnswers.set(TypeOfEngineeringPage, ConstructionalEngineering).success.value

            navigator.nextPage(TypeOfEngineeringPage, mode)(answers) mustBe
              controllers.engineering.routes.ConstructionalEngineeringApprenticeController.onPageLoad(mode)
          }

          "go to AncillaryEngineeringWhichTrade when AncillaryEngineering is selected " in {
            val answers = emptyUserAnswers.set(TypeOfEngineeringPage, TradesRelatingToEngineering).success.value

            navigator.nextPage(TypeOfEngineeringPage, mode)(answers) mustBe
              controllers.engineering.routes.AncillaryEngineeringWhichTradeController.onPageLoad(mode)
          }

          "go to FactoryEngineeringAprentice when FactoryOrWorkshopEngineering is selected " in {
            val answers = emptyUserAnswers.set(TypeOfEngineeringPage, FactoryOrWorkshopEngineering).success.value

            navigator.nextPage(TypeOfEngineeringPage, mode)(answers) mustBe
              controllers.engineering.routes.FactoryEngineeringApprenticeController.onPageLoad(mode)
          }

          "go to EmployerContribution when NoneOfTheAbove is selected " in {
            val answers = emptyUserAnswers.set(TypeOfEngineeringPage, TypeOfEngineering.NoneOfTheAbove).success.value

            navigator.nextPage(TypeOfEngineeringPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to SessionExpired when no TypeOfEngineeringPage in UserAnswers" in {
            navigator.nextPage(TypeOfEngineeringPage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        //Constructional Engineering

        "from ConstructionalEngineeringApprentice" must {

          "go to EmployerContribution when Yes is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringApprenticePage, true).success.value

            navigator.nextPage(ConstructionalEngineeringApprenticePage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to ConstructionalEngineeringList1 when No is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringApprenticePage, false).success.value

            navigator.nextPage(ConstructionalEngineeringApprenticePage, mode)(answers) mustBe
              controllers.engineering.routes.ConstructionalEngineeringList1Controller.onPageLoad(mode)
          }

          "go to SessionExpired when no ConstructionalEngineeringApprenticePage in UserAnswers" in {
            navigator.nextPage(ConstructionalEngineeringApprenticePage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "from ConstructionalEngineeringList1" must {

          "go to EmployerContribution when Yes is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringList1Page, true).success.value

            navigator.nextPage(ConstructionalEngineeringList1Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to ConstructionalEngineeringList2 when No is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringList1Page, false).success.value

            navigator.nextPage(ConstructionalEngineeringList1Page, mode)(answers) mustBe
              controllers.engineering.routes.ConstructionalEngineeringList2Controller.onPageLoad(mode)
          }

          "go to SessionExpired when no ConstructionalEngineeringList1Page in UserAnswers" in {
            navigator.nextPage(ConstructionalEngineeringList1Page, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "from ConstructionalEngineeringList2" must {

          "go to EmployerContribution when Yes is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringList2Page, true).success.value

            navigator.nextPage(ConstructionalEngineeringList2Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to ConstructionalEngineeringList3 when No is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringList2Page, false).success.value

            navigator.nextPage(ConstructionalEngineeringList2Page, mode)(answers) mustBe
              controllers.engineering.routes.ConstructionalEngineeringList3Controller.onPageLoad(mode)
          }

          "go to SessionExpired when no ConstructionalEngineeringList2Page in UserAnswers" in {
            navigator.nextPage(ConstructionalEngineeringList2Page, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "from ConstructionalEngineeringList3" must {

          "go to EmployerContribution when Yes is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringList3Page, true).success.value

            navigator.nextPage(ConstructionalEngineeringList3Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to ConstructionalEngineeringApprentice when No is selected" in {
            val answers = emptyUserAnswers.set(ConstructionalEngineeringList3Page, false).success.value

            navigator.nextPage(ConstructionalEngineeringList3Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }

        //Ancillary Engineering

        "from AncillaryEngineeringWhichTrade" must {
          for (trade <- AncillaryEngineeringWhichTrade.values) {
            s"goto EmployerContribution when '$trade' selected" in {
              val answers = emptyUserAnswers.set(AncillaryEngineeringWhichTradePage, trade).success.value
              navigator.nextPage(AncillaryEngineeringWhichTradePage, mode)(answers) mustBe
                controllers.routes.EmployerContributionController.onPageLoad(mode)
            }
          }

          "go to SessionExpired when no AncillaryEngineeringWhichTradePage in UserAnswers" in {
            navigator.nextPage(AncillaryEngineeringWhichTradePage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        //Factory Engineering

        "from FactoryEngineeringList1" must {

          "go to EmployerContribution when yes is selected" in {
            val answers = emptyUserAnswers.set(FactoryEngineeringList1Page, true).success.value

            navigator.nextPage(FactoryEngineeringList1Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to FactoryEngineeringList2 when no is selected" in {
            val answers = emptyUserAnswers.set(FactoryEngineeringList1Page, false).success.value

            navigator.nextPage(FactoryEngineeringList1Page, mode)(answers) mustBe
              controllers.engineering.routes.FactoryEngineeringList2Controller.onPageLoad(mode)
          }

          "go to SessionExpired when no FactoryEngineeringList1Page in UserAnswers" in {
            navigator.nextPage(FactoryEngineeringList1Page, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "from FactoryEngineeringList2" must {

          "go to EmployerContribution when yes is selected" in {
            val answers = emptyUserAnswers.set(FactoryEngineeringList2Page, true).success.value

            navigator.nextPage(FactoryEngineeringList2Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContribution when no is selected" in {
            val answers = emptyUserAnswers.set(FactoryEngineeringList2Page, false).success.value

            navigator.nextPage(FactoryEngineeringList2Page, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }

        "from FactoryEngineeringApprentice" must {

          "go to EmployerContribution when yes is selected" in {
            val answers = emptyUserAnswers.set(FactoryEngineeringApprenticePage, true).success.value

            navigator.nextPage(FactoryEngineeringApprenticePage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to factory-engineering-list-1 when no is selected" in {
            val answers = emptyUserAnswers.set(FactoryEngineeringApprenticePage, false).success.value

            navigator.nextPage(FactoryEngineeringApprenticePage, mode)(answers) mustBe
              controllers.engineering.routes.FactoryEngineeringList1Controller.onPageLoad(mode)
          }

          "go to SessionExpired when no FactoryEngineeringApprenticePage in UserAnswers" in {
            navigator.nextPage(FactoryEngineeringApprenticePage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "go to SessionExpired when not one of the EngineeringPage" in {
          navigator.nextPage(mock[Page], mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }
}
