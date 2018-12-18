/*
 * Copyright 2018 HM Revenue & Customs
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

package forms

import forms.behaviours.OptionFieldBehaviours
import models.Checkbox
import models.Checkbox.Option1
import play.api.data.{Form, FormError}

class CheckboxFormProviderSpec extends OptionFieldBehaviours {

  val form: Form[Set[Checkbox]] = new CheckboxFormProvider()()

  ".value" must {

    val fieldName = "value[0]"
    val requiredKey = "checkbox.error.required"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "bind all valid values" in {

      for (value <- Set(Option1)) {

        val result = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
        result.value.value shouldEqual value.toString
      }
    }

    "not bind invalid values" in {

      val generator = stringsExceptSpecificValues(Checkbox.values.map(_.toString))

      forAll(generator -> "invalidValue") {
        value =>
          val result = form.bind(Map(fieldName -> value)).apply(fieldName)
          result.errors shouldEqual Seq(FormError(fieldName, "error.invalid"))
      }
    }

  }
}
