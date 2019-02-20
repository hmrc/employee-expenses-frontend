package views

import forms.ChangeWhichTaxYearsFormProvider
import models.{ChangeWhichTaxYears, NormalMode}
import play.api.Application
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption
import views.behaviours.CheckboxViewBehaviours
import views.html.ChangeWhichTaxYearsView

class ChangeWhichTaxYearsViewSpec extends CheckboxViewBehaviours[ChangeWhichTaxYears] {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val form = new ChangeWhichTaxYearsFormProvider()()

  def applyView(form: Form[Seq[ChangeWhichTaxYears]]): HtmlFormat.Appendable =
    application.injector.instanceOf[ChangeWhichTaxYearsView].apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[Seq[ChangeWhichTaxYears]]): HtmlFormat.Appendable =
    application.injector.instanceOf[ChangeWhichTaxYearsView].apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  val messageKeyPrefix = "changeWhichTaxYears"

  val options: Seq[RadioCheckboxOption] = ChangeWhichTaxYears.options

  "ChangeWhichTaxYearsView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like normalPageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like checkboxPage(form, applyView, messageKeyPrefix, options)
  }

  application.stop()
}
