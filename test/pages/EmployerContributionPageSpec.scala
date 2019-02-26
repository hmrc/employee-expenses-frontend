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

import base.SpecBase
import generators.Generators
import models.{EmployerContribution, UserAnswers}
import pages.behaviours.PageBehaviours

import scala.util.Try

class EmployerContributionPageSpec extends PageBehaviours with Generators {

  "EmployerContributionPage" must {

    beRetrievable[EmployerContribution](EmployerContributionPage)

    beSettable[EmployerContribution](EmployerContributionPage)

    beRemovable[EmployerContribution](EmployerContributionPage)

    "remove ExpensesPaid amount when EmployerContribution is None" in {
      val userAnswers: Try[UserAnswers] = emptyUserAnswers.set(ExpensesEmployerPaidPage, 100)
      val updatedUserAnswers = userAnswers.get.set(EmployerContributionPage, EmployerContribution.NoContribution).get

      updatedUserAnswers.get(ExpensesEmployerPaidPage) mustBe None
    }

    "remove ExpensesPaid amount when EmployerContribution is All" in {
      val userAnswers: Try[UserAnswers] = emptyUserAnswers.set(ExpensesEmployerPaidPage, 180)
      val updatedUserAnswers = userAnswers.get.set(EmployerContributionPage, EmployerContribution.All).get

      updatedUserAnswers.get(ExpensesEmployerPaidPage) mustBe None
    }

    "keep ExpensesPaid amount when EmployerContribution is Some" in {
      val userAnswers: Try[UserAnswers] = emptyUserAnswers.set(ExpensesEmployerPaidPage, 100)
      val updatedUserAnswers = userAnswers.get.set(EmployerContributionPage, EmployerContribution.SomeContribution).get

      updatedUserAnswers.get(ExpensesEmployerPaidPage) mustBe Some(100)
    }
  }
}
