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
import models.{CheckMode, ConstructionOccupations, NormalMode}
import org.scalatestplus.mockito.MockitoSugar
import pages.construction._

class ConstructionNavigatorSpec extends SpecBase with MockitoSugar {
  private val modes = Seq(NormalMode, CheckMode)
  private val navigator = new ConstructionNavigator

  "Construction Navigator" when {
    for (mode <- modes) {
      s"in $mode" must {

        "go to EmployerContribution from ConstructionOccupations" in {
          val answers = emptyUserAnswers.set(ConstructionOccupationsPage, ConstructionOccupations.BuildingMaterials).success.value

          navigator.nextPage(ConstructionOccupationsPage, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }
      }
    }
  }

}
