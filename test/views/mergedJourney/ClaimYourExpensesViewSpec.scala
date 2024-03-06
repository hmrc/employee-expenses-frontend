/*
 * Copyright 2024 HM Revenue & Customs
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

package views.mergedJourney

import models.mergedJourney._
import views.html.mergedJourney.ClaimYourExpensesView
import views.newBehaviours.ViewBehaviours

class ClaimYourExpensesViewSpec extends ViewBehaviours {

  val view: ClaimYourExpensesView = app.injector.instanceOf[ClaimYourExpensesView]

  "ClaimYourExpensesView" must {
    "have the right title" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrentPrevious,
        ClaimCompletePrevious
      )
      val doc = asDocument(view(testJourney)(fakeRequest, messages))

      assertPageTitleEqualsMessage(doc,
        "claimYourExpenses.title"
      )
    }
    "have the right link when next journey is wfh" when {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimPending,
        ClaimPending,
        ClaimPending
      )
      val applyView = view(testJourney)(fakeRequest, messages)

      behave like pageWithButtonLink(applyView, frontendAppConfig.startUrlWfh, "site.continue")
    }
    "have the right link when next journey is psubs" when {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimPending,
        ClaimPending
      )
      val applyView = view(testJourney)(fakeRequest, messages)

      behave like pageWithButtonLink(applyView, frontendAppConfig.startUrlPsubs, "site.continue")
    }
    "have the right link when next journey is fre" when {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent,
        ClaimPending
      )
      val applyView = view(testJourney)(fakeRequest, messages)

      behave like pageWithButtonLink(applyView, frontendAppConfig.startUrlFre, "site.continue")
    }
    "have the right link when all claims are complete" when {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent
      )
      val applyView = view(testJourney)(fakeRequest, messages)

      behave like pageWithButtonLink(applyView, controllers.mergedJourney.routes.ClaimsCompleteController.show.url, "site.continue")
    }
    "display correct static text when all journeys are complete" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrentPrevious,
        ClaimCompletePrevious
      )
      val doc = asDocument(view(testJourney)(fakeRequest, messages))

      assertContainsMessages(doc,
        "claimYourExpenses.heading",
        "claimYourExpenses.para.1",
        "claimYourExpenses.para.2",
        "claimYourExpenses.para.4"
      )

      assertDoesNotContainMessages(doc,
        "claimYourExpenses.para.3"
      )
    }
    "display correct static text when journeys are pending" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimPending,
        ClaimPending,
        ClaimPending
      )
      val doc = asDocument(view(testJourney)(fakeRequest, messages))

      assertContainsMessages(doc,
        "claimYourExpenses.heading",
        "claimYourExpenses.para.1",
        "claimYourExpenses.para.2",
        "claimYourExpenses.para.3"
      )

      assertDoesNotContainMessages(doc,
        "claimYourExpenses.para.4"
      )
    }
    "display correct timeline" when {
      "all claims are pending" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimPending,
          ClaimPending,
          ClaimPending
        )
        val doc = asDocument(view(testJourney)(fakeRequest, messages))

        assertContainsMessages(doc,
          messages("claimYourExpenses.eventTitle.wfh", 1),
          messages("claimYourExpenses.eventTitle.psubs", 2),
          messages("claimYourExpenses.eventTitle.fre", 3),
          "claimYourExpenses.tag.startYourClaim",
          "claimYourExpenses.tag.cannotStartYet",
          "claimYourExpenses.status.clickContinue",
          "claimYourExpenses.status.pending.psubs",
          "claimYourExpenses.status.pending.fre"
        )
      }
      "all claims are complete" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompletePrevious,
          ClaimCompleteCurrentPrevious
        )
        val doc = asDocument(view(testJourney)(fakeRequest, messages))

        assertContainsMessages(doc,
          messages("claimYourExpenses.eventTitle.wfh", 1),
          messages("claimYourExpenses.eventTitle.psubs", 2),
          messages("claimYourExpenses.eventTitle.fre", 3),
          "claimYourExpenses.tag.claimComplete",
          "claimYourExpenses.status.claimComplete"
        )
      }
      "claims are stopped or unsuccessful" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimSkipped,
          ClaimStopped,
          ClaimUnsuccessful
        )
        val doc = asDocument(view(testJourney)(fakeRequest, messages))

        assertContainsMessages(doc,
          messages("claimYourExpenses.eventTitle.psubs", 1),
          messages("claimYourExpenses.eventTitle.fre", 2),
          "claimYourExpenses.tag.claimStopped",
          "claimYourExpenses.tag.claimUnsuccessful",
          "claimYourExpenses.status.claimStopped",
          "claimYourExpenses.status.claimUnsuccessful"
        )
      }
      "psubs and fre claims are not changed" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimSkipped,
          ClaimNotChanged,
          ClaimNotChanged
        )
        val doc = asDocument(view(testJourney)(fakeRequest, messages))

        assertContainsMessages(doc,
          messages("claimYourExpenses.eventTitle.psubs", 1),
          messages("claimYourExpenses.eventTitle.fre", 2),
          "claimYourExpenses.tag.claimNotChanged",
          "claimYourExpenses.status.claimNotChanged.psubs",
          "claimYourExpenses.status.claimNotChanged.fre"
        )
      }
    }
  }
}
