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

import views.behaviours.ViewBehaviours
import views.html.AccessibilityStatementView

class AccessibilityStatementViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "AccessibilityStatement view" must {

    val view = application.injector.instanceOf[AccessibilityStatementView]

    val applyView = view.apply("", "")(fakeRequest, messages)

    val applyViewWithAuth = view.apply("", "")(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    val introductionLink = s"""<a href=${frontendAppConfig.accessibilityStatementUrl}>${messages("accessibilityStatement.introduction.paragraph2.linkText")}</a>"""

    val expectedGuidanceKeys = Seq(
      messages("accessibilityStatement.title", messages("site.service_name")),
      messages("accessibilityStatement.heading", messages("site.service_name")),
      "accessibilityStatement.introduction.paragraph1",
      messages("accessibilityStatement.introduction.paragraph2", introductionLink),
      messages("accessibilityStatement.introduction.paragraph3", messages("site.service_name"), ""),
      "accessibilityStatement.usingThisService.heading",
      "accessibilityStatement.usingThisService.aboutTheService",
      "accessibilityStatement.usingThisService.paragraph1",
      "accessibilityStatement.usingThisService.listItem1",
      "accessibilityStatement.usingThisService.listItem2",
      "accessibilityStatement.usingThisService.listItem3",
      "accessibilityStatement.usingThisService.listItem4",
      "accessibilityStatement.usingThisService.listItem5",
      "accessibilityStatement.usingThisService.paragraph2",
      messages("accessibilityStatement.usingThisService.paragraph3", messages("accessibilityStatement.usingThisService.paragraph3.linkText")),
      "accessibilityStatement.howAccessibleThisServiceIs.heading",
      "accessibilityStatement.howAccessibleThisServiceIs.paragraph1",
      "accessibilityStatement.howAccessibleThisServiceIs.paragraph1.linkText",
      "accessibilityStatement.whatToDoIfYouHaveDifficultyUsingThisService.heading",
      "accessibilityStatement.reportingAccessibilityProblemsWithThisService.heading",
      "accessibilityStatement.reportingAccessibilityProblemsWithThisService.paragraph1",
      "accessibilityStatement.reportingAccessibilityProblemsWithThisService.paragraph1.linkText",
      "accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.heading",
      "accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.paragraph1",
      "accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.paragraph1.linkText1",
      "accessibilityStatement.whatToDoIfYouAreNotHappyWithHowWeRespondToYourComplaint.paragraph1.linkText2",
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.heading",
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph1",
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph2",
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph3",
      "accessibilityStatement.contactingUsByPhoneOrGettingAVisitFromUsInPerson.paragraph3.linkText",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.heading",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.paragraph1",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.compliant",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.nonCompliant",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.paragraph2.linkText",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.nonCompliant.heading",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.nonCompliant.paragraph1",
      "accessibilityStatement.technicalInformationAboutThisServicesAccessibility.nonCompliant.subHeading",
      "accessibilityStatement.howWeTestedThisService.heading",
      "accessibilityStatement.howWeTestedThisService.paragraph1",
      "accessibilityStatement.howWeTestedThisService.paragraph2",
      "accessibilityStatement.howWeTestedThisService.paragraph2.linkText",
      "accessibilityStatement.howWeTestedThisService.paragraph3"
    )

    //behave like normalPage(applyView, "accessibilityStatement")

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(applyView)
          assertRenderedById(doc, "pageTitle")
        }

        "hide account menu when user not logged in" in {

          val doc = asDocument(applyView)
          doc.getElementById("hideAccountMenu").text mustBe "true"
        }

        "display the correct browser title" in {

          val doc = asDocument(applyView)
          assertEqualsMessage(
            doc,
            "title",
            s"${messages(s"accessibilityStatement.title")} - ${frontendAppConfig.serviceTitle}"
          )
        }

        "display the correct page title" in {

          val doc = asDocument(applyView)
          assertPageTitleEqualsMessage(doc, s"accessibilityStatement.heading")
        }

        "display the correct guidance" in {

          val doc = asDocument(applyView)
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$key"))
        }

        "display language toggles" in {

          val doc = asDocument(applyView)
          assertRenderedById(doc, "langSelector")
        }
      }
    }

    behave like pageWithAccountMenu(applyViewWithAuth)

    behave like pageWithBackLink(applyView)
  }

  application.stop()
}
