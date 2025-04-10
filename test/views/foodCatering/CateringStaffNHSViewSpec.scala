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

package views.foodCatering

import controllers.foodCatering.routes
import forms.foodCatering.CateringStaffNHSFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.newBehaviours.YesNoViewBehaviours
import views.html.foodCatering.CateringStaffNHSView

class CateringStaffNHSViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "cateringStaffNHS"

  val form = new CateringStaffNHSFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "CateringStaffNHS view" must {

    val view = application.injector.instanceOf[CateringStaffNHSView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave.like(normalPage(applyView(form), messageKeyPrefix))

    behave.like(pageWithAccountMenu(applyViewWithAuth(form)))

    behave.like(pageWithBackLink(applyView(form)))

    behave.like(
      yesNoPage(form, applyView, messageKeyPrefix, routes.CateringStaffNHSController.onSubmit(NormalMode).url)
    )
  }

  application.stop()
}
