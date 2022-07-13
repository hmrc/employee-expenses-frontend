/*
 * Copyright 2022 HM Revenue & Customs
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

import forms.EmployerContributionFormProvider
import models.{EmployerContribution, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.EmployerContributionView
import views.newBehaviours.OptionsViewBehaviours


class EmployerContributionViewSpec extends OptionsViewBehaviours[EmployerContribution] {

  val messageKeyPrefix = "employerContribution"

  val form = new EmployerContributionFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "EmployerContribution view" must {

    val view = application.injector.instanceOf[EmployerContributionView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithAccountMenu(applyViewWithAuth(form))

      behave like pageWithBackLink(applyView(form))

      behave like optionsPage(form, applyView, EmployerContribution.options)

      behave like pageWithList(applyView(form), messageKeyPrefix, Seq("list.item1", "list.item2", "list.item3"))

  }

  application.stop()

    override def pageWithList(view: HtmlFormat.Appendable,
                            pageKey: String,
                            bulletList: Seq[String]): Unit = {

      "behave like a page with a list" must {

        "have a list" in {

          val doc = asDocument(view)
          assertContainsText(doc, "govuk-list govuk-list--bullet")
        }

        "have correct values" in {

          val doc = asDocument(view)
          bulletList.foreach {
            x => assertContainsMessages(doc, s"$pageKey.$x")
          }
      }
    }
  }
}