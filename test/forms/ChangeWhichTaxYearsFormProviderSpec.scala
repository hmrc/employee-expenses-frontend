package forms

import forms.behaviours.CheckboxFieldBehaviours
import models.ChangeWhichTaxYears
import play.api.data.FormError

class ChangeWhichTaxYearsFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new ChangeWhichTaxYearsFormProvider()()

  ".value" must {

    val fieldName = "value"
    val requiredKey = "changeWhichTaxYears.error.required"

    behave like checkboxField[ChangeWhichTaxYears](
      form,
      fieldName,
      validValues  = ChangeWhichTaxYears.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
