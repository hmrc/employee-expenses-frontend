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
import controllers.foodCatering.routes._
import controllers.construction.routes._
import models.EmployerContribution._
import models.FirstIndustryOptions._
import models.SecondIndustryOptions._
import models._
import pages._

class GenericNavigatorSpec extends SpecBase {

  val navigator = new GenericNavigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode)(UserAnswers(userAnswersId)) mustBe routes.IndexController.onPageLoad()
      }

      //MultipleEmploymentsPage

      "go to ClaimByAlternativeController from MultipleEmploymentsPage when 'Yes' is selected" in {
        val answers = emptyUserAnswers.set(MultipleEmploymentsPage, true).success.value

        navigator.nextPage(MultipleEmploymentsPage, NormalMode)(answers) mustBe
          controllers.routes.ClaimByAlternativeController.onPageLoad()
      }

      "go to ClaimByAlternativeController from MultipleEmploymentsPage when 'No' is selected" in {
        val answers = emptyUserAnswers.set(MultipleEmploymentsPage, false).success.value

        navigator.nextPage(MultipleEmploymentsPage, NormalMode)(answers) mustBe
          controllers.routes.FirstIndustryOptionsController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from MultipleEmploymentsPage when no data is available" in {
        navigator.nextPage(MultipleEmploymentsPage, NormalMode)(emptyUserAnswers) mustBe
          controllers.routes.SessionExpiredController.onPageLoad()
      }

      //FirstIndustryOptionsPage

      "go to TypeOfEngineeringController from FirstIndustryOptionsPage when Engineering is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, Engineering).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.engineering.routes.TypeOfEngineeringController.onPageLoad(NormalMode)
      }

      "go to TypeOfTransportController from FirstIndustryOptionsPage when TransportAndDistribution is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, TransportAndDistribution).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.transport.routes.TypeOfTransportController.onPageLoad(NormalMode)
      }

      "go to AmbulanceStaffController from FirstIndustryOptionsPage when Healthcare is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, Healthcare).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.healthcare.routes.AmbulanceStaffController.onPageLoad(NormalMode)
      }

      "go to EmployerContributionController from FirstIndustryOptionsPage when Retail is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, Retail).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
      }

      "go to CateringStaffNHSController from FirstIndustryOptionsPage when FoodAndCatering is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, FoodAndCatering).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          CateringStaffNHSController.onPageLoad(NormalMode)
      }

      "go to SecondIndustryOptionsController from FirstIndustryOptionsPage when NoneOfTheAbove is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, NoneOfTheAbove).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.routes.SecondIndustryOptionsController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from FirstIndustryOptionsPage when no data is available" in {
        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(emptyUserAnswers) mustBe
          controllers.routes.SessionExpiredController.onPageLoad()
      }

      //SecondIndustryOptionsPage

      "go JoinerCarpenterController from SecondIndustryOptionsPage when Construction is selected" in {
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, Construction).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.construction.routes.JoinerCarpenterController.onPageLoad(NormalMode)

      }

      "go to SessionExpiredController from SecondIndustryOptionsPage when no data is available" in {
        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(emptyUserAnswers) mustBe
          controllers.routes.SessionExpiredController.onPageLoad()
      }

      //EmployerContributionPage

      "go to CannotClaimController from EmployerContributionPage when 'All' is selected" in {
        val answers = emptyUserAnswers.set(EmployerContributionPage, All).success.value

        navigator.nextPage(EmployerContributionPage, NormalMode)(answers) mustBe
          controllers.routes.CannotClaimController.onPageLoad()
      }

      "go to CannotClaimController from EmployerContributionPage when 'None' is selected" in {
        val answers = emptyUserAnswers.set(EmployerContributionPage, None).success.value

        navigator.nextPage(EmployerContributionPage, NormalMode)(answers) mustBe
          controllers.routes.ClaimAmountController.onPageLoad()
      }

      "go to CannotClaimController from EmployerContributionPage when 'Some' is selected" in {
        val answers = emptyUserAnswers.set(EmployerContributionPage, Some).success.value

        navigator.nextPage(EmployerContributionPage, NormalMode)(answers) mustBe
          controllers.routes.ExpensesEmployerPaidController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from EmployerContributionPage when no data is available" in {
        navigator.nextPage(EmployerContributionPage, NormalMode)(emptyUserAnswers) mustBe
          controllers.routes.SessionExpiredController.onPageLoad()
      }

      //ExpensesEmployerPaidPage

      "go to CannotClaimController from ExpensesEmployerPaidPage if ClaimAmount is <= ExpensesEmployerPaid" in {
        val answers = emptyUserAnswers.set(ExpensesEmployerPaidPage, 100).success.value
        val updatedAnswers = answers.set(ClaimAmount, 100).success.value

        navigator.nextPage(ExpensesEmployerPaidPage, NormalMode)(updatedAnswers) mustBe
          controllers.routes.CannotClaimController.onPageLoad()
      }

      "go to ClaimAmountController from ExpensesEmployerPaidPage if ClaimAmount is > ExpensesEmployerPaid" in {
        val answers = emptyUserAnswers.set(ExpensesEmployerPaidPage, 50).success.value
        val updatedAnswers = answers.set(ClaimAmount, 100).success.value

        navigator.nextPage(ExpensesEmployerPaidPage, NormalMode)(updatedAnswers) mustBe
          controllers.routes.ClaimAmountController.onPageLoad()
      }

      "go to SessionExpiredController from ExpensesEmployerPaidPage when no data is available" in {
        navigator.nextPage(ExpensesEmployerPaidPage, NormalMode)(emptyUserAnswers) mustBe
          controllers.routes.SessionExpiredController.onPageLoad()
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
