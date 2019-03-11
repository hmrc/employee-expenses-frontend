/*
 * Copyright 2019 HM Revenue & Customs
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

import pages.behaviours.PageBehaviours

class AlreadyClaimingFRESameAmountPageSpec extends PageBehaviours {

  "AlreadyClaimingFREPage" must {

    beRetrievable[Boolean](AlreadyClaimingFRESameAmountPage)

    beSettable[Boolean](AlreadyClaimingFRESameAmountPage)

    beRemovable[Boolean](AlreadyClaimingFRESameAmountPage)
  }

  "remove YourEmployer and YourAddress when answer is Remove" in {

    val userAnswers = emptyUserAnswers
      .set(AlreadyClaimingFRESameAmountPage, true).success.value
      .set(YourAddressPage, true).success.value
      .set(YourEmployerPage, true).success.value

    val updatedUserAnswers = userAnswers.set(AlreadyClaimingFRESameAmountPage, false).get

    updatedUserAnswers.get(YourAddressPage) mustBe None
    updatedUserAnswers.get(YourEmployerPage) mustBe None
  }
}
