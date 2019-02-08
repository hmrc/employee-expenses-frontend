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
import models.AncillaryEngineeringWhichTrade._
import models.TypeOfEngineering._
import models._
import org.scalatest.prop.PropertyChecks
import pages._
import pages.engineering._

class EngineeringNavigatorSpec extends SpecBase with PropertyChecks {

  val navigator = new EngineeringNavigator

  "Engineering Navigator" when {
    "in Normal mode" must {

      "from TypeOfEngineering" must {

        "go to ConstructionalEngineeringList1 when ConstructionalEngineering is selected" in {
          val answers = emptyUserAnswers.set(TypeOfEngineeringPage, ConstructionalEngineering).success.value

          navigator.nextPage(TypeOfEngineeringPage, NormalMode)(answers) mustBe
            controllers.engineering.routes.ConstructionalEngineeringList1Controller.onPageLoad(NormalMode)
        }

        "go to AncillaryEngineeringWhichTrade when AncillaryEngineering is selected " in {
          val answers = emptyUserAnswers.set(TypeOfEngineeringPage, TradesRelatingToEngineering).success.value

          navigator.nextPage(TypeOfEngineeringPage, NormalMode)(answers) mustBe
            controllers.engineering.routes.AncillaryEngineeringWhichTradeController.onPageLoad(NormalMode)
        }

        "go to FactoryEngineeringList1 when FactoryOrWorkshopEngineering is selected " in {
          val answers = emptyUserAnswers.set(TypeOfEngineeringPage, FactoryOrWorkshopEngineering).success.value

          navigator.nextPage(TypeOfEngineeringPage, NormalMode)(answers) mustBe
            controllers.engineering.routes.FactoryEngineeringList1Controller.onPageLoad(NormalMode)
        }

        "go to EmployerContribution when NoneOfTheAbove is selected " in {
          val answers = emptyUserAnswers.set(TypeOfEngineeringPage, TypeOfEngineering.NoneOfTheAbove).success.value

          navigator.nextPage(TypeOfEngineeringPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

      //Constructional Engineering

      "from ConstructionalEngineeringList1" must {

        "go to EmployerContribution when Yes is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringList1Page, true).success.value

          navigator.nextPage(ConstructionalEngineeringList1Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to ConstructionalEngineeringList2 when No is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringList1Page, false).success.value

          navigator.nextPage(ConstructionalEngineeringList1Page, NormalMode)(answers) mustBe
            controllers.engineering.routes.ConstructionalEngineeringList2Controller.onPageLoad(NormalMode)
        }
      }

      "from ConstructionalEngineeringList2" must {

        "go to EmployerContribution when Yes is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringList2Page, true).success.value

          navigator.nextPage(ConstructionalEngineeringList2Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to ConstructionalEngineeringList3 when No is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringList2Page, false).success.value

          navigator.nextPage(ConstructionalEngineeringList2Page, NormalMode)(answers) mustBe
            controllers.engineering.routes.ConstructionalEngineeringList3Controller.onPageLoad(NormalMode)
        }
      }

      "from ConstructionalEngineeringList3" must {

        "go to EmployerContribution when Yes is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringList3Page, true).success.value

          navigator.nextPage(ConstructionalEngineeringList3Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to ConstructionalEngineeringApprentice when No is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringList3Page, false).success.value

          navigator.nextPage(ConstructionalEngineeringList3Page, NormalMode)(answers) mustBe
            controllers.engineering.routes.ConstructionalEngineeringApprenticeController.onPageLoad(NormalMode)
        }

        "go to SessionExpired when no data is available" in {
          navigator.nextPage(ConstructionalEngineeringList3Page, NormalMode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }
      }

      "from ConstructionalEngineeringApprentice" must {

        "go to EmployerContribution when Yes is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringApprenticePage, true).success.value

          navigator.nextPage(ConstructionalEngineeringApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContribution when No is selected" in {
          val answers = emptyUserAnswers.set(ConstructionalEngineeringApprenticePage, false).success.value

          navigator.nextPage(ConstructionalEngineeringApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

      //Ancillary Engineering

      "from AncillaryEngineeringWhichTrade" must {
          for (trade <- AncillaryEngineeringWhichTrade.values) {
            s"goto EmployerContribution when '$trade' selected" in {
              val answers = emptyUserAnswers.set(AncillaryEngineeringWhichTradePage, trade).success.value
              navigator.nextPage(AncillaryEngineeringWhichTradePage, NormalMode)(answers) mustBe
                controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
            }
          }
        }

      //Factory Engineering

      "from FactoryEngineeringList1" must {

        "go to EmployerContribution when yes is selected" in {
          val answers = emptyUserAnswers.set(FactoryEngineeringList1Page, true).success.value

          navigator.nextPage(FactoryEngineeringList1Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to FactoryEngineeringList2 when no is selected" in {
          val answers = emptyUserAnswers.set(FactoryEngineeringList1Page, false).success.value

          navigator.nextPage(FactoryEngineeringList1Page, NormalMode)(answers) mustBe
            controllers.engineering.routes.FactoryEngineeringList2Controller.onPageLoad(NormalMode)
        }
      }

      "from FactoryEngineeringList2" must {

        "go to EmployerContribution when yes is selected" in {
          val answers = emptyUserAnswers.set(FactoryEngineeringList2Page, true).success.value

          navigator.nextPage(FactoryEngineeringList2Page, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to FactoryEngineeringApprentice when no is selected" in {
          val answers = emptyUserAnswers.set(FactoryEngineeringList2Page, false).success.value

          navigator.nextPage(FactoryEngineeringList2Page, NormalMode)(answers) mustBe
            controllers.engineering.routes.FactoryEngineeringApprenticeController.onPageLoad(NormalMode)
        }
      }

      "from FactoryEngineeringApprentice" must {

        "go to EmployerContribution when yes is selected" in {
          val answers = emptyUserAnswers.set(FactoryEngineeringApprenticePage, true).success.value

          navigator.nextPage(FactoryEngineeringApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContribution when no is selected" in {
          val answers = emptyUserAnswers.set(FactoryEngineeringApprenticePage, false).success.value

          navigator.nextPage(FactoryEngineeringApprenticePage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }
    }

    /*    "in Check mode" must {

        }*/
  }
}
