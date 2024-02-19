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

      assertPageTitleEqualsMessage(doc,
        "claimsComplete.title"
      )
    }
    "have the right link when there is an address" when {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent
      )
      val applyView = view(testJourney, Some(address))(fakeRequest, messages, frontendAppConfig)

      behave like pageWithLink(applyView, frontendAppConfig.updateAddressInfoUrl, "claimsComplete.para.updateAddress.link")
    }
    "display correct static text when current and previous year claims were made and there is an address" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrentPrevious,
        ClaimCompletePrevious
      )
      val doc = asDocument(view(testJourney, Some(address))(fakeRequest, messages, frontendAppConfig))

      assertContainsMessages(doc,
        "claimsComplete.heading",
        "claimsComplete.para.claims",
        "claimsComplete.list.wfh",
        "claimsComplete.list.psubs",
        "claimsComplete.list.fre",
        "claimsComplete.heading.current",
        "claimsComplete.para.current",
        "claimsComplete.heading.previous",
        "claimsComplete.para.previous",
        "claimsComplete.heading.address",
        "claimsComplete.para.address",
        "claimsComplete.para.updateAddress.start",
        "claimsComplete.para.updateAddress.link",
        "claimsComplete.para.updateAddress.end"
      )

      assertDoesNotContainMessages(doc,
        "claimsComplete.heading.noAddress",
        "claimsComplete.para.noAddress"
      )
    }
    "display correct static text when current year claims were made and there is an address" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent,
        ClaimCompleteCurrent
      )
      val doc = asDocument(view(testJourney, Some(address))(fakeRequest, messages, frontendAppConfig))

      assertContainsMessages(doc,
        "claimsComplete.heading",
        "claimsComplete.para.claims",
        "claimsComplete.list.wfh",
        "claimsComplete.list.psubs",
        "claimsComplete.list.fre",
        "claimsComplete.heading.current",
        "claimsComplete.para.current",
        "claimsComplete.heading.address",
        "claimsComplete.para.address",
        "claimsComplete.para.updateAddress.start",
        "claimsComplete.para.updateAddress.link",
        "claimsComplete.para.updateAddress.end"
      )

      assertDoesNotContainMessages(doc,
        "claimsComplete.heading.previous",
        "claimsComplete.para.previous",
        "claimsComplete.heading.noAddress",
        "claimsComplete.para.noAddress"
      )
    }
    "display correct static text when previous year claims were made and there is an address" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimCompletePrevious,
        ClaimCompletePrevious,
        ClaimCompletePrevious
      )
      val doc = asDocument(view(testJourney, Some(address))(fakeRequest, messages, frontendAppConfig))

      assertContainsMessages(doc,
        "claimsComplete.heading",
        "claimsComplete.para.claims",
        "claimsComplete.list.wfh",
        "claimsComplete.list.psubs",
        "claimsComplete.list.fre",
        "claimsComplete.heading.previous",
        "claimsComplete.para.previous",
        "claimsComplete.heading.address",
        "claimsComplete.para.address",
        "claimsComplete.para.updateAddress.start",
        "claimsComplete.para.updateAddress.link",
        "claimsComplete.para.updateAddress.end"
      )

      assertDoesNotContainMessages(doc,
        "claimsComplete.heading.current",
        "claimsComplete.para.current",
        "claimsComplete.heading.noAddress",
        "claimsComplete.para.noAddress"
      )
    }
    "display correct static text when there is no address and not all claims were successful" in {
      val testJourney = MergedJourney(
        userAnswersId,
        ClaimSkipped,
        ClaimUnsuccessful,
        ClaimCompleteCurrent
      )
      val doc = asDocument(view(testJourney, None)(fakeRequest, messages, frontendAppConfig))

      assertContainsMessages(doc,
        "claimsComplete.heading",
        "claimsComplete.para.claims",
        "claimsComplete.list.fre",
        "claimsComplete.heading.current",
        "claimsComplete.para.current",
        "claimsComplete.heading.noAddress",
        "claimsComplete.para.noAddress"
      )

      assertDoesNotContainMessages(doc,
        "claimsComplete.list.wfh",
        "claimsComplete.list.psubs",
        "claimsComplete.heading.previous",
        "claimsComplete.para.previous",
        "claimsComplete.heading.address",
        "claimsComplete.para.address",
        "claimsComplete.para.updateAddress.start",
        "claimsComplete.para.updateAddress.link",
        "claimsComplete.para.updateAddress.end"
      )
    }

  }
}
