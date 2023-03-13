/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.printing.routes
import controllers.routes.SessionExpiredController
import org.scalatestplus.mockito.MockitoSugar
import pages.Page
import pages.printing._

class PrintingNavigatorSpec extends SpecBase with MockitoSugar {
  private val navigator = new PrintingNavigator
  private val modes = Seq(NormalMode, CheckMode)

  "PrintingNavigator" when {
    for (mode <- modes) {
      s"in $mode" must {

        "go to SessionExpired when not a printing page" in {
          navigator.nextPage(mock[Page], mode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad
        }

        "go to EmployerContributionController from PrintingOccupationList1Page when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(PrintingOccupationList1Page, true).success.value
          navigator.nextPage(PrintingOccupationList1Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to PrintingOccupationList2Controller from PrintingOccupationList1Page when 'No' is selected" in {
          val answers = emptyUserAnswers.set(PrintingOccupationList1Page, false).success.value
          navigator.nextPage(PrintingOccupationList1Page, mode)(answers) mustBe
            routes.PrintingOccupationList2Controller.onPageLoad(mode)
        }

        "go to SessionExpiredController from PrintingOccupationList1Page when None" in {
          navigator.nextPage(PrintingOccupationList1Page, mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad
        }

        "go to EmployerContributionController from PrintingOccupationList2Controller when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(PrintingOccupationList2Page, true).success.value
          navigator.nextPage(PrintingOccupationList2Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to EmployerContributionController from PrintingOccupationList2Controller when 'No' is selected" in {
          val answers = emptyUserAnswers.set(PrintingOccupationList2Page, false).success.value
          navigator.nextPage(PrintingOccupationList2Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }
      }
    }
  }
}
