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

import controllers.routes._
import controllers.healthcare.routes._
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.healthcare._
import play.api.mvc.Call

class HealthcareNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case AmbulanceStaffPage  => ambulanceStaff(NormalMode)
    case HealthcareList1Page => healthcareList1(NormalMode)
    case HealthcareList2Page => healthcareList2(NormalMode)
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case AmbulanceStaffPage  => ambulanceStaff(CheckMode)
    case HealthcareList1Page => healthcareList1(CheckMode)
    case HealthcareList2Page => healthcareList2(CheckMode)
  }

  def ambulanceStaff(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(AmbulanceStaffPage) match {
    case Some(true)  => EmployerContributionController.onPageLoad(mode)
    case Some(false) => HealthcareList1Controller.onPageLoad(mode)
    case _           => SessionExpiredController.onPageLoad()
  }

  def healthcareList1(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(HealthcareList1Page) match {
    case Some(true)  => EmployerContributionController.onPageLoad(mode)
    case Some(false) => HealthcareList2Controller.onPageLoad(mode)
    case _           => SessionExpiredController.onPageLoad()
  }

  def healthcareList2(mode: Mode)(userAnswers: UserAnswers): Call = userAnswers.get(HealthcareList2Page) match {
    case Some(true) | Some(false) => EmployerContributionController.onPageLoad(mode)
    case _                        => SessionExpiredController.onPageLoad()
  }
}
