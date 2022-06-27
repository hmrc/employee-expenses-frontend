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
import org.scalatestplus.mockito.MockitoSugar
import pages.Page
import pages.clothing.ClothingPage

class ClothingNavigatorSpec extends SpecBase with MockitoSugar {
  private val modes = Seq(NormalMode, CheckMode)
  val navigator = new ClothingNavigator

  "ClothingNavigator" when {
    for (mode <- modes) {
      s"in $mode" must {
        "go to EmployerContribution from Clothing when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(ClothingPage, true).success.value

          navigator.nextPage(ClothingPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to EmployerContribution from Clothing when 'No' is selected" in {
          val answers = emptyUserAnswers.set(ClothingPage, false).success.value

          navigator.nextPage(ClothingPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to SessionExpired when not ClothingPage" in {
          navigator.nextPage(mock[Page], mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad
        }
      }
    }
  }
}
