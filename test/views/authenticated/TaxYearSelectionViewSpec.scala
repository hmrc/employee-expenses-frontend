package views.authenticated

import forms.authenticated.TaxYearSelectionFormProvider
import models.{NormalMode, TaxYearSelection}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioCheckboxOption
import views.behaviours.CheckboxViewBehaviours

class TaxYearSelectionViewSpec extends CheckboxViewBehaviours[TaxYearSelection] {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  val form = new TaxYearSelectionFormProvider()()

  def applyView(form: Form[Seq[TaxYearSelection]]): HtmlFormat.Appendable =
    application.injector.instanceOf[TaxYearSelectionView].apply(form, NormalMode)(fakeRequest, messages)

  def applyViewWithAuth(form: Form[Seq[TaxYearSelection]]): HtmlFormat.Appendable =
    application.injector.instanceOf[TaxYearSelectionView].apply(form, NormalMode)(fakeRequest.withSession(("authToken", "SomeAuthToken")), messages)

  val messageKeyPrefix = "taxYearSelection"

  val options: Set[RadioCheckboxOption] = TaxYearSelection.options

  "TaxYearSelectionView" must {

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like normalPageWithAccountMenu(applyViewWithAuth(form))

    behave like pageWithBackLink(applyView(form))

    behave like checkboxPage(form, applyView, messageKeyPrefix, options)
  }

  application.stop()
}
