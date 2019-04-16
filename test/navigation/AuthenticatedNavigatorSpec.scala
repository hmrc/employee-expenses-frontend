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
import controllers.routes._
import models.AlreadyClaimingFREDifferentAmounts.{Change, NoChange, Remove}
import models.TaxYearSelection.{CurrentYear, CurrentYearMinus1}
import models.{AlreadyClaimingFRESameAmount, CheckMode, FlatRateExpenseOptions, NormalMode, TaxYearSelection}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import controllers.confirmation.routes._
import models.AlreadyClaimingFREDifferentAmounts._
import models.FlatRateExpenseOptions.FRENoYears
import models.TaxYearSelection._
import models._
import org.scalatest.concurrent._
import org.scalatest.mockito.MockitoSugar
import pages.FREResponse
import pages.authenticated._

class AuthenticatedNavigatorSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  val navigator = new AuthenticatedNavigator

  "Authenticated Navigator" when {
    "in Normal mode" must {

      "from TaxYearSelection" must {

        "go to YourEmployer when answered and freResponse returns FRENoYears and currentTax has been selected" in {

          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FRENoYears).success.value
              .set(TaxYearSelectionPage, Seq(CurrentYear)).success.value

          navigator.nextPage(TaxYearSelectionPage, NormalMode)(ua) mustBe
            YourEmployerController.onPageLoad(NormalMode)
        }

        "go to YourAddress when answered and freResponse returns FRENoYears and currentTax has not been selected" in {

          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FRENoYears).success.value
            .set(TaxYearSelectionPage, Seq(CurrentYearMinus1)).success.value

          navigator.nextPage(TaxYearSelectionPage, NormalMode)(ua) mustBe
            YourAddressController.onPageLoad(NormalMode)
        }

        "go to AlreadyClaimingFRESameAmount when answered and freResponse returns FREAllYearsAllAmountsSameAsClaimAmount" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount).success.value

          navigator.nextPage(TaxYearSelectionPage, NormalMode)(ua) mustBe
            AlreadyClaimingFRESameAmountController.onPageLoad(NormalMode)
        }

        "go to AlreadyClaimingFREDifferentAmountsController when answered and freResponse returns FREAllYearsAllAmountsDifferent" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FREAllYearsAllAmountsDifferent).success.value

          navigator.nextPage(TaxYearSelectionPage, NormalMode)(ua) mustBe
            AlreadyClaimingFREDifferentAmountsController.onPageLoad(NormalMode)
        }

        "go to PhoneUsController when answered and freResponse returns ComplexClaim" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.ComplexClaim).success.value

          navigator.nextPage(TaxYearSelectionPage, NormalMode)(ua) mustBe
            PhoneUsController.onPageLoad()
        }

        "go to SessionExpiredController when answered and freResponse returns TechnicalDifficulties" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.TechnicalDifficulties).success.value

          navigator.nextPage(TaxYearSelectionPage, NormalMode)(ua) mustBe
            TechnicalDifficultiesController.onPageLoad()
        }

      }

      "from AlreadyClaimingFRESameAmount" must {

        "go to NoCodeChange when answer is NoChange" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.NoChange).success.value

          navigator.nextPage(AlreadyClaimingFRESameAmountPage, NormalMode)(ua) mustBe
            NoCodeChangeController.onPageLoad()
        }

        "go to RemoveFRECode when answer is false" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.Remove).success.value

          navigator.nextPage(AlreadyClaimingFRESameAmountPage, NormalMode)(ua) mustBe
            RemoveFRECodeController.onPageLoad(NormalMode)
        }

        "go to SessionExpired if no answer" in {
          navigator.nextPage(AlreadyClaimingFRESameAmountPage, NormalMode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad()
        }
      }

      "from AlreadyClaimingFREDifferentAmounts" must {

        "go to NoCodeChange when answer is true" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, NoChange).success.value

          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, NormalMode)(ua) mustBe
            NoCodeChangeController.onPageLoad()
        }

        "go to ChangeWhichTaxYears when answer is false" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, Change).success.value

          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, NormalMode)(ua) mustBe
            ChangeWhichTaxYearsController.onPageLoad(NormalMode)
        }

        "go to RemoveFRECode when answer is false" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, Remove).success.value

          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, NormalMode)(ua) mustBe
            RemoveFRECodeController.onPageLoad(NormalMode)
        }

        "go to SessionExpired if no answer" in {
          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, NormalMode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad()
        }
      }

      "go to CheckYourAnswers from RemoveFRE page" in {
        val ua = emptyUserAnswers.set(RemoveFRECodePage, TaxYearSelection.CurrentYear).success.value

        navigator.nextPage(RemoveFRECodePage, NormalMode)(ua) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to YourEmployer from ChangeWhichTaxYearsPage when using currentTaxYear" in {
        val ua = emptyUserAnswers.set(ChangeWhichTaxYearsPage, Seq(TaxYearSelection.CurrentYear)).success.value
          .set(ChangeWhichTaxYearsPage, Seq(CurrentYear)).success.value

        navigator.nextPage(ChangeWhichTaxYearsPage, NormalMode)(ua) mustBe
          YourEmployerController.onPageLoad(NormalMode)
      }

      "go to YourAddressfrom ChangeWhichTaxYearsPage when not using currentTaxYear" in {
        val ua = emptyUserAnswers.set(ChangeWhichTaxYearsPage, Seq(TaxYearSelection.CurrentYear)).success.value
            .set(ChangeWhichTaxYearsPage, Seq(CurrentYearMinus1)).success.value

        navigator.nextPage(ChangeWhichTaxYearsPage, NormalMode)(ua) mustBe
          YourAddressController.onPageLoad(NormalMode)
      }

      "go to YourAddress from YourEmployer when answered true" in {
        val ua = emptyUserAnswers.set(YourEmployerPage, true).success.value

        navigator.nextPage(YourEmployerPage, NormalMode)(ua) mustBe
          YourAddressController.onPageLoad(NormalMode)
      }

      "go to UpdateEmployerInformation from YourEmployer when answered false" in {
        val ua = emptyUserAnswers.set(YourEmployerPage, false).success.value

        navigator.nextPage(YourEmployerPage, NormalMode)(ua) mustBe
          UpdateEmployerInformationController.onPageLoad(NormalMode)
      }

      "go to YourAddress from UpdateEmployerInformation" in {
        navigator.nextPage(UpdateYourEmployerInformationPage, NormalMode)(emptyUserAnswers) mustBe
          YourAddressController.onPageLoad(NormalMode)
      }

      "go to CheckYourAnswers from YourAddress when answered true" in {
        val ua = emptyUserAnswers.set(YourAddressPage, true).success.value

        navigator.nextPage(YourAddressPage, NormalMode)(ua) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to UpdateYourAddress from YourAddress when answered false" in {
        val ua = emptyUserAnswers.set(YourAddressPage, false).success.value

        navigator.nextPage(YourAddressPage, NormalMode)(ua) mustBe
          UpdateYourAddressController.onPageLoad()
      }

      "go to CheckYourAnswers from UpdateYourAddressPage" in {
        navigator.nextPage(UpdateYourAddressPage, NormalMode)(emptyUserAnswers) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to SessionExpired from YourAddress when YourAddressPage is not in UserAnswers" in {
        navigator.nextPage(YourAddressPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      "go to SessionExpired from YourEmployer when YourEmployerPage is not in UserAnswers" in {
        navigator.nextPage(YourEmployerPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      "go to SessionExpired from TaxYearSelectionPage when FREResponse is not in UserAnswers" in {
        navigator.nextPage(TaxYearSelectionPage, NormalMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      "from CYA" must {
        "go to ConfirmationClaimStoppedController" in {
          val ua = minimumUserAnswers.set(FREResponse, FRENoYears).success.value

          navigator.nextPage(CheckYourAnswersPage, NormalMode)(ua) mustBe
            ConfirmationClaimStoppedController.onPageLoad()
        }

        "go to ConfirmationCurrentAndPreviousYearsController" in {
          val ua = fullUserAnswers
            .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1)).success.value

          navigator.nextPage(CheckYourAnswersPage, NormalMode)(ua) mustBe
            ConfirmationCurrentAndPreviousYearsController.onPageLoad()
        }

        "go to ConfirmationCurrentYearOnlyController" in {
          navigator.nextPage(CheckYourAnswersPage, NormalMode)(fullUserAnswers) mustBe
            ConfirmationCurrentYearOnlyController.onPageLoad()
        }

        "go to ConfirmationPreviousYearsOnlyController" in {
          val ua = fullUserAnswers
            .set(TaxYearSelectionPage, Seq(CurrentYearMinus1, CurrentYearMinus2)).success.value

          navigator.nextPage(CheckYourAnswersPage, NormalMode)(ua) mustBe
            ConfirmationPreviousYearsOnlyController.onPageLoad()
        }
      }
    }

    "in CheckMode" must {

      "go to CheckYourAnswers from UpdateYourAddressPage" in {
        navigator.nextPage(UpdateYourAddressPage, CheckMode)(emptyUserAnswers) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to CheckYourAnswers from YourEmployerController when 'true' and when YourAddress is defined" in {
        val ua = emptyUserAnswers.set(YourEmployerPage, true).success.value
          .set(YourAddressPage, true).success.value

        navigator.nextPage(YourEmployerPage, CheckMode)(ua) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to YourAddress from YourEmployerController when 'true' and when YourAddress is not defined" in {
        val userAnswers = emptyUserAnswers.set(YourEmployerPage, true).success.value

        navigator.nextPage(YourEmployerPage, CheckMode)(userAnswers) mustBe
          YourAddressController.onPageLoad(CheckMode)
      }

      "go to UpdateEmployerInformationController from YourEmployerController when 'false'" in {
        val userAnswers = emptyUserAnswers.set(YourEmployerPage, false).success.value

        navigator.nextPage(YourEmployerPage, CheckMode)(userAnswers) mustBe
          UpdateEmployerInformationController.onPageLoad(CheckMode)
      }

      "go to CheckYourAnswersController from UpdateEmployerInformation when YourAddress is defined" in {
        val userAnswers = emptyUserAnswers.set(YourAddressPage, true).success.value

        navigator.nextPage(UpdateYourEmployerInformationPage, CheckMode)(userAnswers) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to YourAddress from UpdateEmployerInformation when YourAddress is not defined" in {
        navigator.nextPage(UpdateYourEmployerInformationPage, CheckMode)(emptyUserAnswers) mustBe
          YourAddressController.onPageLoad(CheckMode)
      }

      "go to YourEmployer from ChangeWhichTaxYear when YourEmployer is not defined and CurrentYear is selected" in {
        val userAnswers = emptyUserAnswers.set(ChangeWhichTaxYearsPage, Seq(CurrentYear)).success.value
        navigator.nextPage(ChangeWhichTaxYearsPage, CheckMode)(userAnswers) mustBe
          YourEmployerController.onPageLoad(CheckMode)
      }

      "go to CheckYourAnswers from ChangeWhichTaxYear when YourEmployer is not defined and CurrentYear is not selected" in {
        val userAnswers = emptyUserAnswers.set(TaxYearSelectionPage, Seq(CurrentYearMinus1)).success.value
        navigator.nextPage(ChangeWhichTaxYearsPage, CheckMode)(userAnswers) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to CheckYourAnswers from ChangeWhichTaxYear when YourEmployer is defined and CurrentYear is selected" in {
        val userAnswers = emptyUserAnswers.set(YourEmployerPage, true).success.value
            .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1)).success.value

        navigator.nextPage(ChangeWhichTaxYearsPage, CheckMode)(userAnswers) mustBe
          CheckYourAnswersController.onPageLoad()
      }


      "from TaxYearSelection" must {

        "go to YourEmployer when answered and freResponse returns FRENoYears and has currentYear" in {

          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FRENoYears).success.value
            .set(TaxYearSelectionPage, Seq(CurrentYear)).success.value

          navigator.nextPage(TaxYearSelectionPage, CheckMode)(ua) mustBe
            YourEmployerController.onPageLoad(CheckMode)
        }

        "go to YourAddress when answered and freResponse returns FRENoYears and doesn't have  currentYear" in {

          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FRENoYears).success.value
            .set(TaxYearSelectionPage, Seq(CurrentYearMinus1)).success.value

          navigator.nextPage(TaxYearSelectionPage, CheckMode)(ua) mustBe
            YourAddressController.onPageLoad(CheckMode)
        }

        "go to AlreadyClaimingFRESameAmount when answered and freResponse returns FREAllYearsAllAmountsSameAsClaimAmount" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FREAllYearsAllAmountsSameAsClaimAmount).success.value

          navigator.nextPage(TaxYearSelectionPage, CheckMode)(ua) mustBe
            AlreadyClaimingFRESameAmountController.onPageLoad(CheckMode)
        }

        "go to AlreadyClaimingFREDifferentAmountsController when answered and freResponse returns FREAllYearsAllAmountsDifferent" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FREAllYearsAllAmountsDifferent).success.value

          navigator.nextPage(TaxYearSelectionPage, CheckMode)(ua) mustBe
            AlreadyClaimingFREDifferentAmountsController.onPageLoad(CheckMode)
        }

        "go to PhoneUsController when answered and freResponse returns ComplexClaim" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.ComplexClaim).success.value

          navigator.nextPage(TaxYearSelectionPage, CheckMode)(ua) mustBe
            PhoneUsController.onPageLoad()
        }

        "go to SessionExpiredController when answered and freResponse returns TechnicalDifficulties" in {
          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.TechnicalDifficulties).success.value

          navigator.nextPage(TaxYearSelectionPage, CheckMode)(ua) mustBe
            TechnicalDifficultiesController.onPageLoad()
        }
      }


      "from AlreadyClaimingFRESameAmount" must {

        "go to NoCodeChange when answer is NoChange" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.NoChange).success.value

          navigator.nextPage(AlreadyClaimingFRESameAmountPage, CheckMode)(ua) mustBe
            NoCodeChangeController.onPageLoad()
        }

        "go to RemoveFRECode when answer is false" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.Remove).success.value

          navigator.nextPage(AlreadyClaimingFRESameAmountPage, CheckMode)(ua) mustBe
            RemoveFRECodeController.onPageLoad(CheckMode)
        }

        "go to SessionExpired if no answer" in {
          navigator.nextPage(AlreadyClaimingFRESameAmountPage, CheckMode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad()
        }
      }

      "from AlreadyClaimingFREDifferentAmounts" must {

        "go to NoCodeChange when answer is true" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, NoChange).success.value

          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, CheckMode)(ua) mustBe
            NoCodeChangeController.onPageLoad()
        }

        "go to ChangeWhichTaxYears when answer is false" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, Change).success.value

          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, CheckMode)(ua) mustBe
            ChangeWhichTaxYearsController.onPageLoad(CheckMode)
        }

        "go to RemoveFRECode when answer is false" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFREDifferentAmountsPage, Remove).success.value

          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, CheckMode)(ua) mustBe
            RemoveFRECodeController.onPageLoad(CheckMode)
        }

        "go to SessionExpired if no answer" in {
          navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, CheckMode)(emptyUserAnswers) mustBe
            SessionExpiredController.onPageLoad()
        }
      }

      "go to CheckYourAnswers from YourAddress when answered true" in {
        val ua = emptyUserAnswers.set(YourAddressPage, true).success.value

        navigator.nextPage(YourAddressPage, CheckMode)(ua) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to UpdateYourAddress from YourAddress when answered false" in {
        val ua = emptyUserAnswers.set(YourAddressPage, false).success.value

        navigator.nextPage(YourAddressPage, CheckMode)(ua) mustBe
          UpdateYourAddressController.onPageLoad()
      }

      "go to SessionExpired from YourAddress when YourAddressPage is not in UserAnswers" in {
        navigator.nextPage(YourAddressPage, CheckMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      "go to SessionExpired from YourEmployer when YourEmployerPage is not in UserAnswers" in {
        navigator.nextPage(YourEmployerPage, CheckMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

      "go to SessionExpired from TaxYearSelectionPage when FREResponse is not in UserAnswers" in {
        navigator.nextPage(TaxYearSelectionPage, CheckMode)(emptyUserAnswers) mustBe
          SessionExpiredController.onPageLoad()
      }

    }
  }
}
