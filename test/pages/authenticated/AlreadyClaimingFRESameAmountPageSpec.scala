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

package pages.authenticated

import models.AlreadyClaimingFRESameAmount
import models.AlreadyClaimingFRESameAmount.{NoChange, Remove}
import pages.behaviours.PageBehaviours

class AlreadyClaimingFRESameAmountPageSpec extends PageBehaviours {

  "AlreadyClaimingFRESameAmountPage" must {

    beRetrievable[AlreadyClaimingFRESameAmount](AlreadyClaimingFRESameAmountPage)

    beSettable[AlreadyClaimingFRESameAmount](AlreadyClaimingFRESameAmountPage)

    beRemovable[AlreadyClaimingFRESameAmount](AlreadyClaimingFRESameAmountPage)
  }

  "remove YourEmployer and YourAddress when answer is Remove" in {

    val userAnswers = emptyUserAnswers
      .set(AlreadyClaimingFRESameAmountPage, NoChange).success.value
      .set(YourAddressPage, true).success.value
      .set(YourEmployerPage, true).success.value

    val updatedUserAnswers = userAnswers.set(AlreadyClaimingFRESameAmountPage, Remove).get

    updatedUserAnswers.get(YourAddressPage) mustBe None
    updatedUserAnswers.get(YourEmployerPage) mustBe None
  }
}
