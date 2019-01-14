package views

import controllers.routes
import forms.MyNewPageFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.MyNewPageView

class MyNewPageViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "myNewPage"

  val form = new MyNewPageFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "MyNewPage view" must {

    val view = application.injector.instanceOf[MyNewPageView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.MyNewPageController.onSubmit(NormalMode).url)
  }

  application.stop()
}
