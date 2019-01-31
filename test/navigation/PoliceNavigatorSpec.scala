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
import controllers.police.routes
import models.NormalMode
import pages.police._

class PoliceNavigatorSpec extends SpecBase {
  val navigator = new PoliceNavigator

  "Navigator" when {

    "in Normal mode" must {

      "on SpecialConstableController" must {

        "go to CannotClaimExpense when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(SpecialConstablePage, true).success.value
          navigator.nextPage(SpecialConstablePage, NormalMode)(answers) mustBe
            controllers.routes.CannotClaimExpenseController.onPageLoad()
        }

        "go to CommunitySupportOfficer when 'No' is selected" in {
          val answers = emptyUserAnswers.set(SpecialConstablePage, false).success.value
          navigator.nextPage(SpecialConstablePage, NormalMode)(answers) mustBe
            routes.CommunitySupportOfficerController.onPageLoad(NormalMode)
        }
      }

      "on CommunitySupportOfficer" must {

        "go to EmployerContribution when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(CommunitySupportOfficerPage, true).success.value
          navigator.nextPage(CommunitySupportOfficerPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to MetropolitanPolice when 'No' is selected" in {
          val answers = emptyUserAnswers.set(CommunitySupportOfficerPage, false).success.value
          navigator.nextPage(CommunitySupportOfficerPage, NormalMode)(answers) mustBe
            routes.MetropolitanPoliceController.onPageLoad(NormalMode)
        }
      }

      "on MetropolitanPolice" must {

        "go to CannotClaimExpense when 'yes' is selected" in {
          val answers = emptyUserAnswers.set(MetropolitanPolicePage, true).success.value
          navigator.nextPage(MetropolitanPolicePage, NormalMode)(answers) mustBe
            controllers.routes.CannotClaimExpenseController.onPageLoad()

        }

        "go to PoliceOfficer when 'No' is selected" in {
          val answers = emptyUserAnswers.set(MetropolitanPolicePage, false).success.value
          navigator.nextPage(MetropolitanPolicePage, NormalMode)(answers) mustBe
            routes.PoliceOfficerController.onPageLoad(NormalMode)
        }
      }

      "on PoliceOfficer" must {

        "go to EmployerContribution when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(PoliceOfficerPage, true).success.value
          navigator.nextPage(PoliceOfficerPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContribution when 'No' is selected" in {
          val answers = emptyUserAnswers.set(PoliceOfficerPage, false).success.value
          navigator.nextPage(PoliceOfficerPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }
    }
  }
}
