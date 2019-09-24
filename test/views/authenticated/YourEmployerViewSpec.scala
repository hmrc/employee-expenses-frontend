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

package views.authenticated

import controllers.authenticated.routes
import forms.authenticated.YourEmployerFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.authenticated.YourEmployerView

class YourEmployerViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "yourEmployer"

  val form = new YourEmployerFormProvider()()
  val employerName = Seq("HMRC Longbenton")

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "YourEmployer view" must {

    val view = application.injector.instanceOf[YourEmployerView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, employerName)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, employerName)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(
      form,
      applyView,
      messageKeyPrefix,
      routes.YourEmployerController.onSubmit().url,
      Some(messages(s"$messageKeyPrefix.label", employerName))
    )

    behave like pageWithBodyText(applyView(form), employerName.head.toString)
  }

  application.stop()
}

