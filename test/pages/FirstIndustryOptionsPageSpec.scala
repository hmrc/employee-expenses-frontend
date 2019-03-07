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

package pages

import models.{EmployerContribution, FirstIndustryOptions, SecondIndustryOptions, UserAnswers}
import pages.behaviours.PageBehaviours
import pages.construction._

import scala.util.Try

class FirstIndustryOptionsPageSpec extends PageBehaviours {

  "FirstIndustryOptionsPage" must {

    beRetrievable[FirstIndustryOptions](FirstIndustryOptionsPage)
    beSettable[FirstIndustryOptions](FirstIndustryOptionsPage)
    beRemovable[FirstIndustryOptions](FirstIndustryOptionsPage)

    "remove answers stored in the industry group when updated" in {
      val userAnswers = new UserAnswers(userAnswersId)
        .set(FirstIndustryOptionsPage, FirstIndustryOptions.NoneOfAbove).success.value
        .set(SecondIndustryOptionsPage, SecondIndustryOptions.Construction).success.value
        .set(JoinerCarpenterPage, false).success.value
        .set(StoneMasonPage, false).success.value
        .set(ConstructionOccupationList1Page, false).success.value
        .set(ConstructionOccupationList2Page, true).success.value
        .set(EmployerContributionPage, EmployerContribution.SomeContribution).success.value
        .set(ExpensesEmployerPaidPage, 20).success.value

      val updatedUserAnswers = userAnswers.set(FirstIndustryOptionsPage, FirstIndustryOptions.Retail).get


      updatedUserAnswers.data.keys.contains("industry") mustBe false
      updatedUserAnswers.data.keys.contains(ExpensesEmployerPaidPage) mustBe false
      updatedUserAnswers.data.keys.contains(EmployerContributionPage) mustBe false

    }
  }

}
