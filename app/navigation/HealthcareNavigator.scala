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

package navigation

import controllers.routes
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.healthcare._
import play.api.mvc.Call
import controllers.healthcare.routes._

class HealthcareNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case AmbulanceStaffPage  => ambulanceStaff(NormalMode)
    case HealthcareList1Page => healthcareList1(NormalMode)
    case HealthcareList2Page => _ => routes.ClaimAmountController.onPageLoad(NormalMode)
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case AmbulanceStaffPage  => ambulanceStaff(CheckMode)
    case HealthcareList1Page => healthcareList1(CheckMode)
    case HealthcareList2Page => _ => routes.ClaimAmountController.onPageLoad(CheckMode)
  }

  def ambulanceStaff(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(AmbulanceStaffPage) match {
    case Some(true)  =>
      routes.ClaimAmountController.onPageLoad(mode)
    case Some(false) =>
      HealthcareList1Controller.onPageLoad(mode)
    case _           =>
      routes.SessionExpiredController.onPageLoad()
  }

  def healthcareList1(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(AmbulanceStaffPage) match {
    case Some(true)  =>
      routes.ClaimAmountController.onPageLoad(mode)
    case Some(false) =>
      HealthcareList2Controller.onPageLoad(mode)
    case _           =>
      routes.SessionExpiredController.onPageLoad()
  }
}
