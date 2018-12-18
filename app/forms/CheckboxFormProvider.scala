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

import forms.mappings.Mappings
import models.Checkbox
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}

class CheckboxFormProvider extends Mappings {


  private def constraint: Constraint[Set[Checkbox]] = Constraint {
    case set: Set[_] if set.nonEmpty =>
      Valid
    case set: Set[_] if set.isEmpty =>
      Invalid("checkbox.error.required")
    case _ =>
      Invalid("error.invalid")
  }

  def apply(): Form[Set[Checkbox]] =
    Form(
      "value" -> set(enumerable[Checkbox]( "checkbox.error.required")).verifying(constraint)
    )
}
