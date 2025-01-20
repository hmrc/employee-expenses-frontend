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

package views.transport
  import play.twirl.api.Html
  import views.html.transport.UseIformFreOnlyView
  import views.newBehaviours.ViewBehaviours


class  UseIformFreOnlyViewSpec extends ViewBehaviours{

  val messageKeyPrefix = "usePrintAndPostDetailed"

  val application = applicationBuilder()
    .build()


  val view = application.injector.instanceOf[UseIformFreOnlyView]

  def createView(): Html = view.apply()(fakeRequest, messages)

  val applyView = view.apply()(fakeRequest, messages)

  val claimByIformUrl: String = frontendAppConfig.employeeExpensesClaimByIformUrl


  behave like normalPage(applyView, "usePrintAndPostDetailed")

  behave like pageWithBackLink(applyView)


  "when freJourneyEnabled is enabled- all new content is displayed for only uniformsClothingToolsView" in {

    val doc = asDocument(createView())

    assertContainsMessages(doc, messages(s"${messageKeyPrefix}.uniformsClothingTools.1_freOnly_iform"))

  }
  behave like pageWithButtonLink(applyView, claimByIformUrl,"continue")

}
