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

package views.manufacturing

import forms.manufacturing.TypeOfManufacturingFormProvider
import models.{NormalMode, TypeOfManufacturing}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.manufacturing.TypeOfManufacturingView

class TypeOfManufacturingViewSpec extends OptionsViewBehaviours[TypeOfManufacturing] {

  val messageKeyPrefix = "typeOfManufacturing"

  val form = new TypeOfManufacturingFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val view = application.injector.instanceOf[TypeOfManufacturingView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  "TypeOfManufacturingView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like normalPageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like optionsPage(form, applyView, TypeOfManufacturing.options)
  }

  application.stop()
}
