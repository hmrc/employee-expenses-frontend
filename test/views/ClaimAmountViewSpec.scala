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

package views

import models.UserAnswers
import pages.ClaimAmount
import play.api.libs.json.Json
import views.behaviours.ViewBehaviours
import views.html.ClaimAmountView

class ClaimAmountViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ClaimAmount view" must {

    val claimAmount: Int = 180
    val employerContribution: Option[Int] = None
    val someEmployerContribution: Option[Int] = Some(10)
    val band1 : Int = 20
    val calculatedBand1 : String = "36.00"
    val band2 : Int = 40
    val calculatedBand2 : String = "72.00"
    val scotlandBand1 : Int = 19
    val calculatedScotlandBand1 : String = "36.00"
    val scotlandBand2 : Int = 41
    val calculatedScotlandBand2 : String = "72.00"
    val onwardRoute: String = "/employee-expenses/which-tax-year"

    val messageKeyPrefix = "claimAmount"

    def userAnswers = UserAnswers(userAnswersId, Json.obj(ClaimAmount.toString -> claimAmount))

    val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

    val view = application.injector.instanceOf[ClaimAmountView]

    val applyView = view.apply(
      claimAmount, employerContribution, band1, calculatedBand1, band2, calculatedBand2,
      scotlandBand1, calculatedScotlandBand1, scotlandBand2, calculatedScotlandBand2, onwardRoute
    )(fakeRequest, messages)

    val applyViewWithEmployerContribution = view.apply(
      claimAmount, someEmployerContribution, band1, calculatedBand1, band2, calculatedBand2,
      scotlandBand1, calculatedScotlandBand1, scotlandBand2, calculatedScotlandBand2, onwardRoute
    )(fakeRequest, messages)

    val applyViewWithAuth = view.apply(
      claimAmount, employerContribution, band1, calculatedBand1, band2, calculatedBand2,
      scotlandBand1, calculatedScotlandBand1, scotlandBand2, calculatedScotlandBand2, onwardRoute
    )(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView, "claimAmount")

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(applyView)

    "display correct text when some employer contribution" in {
      val doc = asDocument(applyViewWithEmployerContribution)
      assertContainsText(doc, messages("claimAmount.someContributionDescription", claimAmount - someEmployerContribution.get))
      assertContainsText(doc, messages("claimAmount.employerContributionDescription"))
    }

    "display correct text when no employer contribution" in {
      val doc = asDocument(applyView)
      assertContainsText(doc, messages("claimAmount.noContributionDescription", claimAmount))
    }

    behave like pageWithBodyText(
      applyView,
      "claimAmount.description",
      "claimAmount.englandHeading",
      "claimAmount.scotlandHeading"
    )

    "display relevant data" in {
      val doc = asDocument(applyView)
      assertContainsText(doc, messages("claimAmount.band1", calculatedBand1, band1))
      assertContainsText(doc, messages("claimAmount.band2", calculatedBand2, band2))
      assertContainsText(doc, messages("claimAmount.scotlandBand1", calculatedScotlandBand1, scotlandBand1))
      assertContainsText(doc, messages("claimAmount.scotlandBand2", calculatedScotlandBand2, scotlandBand2))
    }

    behave like pageWithButtonLink(applyView, onwardRoute, "site.continue")
  }

  application.stop()
}
