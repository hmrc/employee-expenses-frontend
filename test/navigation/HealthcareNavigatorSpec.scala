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
import controllers.routes
import models.{CheckMode, NormalMode}
import pages.healthcare._

class HealthcareNavigatorSpec extends SpecBase {
  private val modes = Seq(NormalMode, CheckMode)
  private val navigator = new HealthcareNavigator

  "HealthcareNavigator" when {

    for (mode <- modes) {
      s"in $mode" must {

        "Healthcare list 1 page" must {
          "go to healthcare list 2 page if answered no" in {

            val userAnswers = emptyUserAnswers.set(HealthcareList1Page, false).success.value

            navigator.nextPage(HealthcareList1Page, mode)(userAnswers) mustBe
              controllers.healthcare.routes.HealthcareList2Controller.onPageLoad(mode)
          }
          "go to EmployerContribution page if answered yes" in {

            val userAnswers = emptyUserAnswers.set(HealthcareList1Page, true).success.value

            navigator.nextPage(HealthcareList1Page, mode)(userAnswers) mustBe
              routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to session expired page if no data" in {
            navigator.nextPage(HealthcareList1Page, mode)(emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }
        }

        "Healthcare list 2 page" must {
          "go to EmployerContribution page if answered yes" in {

            val userAnswers = emptyUserAnswers.set(HealthcareList2Page, true).success.value

            navigator.nextPage(HealthcareList2Page, mode)(userAnswers) mustBe
              routes.EmployerContributionController.onPageLoad(mode)
          }
          "go to claim amount page if answered no" in {

            val userAnswers = emptyUserAnswers.set(HealthcareList2Page, false).success.value

            navigator.nextPage(HealthcareList2Page, mode)(userAnswers) mustBe
              controllers.healthcare.routes.AmbulanceStaffController.onPageLoad(mode)
          }

          "go to session expired page if no data" in {
            navigator.nextPage(HealthcareList2Page, mode)(emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }
        }

        "ambulance staff page" must {

          "go to EmployerContribution page if answered no" in {

            val userAnswers = emptyUserAnswers.set(AmbulanceStaffPage, false).success.value

            navigator.nextPage(AmbulanceStaffPage, mode)(userAnswers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
          "go to EmployerContribution page if answered yes" in {

            val userAnswers = emptyUserAnswers.set(AmbulanceStaffPage, true).success.value

            navigator.nextPage(AmbulanceStaffPage, mode)(userAnswers) mustBe
              routes.EmployerContributionController.onPageLoad(mode)
          }
          "go to session expired page if no data" in {
            navigator.nextPage(AmbulanceStaffPage, mode)(emptyUserAnswers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }
        }
      }
    }
  }
}
