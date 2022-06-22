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
import controllers.police.routes
import controllers.routes._
import models.{CheckMode, NormalMode}
import org.scalatestplus.mockito.MockitoSugar
import pages.Page
import pages.police._

class PoliceNavigatorSpec extends SpecBase with MockitoSugar {
  private val navigator = new PoliceNavigator
  private val modes = Seq(NormalMode, CheckMode)

  "PoliceNavigator" when {

    for (mode <- modes) {
      s"in $mode" must {

        "go to SessionExpired when not a police page" in {
          navigator.nextPage(mock[Page], mode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad
        }

        "on SpecialConstableController" must {

          "go to CannotClaimExpense when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(SpecialConstablePage, true).success.value
            navigator.nextPage(SpecialConstablePage, mode)(answers) mustBe
              controllers.routes.CannotClaimExpenseController.onPageLoad()
          }

          "go to CommunitySupportOfficer when 'No' is selected" in {
            val answers = emptyUserAnswers.set(SpecialConstablePage, false).success.value
            navigator.nextPage(SpecialConstablePage, mode)(answers) mustBe
              routes.CommunitySupportOfficerController.onPageLoad(mode)
          }

          "go to SessionExpired when None" in {
            navigator.nextPage(SpecialConstablePage, mode)(emptyUserAnswers) mustBe
              SessionExpiredController.onPageLoad
          }
        }

        "on CommunitySupportOfficer" must {

          "go to EmployerContribution when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(CommunitySupportOfficerPage, true).success.value
            navigator.nextPage(CommunitySupportOfficerPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to MetropolitanPolice when 'No' is selected" in {
            val answers = emptyUserAnswers.set(CommunitySupportOfficerPage, false).success.value
            navigator.nextPage(CommunitySupportOfficerPage, mode)(answers) mustBe
              routes.MetropolitanPoliceController.onPageLoad(mode)
          }

          "go to SessionExpired when None" in {
            navigator.nextPage(CommunitySupportOfficerPage, mode)(emptyUserAnswers) mustBe
              SessionExpiredController.onPageLoad
          }
        }

        "on MetropolitanPolice" must {

          "go to CannotClaimExpense when 'yes' is selected" in {
            val answers = emptyUserAnswers.set(MetropolitanPolicePage, true).success.value
            navigator.nextPage(MetropolitanPolicePage, mode)(answers) mustBe
              controllers.routes.CannotClaimExpenseController.onPageLoad()

          }

          "go to PoliceOfficer when 'No' is selected" in {
            val answers = emptyUserAnswers.set(MetropolitanPolicePage, false).success.value
            navigator.nextPage(MetropolitanPolicePage, mode)(answers) mustBe
              routes.PoliceOfficerController.onPageLoad(mode)
          }

          "go to SessionExpired when None" in {
            navigator.nextPage(MetropolitanPolicePage, mode)(emptyUserAnswers) mustBe
              SessionExpiredController.onPageLoad
          }
        }

        "on PoliceOfficer" must {

          "go to EmployerContribution when 'Yes' is selected" in {
            val answers = emptyUserAnswers.set(PoliceOfficerPage, true).success.value
            navigator.nextPage(PoliceOfficerPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to EmployerContribution when 'No' is selected" in {
            val answers = emptyUserAnswers.set(PoliceOfficerPage, false).success.value
            navigator.nextPage(PoliceOfficerPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }
      }
    }
  }
}
