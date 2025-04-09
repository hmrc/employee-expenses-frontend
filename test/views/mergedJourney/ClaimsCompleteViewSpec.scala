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
import views.html.mergedJourney.ClaimsCompleteView
import views.newBehaviours.ViewBehaviours

class ClaimsCompleteViewSpec extends ViewBehaviours {

  val view: ClaimsCompleteView = app.injector.instanceOf[ClaimsCompleteView]

  "ClaimsCompleteView" must {
    "have the right title" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrentPrevious,
        ClaimCompletePrevious
      )
      val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

      assertPageTitleEqualsMessage(doc, "claimsComplete.title")
    }
    "have a confirmation panel" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompletePrevious,
        ClaimCompleteCurrentPrevious
      )
      val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))
      assertRenderedByClass(doc, "govuk-panel govuk-panel--confirmation")
    }
    "display the correct list of claims" when {
      "claims are complete" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompletePrevious,
          ClaimCompleteCurrentPrevious
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(
          doc,
          "claimsComplete.para.claims",
          "claimsComplete.list.wfh",
          "claimsComplete.list.psubs",
          "claimsComplete.list.fre"
        )
        assertDoesNotContainMessages(doc, "claimsComplete.para.claimsTried")
      }
      "claims are complete or skipped" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimSkipped,
          ClaimCompleteCurrentPrevious
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(doc, "claimsComplete.para.claims", "claimsComplete.list.wfh", "claimsComplete.list.fre")
        assertDoesNotContainMessages(doc, "claimsComplete.para.claimsTried", "claimsComplete.list.psubs")
      }
      "claims are not made" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimStopped,
          ClaimUnsuccessful,
          ClaimNotChanged
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(
          doc,
          "claimsComplete.para.claimsTried",
          "claimsComplete.list.wfh",
          "claimsComplete.list.psubs",
          "claimsComplete.list.fre"
        )
        assertDoesNotContainMessages(doc, "claimsComplete.para.claims")
      }
      "claims are not made or skipped" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimSkipped,
          ClaimUnsuccessful,
          ClaimNotChanged
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(
          doc,
          "claimsComplete.para.claimsTried",
          "claimsComplete.list.psubs",
          "claimsComplete.list.fre"
        )
        assertDoesNotContainMessages(doc, "claimsComplete.para.claims", "claimsComplete.list.wfh")
      }
    }
    "have the right guidance" when {
      "claims are complete for current year only" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(doc, "claimsComplete.current.heading", "claimsComplete.current.para")
        assertDoesNotContainMessages(
          doc,
          "claimsComplete.previous.heading",
          "claimsComplete.previous.para.claims",
          "claimsComplete.happensNext.heading",
          "claimsComplete.happensNext.para",
          "claimsComplete.current.bullet.1",
          "claimsComplete.previous.para.noClaims"
        )
      }
      "claims are complete for previous years only" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompletePrevious,
          ClaimCompletePrevious,
          ClaimCompletePrevious
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(doc, "claimsComplete.previous.heading", "claimsComplete.previous.para.claims")
        assertDoesNotContainMessages(
          doc,
          "claimsComplete.current.heading",
          "claimsComplete.current.para",
          "claimsComplete.happensNext.heading",
          "claimsComplete.happensNext.para",
          "claimsComplete.current.bullet.1",
          "claimsComplete.current.bullet.2",
          "claimsComplete.previous.para.noClaims"
        )
      }
      "claims are complete for current and previous years" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompletePrevious,
          ClaimCompleteCurrentPrevious
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(
          doc,
          "claimsComplete.current.heading",
          "claimsComplete.current.para",
          "claimsComplete.previous.heading",
          "claimsComplete.previous.para.claims"
        )
        assertDoesNotContainMessages(
          doc,
          "claimsComplete.happensNext.heading",
          "claimsComplete.happensNext.para",
          "claimsComplete.current.bullet.1",
          "claimsComplete.previous.para.noClaims"
        )
      }
      "claims are not made" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimStopped,
          ClaimUnsuccessful,
          ClaimNotChanged
        )
        val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

        assertContainsMessages(
          doc,
          "claimsComplete.happensNext.heading",
          "claimsComplete.happensNext.para",
          "claimsComplete.current.heading",
          "claimsComplete.current.bullet.1",
          "claimsComplete.current.bullet.2",
          "claimsComplete.previous.heading",
          "claimsComplete.previous.para.noClaims"
        )
        assertDoesNotContainMessages(doc, "claimsComplete.previous.para.claims")
      }
    }
    "have the right address information" when {
      "there is an address" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent
        )
        val applyView = view(testJourney, Some(address))(fakeRequest, messages, frontendAppConfig)
        val doc       = asDocument(applyView)

        assertContainsMessages(
          doc,
          "claimsComplete.address.heading",
          "claimsComplete.address.para",
          "claimsComplete.address.updateAddress.start",
          "claimsComplete.address.updateAddress.link",
          "claimsComplete.address.updateAddress.end"
        )
      }
      "there is no address" in {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent
        )
        val applyView = view(testJourney, None)(fakeRequest, messages, frontendAppConfig)
        val doc       = asDocument(applyView)

        assertContainsMessages(
          doc,
          "claimsComplete.address.heading",
          "claimsComplete.noAddress.updateAddress.start",
          "claimsComplete.noAddress.updateAddress.link",
          "claimsComplete.noAddress.updateAddress.end",
          "claimsComplete.noAddress.inset"
        )
      }
    }
    "have the right address link" when {
      "there is an address" when {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent
        )
        val applyView = view(testJourney, Some(address))(fakeRequest, messages, frontendAppConfig)

        behave.like(
          pageWithLink(applyView, frontendAppConfig.updateAddressInfoUrl, "claimsComplete.address.updateAddress.link")
        )
      }
      "there is no address" when {
        val testJourney = MergedJourney(
          userAnswersId,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent,
          ClaimCompleteCurrent
        )
        val applyView = view(testJourney, None)(fakeRequest, messages, frontendAppConfig)

        behave.like(
          pageWithLink(applyView, frontendAppConfig.updateAddressInfoUrl, "claimsComplete.noAddress.updateAddress.link")
        )
      }
    }
    "have the right feedback link" when {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent
      )
      val applyView = view(testJourney, Some(address))(fakeRequest, messages, frontendAppConfig)
      val doc       = asDocument(applyView)

      assertContainsMessages(doc, "claimsComplete.feedback.link", "claimsComplete.feedback.end")

      behave.like(pageWithLink(applyView, frontendAppConfig.feedbackSurveyUrl, "claimsComplete.feedback.link"))
    }
  }

}
