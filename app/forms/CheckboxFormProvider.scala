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
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, FormError}

class CheckboxFormProvider extends Mappings {

  private def checkboxFormatter: Formatter[Checkbox.Value] = new Formatter[Checkbox.Value] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Checkbox.Value] = data.get(key) match {
      case Some(s) => Right(Checkbox.withName(s))
      case None => produceError(key, "error.blank")
      case _ => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: Checkbox.Value) = Map(key -> value.toString)
  }

  private def constraint: Constraint[Seq[Checkbox.Value]] = Constraint {
    case set if set.nonEmpty =>
      Valid
    case _ =>
      Invalid("error.blank")
  }

  def produceError(key: String, error: String) = Left(Seq(FormError(key, error)))

  def apply(): Form[Seq[Checkbox.Value]] =
    Form(
      "value" -> seq(of(checkboxFormatter)).verifying(constraint)
    )
}
