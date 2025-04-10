/*
 * Copyright 2023 HM Revenue & Customs
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

package forms.transport

import forms.TransportVehicleTradeFormProvider
import forms.behaviours.OptionFieldBehaviours
import models.TransportVehicleTrade
import play.api.data.FormError

class TransportVehicleTradeFormProviderSpec extends OptionFieldBehaviours {

  val form = new TransportVehicleTradeFormProvider()()

  ".value" must {

    val fieldName   = "value"
    val requiredKey = "transportVehicleTrade.error.required"

    behave.like(
      optionsField[TransportVehicleTrade](
        form,
        fieldName,
        validValues = TransportVehicleTrade.values,
        invalidError = FormError(fieldName, "error.invalid")
      )
    )

    behave.like(
      mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey)
      )
    )
  }

}
