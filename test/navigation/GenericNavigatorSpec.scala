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
import models.FirstIndustryOptions.Engineering
import models._
import org.scalatest.prop.PropertyChecks
import pages._

class GenericNavigatorSpec extends SpecBase with PropertyChecks {

  val navigator = new GenericNavigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode)(UserAnswers(userAnswersId)) mustBe routes.IndexController.onPageLoad()
      }

      "go to TypeOfEngineeringPage from FirstIndustryOptionsPage when Engineering is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, Engineering).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.engineering.routes.TypeOfEngineeringController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode)(UserAnswers(userAnswersId)) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}