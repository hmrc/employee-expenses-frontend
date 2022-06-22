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
import models.{CheckMode, NormalMode}
import pages.foodCatering.CateringStaffNHSPage
import controllers.routes._

class FoodCateringNavigatorSpec extends SpecBase {
  private val modes = Seq(NormalMode, CheckMode)
  private val navigator = new FoodCateringNavigator

  "FoodCateringNavigator" when {
    for (mode <- modes) {
      s"in $mode" must {
        "go to EmployerContributionController from CateringStaffNHSController when answered 'yes' " in {
          val userAnswers = emptyUserAnswers.set(CateringStaffNHSPage, true).success.value

          navigator.nextPage(CateringStaffNHSPage, mode)(userAnswers) mustBe
            EmployerContributionController.onPageLoad(mode)
        }

        "go to EmployerContributionController from CateringStaffNHSController when answered 'no' " in {
          val userAnswers = emptyUserAnswers.set(CateringStaffNHSPage, false).success.value

          navigator.nextPage(CateringStaffNHSPage, mode)(userAnswers) mustBe
            EmployerContributionController.onPageLoad(mode)
        }

        "go to session expired page if no data" in {
          navigator.nextPage(CateringStaffNHSPage, mode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad
        }
      }
    }
  }
}
