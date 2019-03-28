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

import generators.Generators
import models.UserAnswers
import pages.behaviours.PageBehaviours

import scala.util.Try

class EmployerContributionPageSpec extends PageBehaviours with Generators {

  private val userAnswers: Try[UserAnswers] = emptyUserAnswers
    .set(ExpensesEmployerPaidPage, 100)
    .flatMap(_.set(SameEmployerContributionAllYearsPage, true))

  "EmployerContributionPage" must {

    beRetrievable[Boolean](EmployerContributionPage)

    beSettable[Boolean](EmployerContributionPage)

    beRemovable[Boolean](EmployerContributionPage)

    "remove ExpensesPaid and SameEmployerContributionAllYears when EmployerContribution is False" in {

      val updatedUserAnswers = userAnswers.flatMap(_.set(EmployerContributionPage, false)).get

      updatedUserAnswers.get(ExpensesEmployerPaidPage) mustBe None
      updatedUserAnswers.get(SameEmployerContributionAllYearsPage) mustBe None
    }


    "keep ExpensesPaid amount when EmployerContribution is True" in {
      val updatedUserAnswers = userAnswers.get.set(EmployerContributionPage, true).get

      updatedUserAnswers.get(ExpensesEmployerPaidPage) mustBe Some(100)
      updatedUserAnswers.get(SameEmployerContributionAllYearsPage) mustBe Some(true)

    }
  }
}
