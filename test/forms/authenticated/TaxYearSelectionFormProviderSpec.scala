package forms.authenticated

import forms.behaviours.CheckboxFieldBehaviours
import models.TaxYearSelection
import play.api.data.FormError

class TaxYearSelectionFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new TaxYearSelectionFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "taxYearSelection.error.required"

    behave like checkboxField[TaxYearSelection](
      form,
      fieldName,
      validValues  = TaxYearSelection.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
