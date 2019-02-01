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

import forms.engineering.TypeOfEngineeringFormProvider
import models.{NormalMode, TypeOfEngineering}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.engineering.TypeOfEngineeringView

class TypeOfEngineeringViewSpec extends OptionsViewBehaviours[TypeOfEngineering] {

  val messageKeyPrefix = "typeOfEngineering"

  val form = new TypeOfEngineeringFormProvider()()

  val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val view: TypeOfEngineeringView = application.injector.instanceOf[TypeOfEngineeringView]

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, hc)

  def applyViewWithAuth(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, hcWithAuth)

  "TypeOfEngineeringView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like normalPageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like optionsPage(form, applyView, TypeOfEngineering.options)
  }

  application.stop()
}
