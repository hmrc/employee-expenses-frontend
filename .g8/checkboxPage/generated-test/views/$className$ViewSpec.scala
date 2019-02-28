package views

import forms.$className$FormProvider
import models.{$className$, NormalMode}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption
import views.behaviours.CheckboxViewBehaviours
import views.html.$className$View

class $className$ViewSpec extends CheckboxViewBehaviours[$className$] {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val form = new $className$FormProvider()()

  def applyView(form: Form[Seq[$className$]]): HtmlFormat.Appendable =
    application.injector.instanceOf[$className$View].apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[Seq[$className$]]): HtmlFormat.Appendable =
    application.injector.instanceOf[$className$View].apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  val messageKeyPrefix = "$className;format="decap"$"

  val options: Seq[RadioCheckboxOption] = $className$.options

  "$className$View" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like checkboxPage(form, applyView, messageKeyPrefix, options)
  }

  application.stop()
}
