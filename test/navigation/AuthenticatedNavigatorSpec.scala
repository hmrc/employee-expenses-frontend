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
import models.{CheckMode, FlatRateExpenseOptions, NormalMode, TaxYearSelection}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.FREResponse
import pages.authenticated._

class AuthenticatedNavigatorSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  val navigator = new AuthenticatedNavigator

  "Authenticated Navigator" when {
    "in Normal mode" must {

      "from TaxYearSelection" must {

        "go to YourEmployer when answered and freResponse returns FRENoYears" in {

          val ua = emptyUserAnswers.set(FREResponse, FlatRateExpenseOptions.FRENoYears).success.value

          navigator.nextPage(TaxYearSelectionPage, NormalMode)(ua) mustBe
            YourEmployerController.onPageLoad(NormalMode)
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

        "go to NoCodeChange when answer is true" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, true).success.value

          navigator.nextPage(AlreadyClaimingFRESameAmountPage, NormalMode)(ua) mustBe
            NoCodeChangeController.onPageLoad()
        }

        "go to RemoveFRECode when answer is false" in {
          val ua = emptyUserAnswers.set(AlreadyClaimingFRESameAmountPage, false).success.value

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

      "go to YourEmployer from RemoveFRE page" in {
        val ua = emptyUserAnswers.set(RemoveFRECodePage, TaxYearSelection.CurrentYear).success.value

        navigator.nextPage(RemoveFRECodePage, NormalMode)(ua) mustBe
          CheckYourAnswersController.onPageLoad()
      }

      "go to YourEmployer from ChangeWhichTaxYearsPage" in {
        val ua = emptyUserAnswers.set(ChangeWhichTaxYearsPage, Seq(TaxYearSelection.CurrentYear)).success.value

        navigator.nextPage(ChangeWhichTaxYearsPage, NormalMode)(ua) mustBe
          YourEmployerController.onPageLoad(NormalMode)
      }

      "go to YourAddress from YourEmployer when answered true" in {
        val ua = emptyUserAnswers.set(YourEmployerPage, true).success.value

        navigator.nextPage(YourEmployerPage, NormalMode)(ua) mustBe
          YourAddressController.onPageLoad(NormalMode)
      }

      "go to UpdateEmployerInformation from YourEmployer when answered false" in {
        val ua = emptyUserAnswers.set(YourEmployerPage, false).success.value

        navigator.nextPage(YourEmployerPage, NormalMode)(ua) mustBe
          UpdateEmployerInformationController.onPageLoad()
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
    }

    "in CheckMode" must {
      "go to CheckYourAnswers from UpdateYourAddressPage" in {
        navigator.nextPage(UpdateYourAddressPage, CheckMode)(emptyUserAnswers) mustBe
          CheckYourAnswersController.onPageLoad()
      }
    }
  }

}
