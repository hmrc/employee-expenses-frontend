/*
 * Copyright 2021 HM Revenue & Customs
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

package views.clothing

import controllers.clothing.routes
import forms.clothing.ClothingFormProvider
import models.NormalMode
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.clothing.ClothingView

class ClothingViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "clothing"

  val form = new ClothingFormProvider()()

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "Clothing view" must {

    val view = application.injector.instanceOf[ClothingView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(
      form = form,
      createView = applyView,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.ClothingController.onSubmit(NormalMode).url,
      legendLabel = Some(messageKeyPrefix + ".radioLabel")

    )

    behave like pageWithList(applyView(form), messageKeyPrefix,
      Seq(
        "occupation1",
        "occupation2",
        "occupation3"
      )
    )
    behave like pageWithBodyText(applyView(form), "aluminiumOccupationList1.listText")


  }

  application.stop()
}
