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

package views.engineering

import controllers.engineering.routes
import forms.engineering.ConstructionalEngineeringList3FormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.engineering.ConstructionalEngineeringList3View

class ConstructionalEngineeringList3ViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "constructionalEngineeringList3"

  val form = new ConstructionalEngineeringList3FormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ConstructionalEngineeringList3 view" must {

    val view = application.injector.instanceOf[ConstructionalEngineeringList3View]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like normalPageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(
      form = form,
      createView = applyView,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.ConstructionalEngineeringList3Controller.onSubmit(NormalMode).url,
      legendLabel = Some(messageKeyPrefix + ".radioLabel"))

    behave like pageWithList(applyView(form), messageKeyPrefix,
      Seq(
        "occupation1",
        "occupation2",
        "occupation3",
        "occupation4"
      )
    )

    behave like pageWithBodyText(applyView(form), "constructionalEngineeringList3.listText")
  }

  application.stop()
}
