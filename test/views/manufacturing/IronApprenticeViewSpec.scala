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

package views.manufacturing

import controllers.manufacturing.routes
import forms.manufacturing.IronApprenticeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.newBehaviours.YesNoViewBehaviours
import views.html.manufacturing.IronApprenticeView

class IronApprenticeViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "ironApprentice"

  val form = new IronApprenticeFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "IronApprentice view" must {

    val view = application.injector.instanceOf[IronApprenticeView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave.like(normalPage(applyView(form), messageKeyPrefix))

    behave.like(pageWithAccountMenu(applyViewWithAuth(form)))

    behave.like(pageWithBackLink(applyView(form)))

    behave.like(yesNoPage(form, applyView, messageKeyPrefix, routes.IronApprenticeController.onSubmit(NormalMode).url))
  }

  application.stop()
}
