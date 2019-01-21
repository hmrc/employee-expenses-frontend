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

import forms.engineering.ConstructionalEngineeringList2FormProvider
import models.NormalMode
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.engineering.ConstructionalEngineeringList2View

class ConstructionalEngineeringList2ViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "constructionalEngineeringList2"

  val form = new ConstructionalEngineeringList2FormProvider()()

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "ConstructionalEngineeringList2 view" must {

    val view = application.injector.instanceOf[ConstructionalEngineeringList2View]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix,
      controllers.engineering.routes.ConstructionalEngineeringList2Controller.onSubmit(NormalMode).url)

    behave like pageWithList(applyView(form), messageKeyPrefix,
      Seq(
        "occupation1",
        "occupation2",
        "occupation3",
        "occupation4",
        "occupation5",
        "occupation6",
        "occupation7",
        "occupation8",
        "occupation9"
      )
    )

    behave like pageWithSecondaryHeader(applyView(form), messages("constructionalEngineeringList2.secondaryHeading"))
  }

  application.stop()
}