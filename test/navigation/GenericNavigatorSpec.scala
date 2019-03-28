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
import controllers.authenticated.routes._
import controllers.foodCatering.routes._
import controllers.routes._
import models.FirstIndustryOptions._
import models.FourthIndustryOptions.{Agriculture, FireService, Heating, Leisure, Prisons}
import models.SecondIndustryOptions._
import models.ThirdIndustryOptions._
import models._
import pages._

class GenericNavigatorSpec extends SpecBase {

  val navigator = new GenericNavigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode)(UserAnswers(userAnswersId)) mustBe
          IndexController.onPageLoad()
      }

      // Claim Amount

      "go to TaxYearSelectionController from ClaimAmount" in {
        navigator.nextPage(ClaimAmount, NormalMode)(emptyUserAnswers) mustBe
          TaxYearSelectionController.onPageLoad(NormalMode)
      }

      //MultipleEmploymentsPage

      "go to ClaimByAlternativeController from MultipleEmploymentsPage when 'Yes' is selected" in {
        val answers = emptyUserAnswers.set(MultipleEmploymentsPage, MultipleEmployments.MoreThanOneJob).success.value

        navigator.nextPage(MultipleEmploymentsPage, NormalMode)(answers) mustBe
          ClaimByAlternativeController.onPageLoad()
      }

      "go to ClaimByAlternativeController from MultipleEmploymentsPage when 'No' is selected" in {
        val answers = emptyUserAnswers.set(MultipleEmploymentsPage, MultipleEmployments.OneJob).success.value

        navigator.nextPage(MultipleEmploymentsPage, NormalMode)(answers) mustBe
          FirstIndustryOptionsController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from MultipleEmploymentsPage when no data is available" in {
        navigator.nextPage(MultipleEmploymentsPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
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
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go to CateringStaffNHSController from FirstIndustryOptionsPage when FoodAndCatering is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, FoodAndCatering).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          CateringStaffNHSController.onPageLoad(NormalMode)
      }

      "go to SecondIndustryOptionsController from FirstIndustryOptionsPage when NoneOfTheAbove is selected" in {
        val answers = emptyUserAnswers.set(FirstIndustryOptionsPage, FirstIndustryOptions.NoneOfAbove).success.value

        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(answers) mustBe
          SecondIndustryOptionsController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from FirstIndustryOptionsPage when no data is available" in {
        navigator.nextPage(FirstIndustryOptionsPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      //SecondIndustryOptionsPage

      "go JoinerCarpenterController from SecondIndustryOptionsPage when 'Construction' is selected" in {
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, Construction).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.construction.routes.JoinerCarpenterController.onPageLoad(NormalMode)
      }

      "go ThirdIndustryOptionsController from SecondIndustryOptionsPage when NoneOfAbove is selected" in {
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, SecondIndustryOptions.NoneOfAbove).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
        ThirdIndustryOptionsController.onPageLoad(NormalMode)

      }

      "go to EmployersContributionsController from SecondIndustryOptionsPage when 'Council' is selected" in {
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, Council).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go to TypeOfManufacturingController from SecondIndustryOptionsPage when 'ManufacturingWarehousing' is selected" in {
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, ManufacturingWarehousing).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.manufacturing.routes.TypeOfManufacturingController.onPageLoad(NormalMode)
      }

      "go to SpecialConstableController from SecondIndustryOptionsPage when 'Police' is selected" in{
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, Police).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.police.routes.SpecialConstableController.onPageLoad(NormalMode)
      }

      "go to ClothingController from SecondIndustryOptionsPage when 'ClothingTextiles' is selected" in{
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, ClothingTextiles).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.clothing.routes.ClothingController.onPageLoad(NormalMode)
      }

      "go to ThirdIndustryOptionsController from SecondIndustryOptionsPage when 'None of the above' is selected" in{
        val answers = emptyUserAnswers.set(SecondIndustryOptionsPage, SecondIndustryOptions.NoneOfAbove).success.value

        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(answers) mustBe
          ThirdIndustryOptionsController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from SecondIndustryOptionsPage when no data is available" in {
        navigator.nextPage(SecondIndustryOptionsPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      //ThirdIndustryOptionsPage

      "go EmployerContributionController from ThirdIndustryOptionsPage when Education is selected" in {
        val answers = emptyUserAnswers.set(ThirdIndustryOptionsPage, Education).success.value

        navigator.nextPage(ThirdIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go EmployerContributionController from ThirdIndustryOptionsPage when Banks and Building Societies is selected" in {
        val answers = emptyUserAnswers.set(ThirdIndustryOptionsPage, BanksBuildingSocieties).success.value

        navigator.nextPage(ThirdIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go ElectricalControllerPage from ThirdIndustryOptionsPage when Eletrical is selected" in {
        val answers = emptyUserAnswers.set(ThirdIndustryOptionsPage, Electrical).success.value

        navigator.nextPage(ThirdIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.electrical.routes.ElectricalController.onPageLoad(NormalMode)
      }

      "go PrintingOccupationList1Controller from ThirdIndustryOptionsPage when Printing is selected" in {
        val answers = emptyUserAnswers.set(ThirdIndustryOptionsPage, Printing).success.value

        navigator.nextPage(ThirdIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.printing.routes.PrintingOccupationList1Controller.onPageLoad(NormalMode)
      }

      "go SecurityGuardNHSController from ThirdIndustryOptionsPage when Security is selected" in {
        val answers = emptyUserAnswers.set(ThirdIndustryOptionsPage, Security).success.value

        navigator.nextPage(ThirdIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.security.routes.SecurityGuardNHSController.onPageLoad(NormalMode)
      }

      "go FourthIndustryOptionsController from ThirdIndustryOptionsPage when NoneOfAbove is selected" in {
        val answers = emptyUserAnswers.set(ThirdIndustryOptionsPage, ThirdIndustryOptions.NoneOfAbove).success.value

        navigator.nextPage(ThirdIndustryOptionsPage, NormalMode)(answers) mustBe
          FourthIndustryOptionsController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from ThirdIndustryOptionsPage when no data is available" in {
        navigator.nextPage(ThirdIndustryOptionsPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      //FourthIndustryOptionsPage

      "go EmployerContributionController from FourthIndustryOptionsPage when 'agriculture' is selected" in {
        val answers = emptyUserAnswers.set(FourthIndustryOptionsPage, Agriculture).success.value

        navigator.nextPage(FourthIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go EmployerContributionController from FourthIndustryOptionsPage when 'fire service' is selected" in {
        val answers = emptyUserAnswers.set(FourthIndustryOptionsPage, FireService).success.value

        navigator.nextPage(FourthIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go HeatingController from FourthIndustryOptionsPage when 'heating' is selected" in {
        val answers = emptyUserAnswers.set(FourthIndustryOptionsPage, Heating).success.value

        navigator.nextPage(FourthIndustryOptionsPage, NormalMode)(answers) mustBe
          controllers.heating.routes.HeatingOccupationListController.onPageLoad(NormalMode)
      }

      "go EmployerContributionController from FourthIndustryOptionsPage when 'leisure' is selected" in {
        val answers = emptyUserAnswers.set(FourthIndustryOptionsPage, Leisure).success.value

        navigator.nextPage(FourthIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go EmployerContributionController from FourthIndustryOptionsPage when 'prison' is selected" in {
        val answers = emptyUserAnswers.set(FourthIndustryOptionsPage, Prisons).success.value

        navigator.nextPage(FourthIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go EmployerContributionController from FourthIndustryOptionsPage when 'NoneOfTheAbove' is selected" in {
        val answers = emptyUserAnswers.set(FourthIndustryOptionsPage, FourthIndustryOptions.NoneOfAbove).success.value

        navigator.nextPage(FourthIndustryOptionsPage, NormalMode)(answers) mustBe
          EmployerContributionController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from FourthIndustryOptionsPage when no data is available" in {
        navigator.nextPage(FourthIndustryOptionsPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()

      }

      //EmployerContributionPage

      "go to ClaimAmount from EmployerContributionPage when 'None' is selected" in {
        val answers = emptyUserAnswers.set(EmployerContributionPage, false).success.value

        navigator.nextPage(EmployerContributionPage, NormalMode)(answers) mustBe
          ClaimAmountController.onPageLoad(NormalMode)
      }

      "go to ExpensesEmployerPaidController from EmployerContributionPage when 'Some' is selected" in {
        val answers = emptyUserAnswers.set(EmployerContributionPage, true).success.value

        navigator.nextPage(EmployerContributionPage, NormalMode)(answers) mustBe
          ExpensesEmployerPaidController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from EmployerContributionPage when no data is available" in {
        navigator.nextPage(EmployerContributionPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      //ExpensesEmployerPaidPage

      "go to CannotClaimController from ExpensesEmployerPaidPage if ClaimAmount is <= ExpensesEmployerPaid" in {
        val answers = emptyUserAnswers.set(ExpensesEmployerPaidPage, 100).success.value
        val updatedAnswers = answers.set(ClaimAmount, 100).success.value

        navigator.nextPage(ExpensesEmployerPaidPage, NormalMode)(updatedAnswers) mustBe
          CannotClaimController.onPageLoad()
      }

      "go to SameEmployerContributionAllYearsController from ExpensesEmployerPaidPage if ClaimAmount is > ExpensesEmployerPaid" in {
        val answers = emptyUserAnswers.set(ExpensesEmployerPaidPage, 50).success.value
        val updatedAnswers = answers.set(ClaimAmount, 100).success.value

        navigator.nextPage(ExpensesEmployerPaidPage, NormalMode)(updatedAnswers) mustBe
          SameEmployerContributionAllYearsController.onPageLoad(NormalMode)
      }

      "go to SessionExpiredController from ExpensesEmployerPaidPage when no data is available" in {
        navigator.nextPage(ExpensesEmployerPaidPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      //SameEmployerContributionAllYearsPage
      "go to ClaimAmountController from SameEmployerContributionAllYearsPage if true" in {
        val answers = emptyUserAnswers.set(SameEmployerContributionAllYearsPage, true).success.value

        navigator.nextPage(SameEmployerContributionAllYearsPage, NormalMode)(answers) mustBe
          ClaimAmountController.onPageLoad(NormalMode)
      }

      "go to PhoneUsController from SameEmployerContributionAllYearsPage if false" in {
        val answers = emptyUserAnswers.set(SameEmployerContributionAllYearsPage, false).success.value

        navigator.nextPage(SameEmployerContributionAllYearsPage, NormalMode)(answers) mustBe
          PhoneUsController.onPageLoad()
      }

      "go to SessionExpiredController from SameEmployerContributionAllYearsPage when no data is available" in {
        navigator.nextPage(SameEmployerContributionAllYearsPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      "go to TaxYearSelectionController from ClaimAmountPage" in {
        navigator.nextPage(ClaimAmount, NormalMode)(emptyUserAnswers) mustBe TaxYearSelectionController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode)(UserAnswers(userAnswersId)) mustBe CheckYourAnswersController.onPageLoad()
      }

      //EmployerContributionPage

      "go to ClaimAmount from EmployerContributionPage when 'None' is selected" in {
        val answers = emptyUserAnswers.set(EmployerContributionPage, false).success.value

        navigator.nextPage(EmployerContributionPage, CheckMode)(answers) mustBe
          ClaimAmountController.onPageLoad(CheckMode)
      }

      "go to ExpensesEmployerPaidController from EmployerContributionPage when 'Some' is selected" in {
        val answers = emptyUserAnswers.set(EmployerContributionPage, true).success.value

        navigator.nextPage(EmployerContributionPage, CheckMode)(answers) mustBe
          ExpensesEmployerPaidController.onPageLoad(CheckMode)
      }

      "go to SessionExpiredController from EmployerContributionPage when no data is available" in {
        navigator.nextPage(EmployerContributionPage, CheckMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      //ExpensesEmployerPaidPage

      "go to CannotClaimController from ExpensesEmployerPaidPage if ClaimAmount is <= ExpensesEmployerPaid" in {
        val answers = emptyUserAnswers.set(ExpensesEmployerPaidPage, 100).success.value
        val updatedAnswers = answers.set(ClaimAmount, 100).success.value

        navigator.nextPage(ExpensesEmployerPaidPage, CheckMode)(updatedAnswers) mustBe
          CannotClaimController.onPageLoad()
      }

      "go to SameEmployerContributionAllYearsController from ExpensesEmployerPaidPage if ClaimAmount is > ExpensesEmployerPaid" in {
        val answers = emptyUserAnswers.set(ExpensesEmployerPaidPage, 50).success.value
        val updatedAnswers = answers.set(ClaimAmount, 100).success.value

        navigator.nextPage(ExpensesEmployerPaidPage, CheckMode)(updatedAnswers) mustBe
          SameEmployerContributionAllYearsController.onPageLoad(CheckMode)
      }

      "go to SessionExpiredController from ExpensesEmployerPaidPage when no data is available" in {
        navigator.nextPage(ExpensesEmployerPaidPage, CheckMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      //SameEmployerContributionAllYearsPage
      "go to ClaimAmountController from SameEmployerContributionAllYearsPage if true" in {
        val answers = emptyUserAnswers.set(SameEmployerContributionAllYearsPage, true).success.value

        navigator.nextPage(SameEmployerContributionAllYearsPage, CheckMode)(answers) mustBe
          ClaimAmountController.onPageLoad(CheckMode)
      }

      "go to PhoneUsController from SameEmployerContributionAllYearsPage if false" in {
        val answers = emptyUserAnswers.set(SameEmployerContributionAllYearsPage, false).success.value

        navigator.nextPage(SameEmployerContributionAllYearsPage, CheckMode)(answers) mustBe
          PhoneUsController.onPageLoad()
      }

      "go to SessionExpiredController from SameEmployerContributionAllYearsPage when no data is available" in {
        navigator.nextPage(SameEmployerContributionAllYearsPage, CheckMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      "go to TaxYearSelectionController from ClaimAmountPage" in {
        navigator.nextPage(ClaimAmount, CheckMode)(emptyUserAnswers) mustBe CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
