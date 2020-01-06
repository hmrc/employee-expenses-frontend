/*
 * Copyright 2020 HM Revenue & Customs
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

package views.printing

import controllers.printing.routes
import forms.printing.PrintingOccupationList2FormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.printing.PrintingOccupationList2View


class PrintingOccupationList2ViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "printingOccupationList2"

  val form = new PrintingOccupationList2FormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "PrintingOccupationList2 view" must {

    val view = application.injector.instanceOf[PrintingOccupationList2View]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithList(applyView(form), messageKeyPrefix,
      Seq(
        "occupation1",
        "occupation2",
        "occupation3",
        "occupation4",
        "occupation5"
      )
    )

    behave like yesNoPage(
      form = form,
      createView = applyView,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.PrintingOccupationList2Controller.onSubmit(NormalMode).url,
      legendLabel = Some(messageKeyPrefix + ".radioLabel")
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithBodyText(applyView(form), "printingOccupationList2.listText")
  }

  application.stop()
}
