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

package pages.authenticated

import models.AlreadyClaimingFREDifferentAmounts.Change
import models.TaxYearSelection.{CurrentYear, CurrentYearMinus1}
import models.{AlreadyClaimingFRESameAmount, TaxYearSelection, UserAnswers}
import pages.behaviours.PageBehaviours

class TaxYearSelectionPageSpec extends PageBehaviours {

  "TaxYearSelectionPage" must {

    beRetrievable[Seq[TaxYearSelection]](TaxYearSelectionPage)

    beSettable[Seq[TaxYearSelection]](TaxYearSelectionPage)

    beRemovable[Seq[TaxYearSelection]](TaxYearSelectionPage)

    "remove authenticated userAnswers when TaxYearSelectionPage is updated" in {

      val userAnswers = new UserAnswers()
        .set(TaxYearSelectionPage, Seq(CurrentYear))
        .success
        .value
        .set(AlreadyClaimingFREDifferentAmountsPage, Change)
        .success
        .value
        .set(AlreadyClaimingFRESameAmountPage, AlreadyClaimingFRESameAmount.Remove)
        .success
        .value
        .set(ChangeWhichTaxYearsPage, Seq(CurrentYear))
        .success
        .value
        .set(RemoveFRECodePage, CurrentYear)
        .success
        .value
        .set(YourEmployerPage, true)
        .success
        .value

      val updatedUserAnswers = userAnswers.set(TaxYearSelectionPage, Seq(CurrentYearMinus1)).get

      updatedUserAnswers.get(AlreadyClaimingFREDifferentAmountsPage) mustBe None
      updatedUserAnswers.get(AlreadyClaimingFRESameAmountPage) mustBe None
      updatedUserAnswers.get(ChangeWhichTaxYearsPage) mustBe None
      updatedUserAnswers.get(RemoveFRECodePage) mustBe None
      updatedUserAnswers.get(YourEmployerPage) mustBe None
    }

  }

}
