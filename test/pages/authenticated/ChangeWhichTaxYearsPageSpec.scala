/*
 * Copyright 2020 HM Revenue & Customs
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

import models.TaxYearSelection
import pages.behaviours.PageBehaviours

class ChangeWhichTaxYearsPageSpec extends PageBehaviours {

  "ChangeWhichTaxYearsPage" must {

    beRetrievable[Seq[TaxYearSelection]](ChangeWhichTaxYearsPage)

    beSettable[Seq[TaxYearSelection]](ChangeWhichTaxYearsPage)

    beRemovable[Seq[TaxYearSelection]](ChangeWhichTaxYearsPage)

    "remove RemoveFRECode when ChangeWhichTaxYears is set" in {

      val userAnswers = emptyUserAnswers.set(RemoveFRECodePage, TaxYearSelection.CurrentYear).success.value

      val result = userAnswers.set(ChangeWhichTaxYearsPage, Seq(TaxYearSelection.CurrentYear)).success.value

      result.get(RemoveFRECodePage) mustNot be(defined)
    }
  }
}
