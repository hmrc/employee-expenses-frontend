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
import models.TypeOfEngineering._
import models._
import org.scalatest.prop.PropertyChecks
import pages._
import pages.engineering.ConstructionalEngineeringList1Page

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
          val answers = emptyUserAnswers.set(TypeOfEngineeringPage, NoneOfTheAbove).success.value

          navigator.nextPage(TypeOfEngineeringPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

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

      "in Check mode" must {

      }

    }
  }

}
