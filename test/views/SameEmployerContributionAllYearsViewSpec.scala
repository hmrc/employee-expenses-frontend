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

package views

import forms.SameEmployerContributionAllYearsFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.newBehaviours.YesNoViewBehaviours
import views.html.SameEmployerContributionAllYearsView

class SameEmployerContributionAllYearsViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "sameEmployerContributionAllYears"

  val form = new SameEmployerContributionAllYearsFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val contribution = 10

  "SameEmployerContributionAllYears view" must {

    val view = application.injector.instanceOf[SameEmployerContributionAllYearsView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, contribution)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, contribution)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave.like(pageWithAccountMenu(applyViewWithAuth(form)))

    behave.like(pageWithBackLink(applyView(form)))

    "behave like a normal page" when {

      "rendered" must {

        "have the correct banner title" in {

          val doc = asDocument(applyView(form))
          assertRenderedByCssSelector(doc, "div.govuk-header__content")
        }

        "hide account menu when user not logged in" in {

          val doc = asDocument(applyView(form))
          assertNotRenderedById(doc, "secondary-nav")
        }

        "display the correct browser title" in {

          val doc = asDocument(applyView(form))
          assertEqualsMessage(
            doc,
            "title",
            s"${messages(s"$messageKeyPrefix.title", contribution)} - ${frontendAppConfig.serviceTitle}"
          )
        }

        "display the correct page title" in {

          val doc = asDocument(applyView(form))
          assertPageTitleEqualsMessage(doc, messages(s"$messageKeyPrefix.heading", contribution))
        }

        "display language toggles" in {

          val doc = asDocument(applyView(form))
          assertRenderedByCssSelector(doc, "nav.hmrc-language-select")
        }
      }
    }

    "behave like a page with a Yes/No question" when {
      "rendered" must {
        "contain an input for the value" in {

          val doc = asDocument(applyView(form))
          assertRenderedById(doc, "value")
          assertRenderedById(doc, "value-2")
        }

        "have no values checked when rendered with no form" in {

          val doc = asDocument(applyView(form))
          assert(!doc.getElementById("value").hasAttr("checked"))
          assert(!doc.getElementById("value-2").hasAttr("checked"))
        }

        "not render an error summary" in {

          val doc = asDocument(applyView(form))
          assertNotRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
        }
      }

      "rendered with a value of true" must
        behave.like(answeredYesNoPage(applyView, true))

      "rendered with a value of false" must
        behave.like(answeredYesNoPage(applyView, false))

      "rendered with an error" must {

        "show an error summary" in {

          val doc = asDocument(applyView(form.withError(error)))
          assertRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
        }

        "show an error in the value field's label" in {

          val doc       = asDocument(applyView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe "Error: " + messages(errorMessage)
        }

        "show an error prefix in the browser title" in {

          val doc = asDocument(applyView(form.withError(error)))
          assertEqualsValue(
            doc,
            "title",
            s"""${messages("error.browser.title.prefix")} ${messages(
                s"$messageKeyPrefix.title",
                contribution
              )} - ${frontendAppConfig.serviceTitle}"""
          )
        }
      }
    }
  }

  override def answeredYesNoPage(createView: Form[Boolean] => HtmlFormat.Appendable, answer: Boolean): Unit = {

    "have only the correct value checked" in {

      val doc = asDocument(createView(form.fill(answer)))
      assert(doc.getElementById("value").hasAttr("checked") == answer)
      assert(doc.getElementById("value-2").hasAttr("checked") != answer)
    }

    "not render an error summary" in {

      val doc = asDocument(createView(form.fill(answer)))
      assertNotRenderedByCssSelector(doc, "#main-content > div > div > form > div.govuk-error-summary > div > h2")
    }
  }

  application.stop()
}
