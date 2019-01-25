package views

import controllers.routes
import forms.manufacturing.IronSteelOccupationFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IronSteelOccupationView

class IronSteelOccupationViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "ironSteelOccupation"

  val form = new IronSteelOccupationFormProvider()()

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "IronSteelOccupation view" must {

    val view = application.injector.instanceOf[IronSteelOccupationView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IronSteelOccupationController.onSubmit(NormalMode).url)
  }

  application.stop()
}
