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
import controllers.routes
import models.NormalMode
import pages.healthcare._

class HealthcareNavigatorSpec extends SpecBase {

  val navigator = new HealthcareNavigator

  "HealthcareNavigator" when {

    "in Normal mode" must {

      "ambulance staff page" must {

        "go to healthcare list 1 page if answered no" in {

          val userAnswers = emptyUserAnswers.set(AmbulanceStaffPage, false).success.value

          navigator.nextPage(AmbulanceStaffPage, NormalMode)(userAnswers) mustBe
            controllers.healthcare.routes.HealthcareList1Controller.onPageLoad(NormalMode)
        }
        "go to claim amount page if answered yes" in {

          val userAnswers = emptyUserAnswers.set(AmbulanceStaffPage, true).success.value

          navigator.nextPage(AmbulanceStaffPage, NormalMode)(userAnswers) mustBe
            routes.EmployerContributionController.onPageLoad(NormalMode)
        }
        "go to session expired page if no data" in {
          navigator.nextPage(AmbulanceStaffPage, NormalMode)(emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Healthcare list 1 page" must {
        "go to healthcare list 2 page if answered no" in {

          val userAnswers = emptyUserAnswers.set(HealthcareList1Page, false).success.value

          navigator.nextPage(HealthcareList1Page, NormalMode)(userAnswers) mustBe
            controllers.healthcare.routes.HealthcareList2Controller.onPageLoad(NormalMode)
        }
        "go to claim amount page if answered yes" in {

          val userAnswers = emptyUserAnswers.set(HealthcareList1Page, true).success.value

          navigator.nextPage(HealthcareList1Page, NormalMode)(userAnswers) mustBe
            routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to session expired page if no data" in {
          navigator.nextPage(HealthcareList1Page, NormalMode)(emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Healthcare list 2 page" must {
        "go to claim amount page if answered yes" in {

          val userAnswers = emptyUserAnswers.set(HealthcareList2Page, true).success.value

          navigator.nextPage(HealthcareList2Page, NormalMode)(userAnswers) mustBe
            routes.EmployerContributionController.onPageLoad(NormalMode)
        }
        "go to claim amount page if answered no" in {

          val userAnswers = emptyUserAnswers.set(HealthcareList2Page, false).success.value

          navigator.nextPage(HealthcareList2Page, NormalMode)(userAnswers) mustBe
            routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to session expired page if no data" in {
          navigator.nextPage(HealthcareList2Page, NormalMode)(emptyUserAnswers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }

//    "in Check mode" must {
//
//    }

  }
}
