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

import controllers.authenticated.routes._
import forms.authenticated.YourAddressFormProvider
import models.{Address, NormalMode}
import org.jsoup.nodes.Element
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.authenticated.YourAddressView

class YourAddressViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "yourAddress"

  val form = new YourAddressFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "YourAddress view" must {

    val view = application.injector.instanceOf[YourAddressView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, address)(fakeRequest, messages)

    def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, address)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(
      form,
      applyView,
      messageKeyPrefix,
      YourAddressController.onSubmit(NormalMode).url,
      Some(messages(s"$messageKeyPrefix.label", Address.asLabel(address)))
    )

    "behave like page with address" in {
      val doc = asDocument(applyViewWithAuth(form))

      val address: Element = doc.getElementById("citizenDetailsAddress")

      address.text must include ("6 Howsell Road Llanddew Line 3 Line 4 Line 5 DN16 3FB GREAT BRITAIN")
    }
  }

  application.stop()
}
