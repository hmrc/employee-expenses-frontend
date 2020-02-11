/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.police._
import javax.inject.{Inject, Singleton}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.police._
import play.api.mvc.Call

@Singleton
class PoliceNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case SpecialConstablePage         => specialConstable(NormalMode)
    case CommunitySupportOfficerPage  => communitySupportOfficer(NormalMode)
    case MetropolitanPolicePage       => metropolitanPolice(NormalMode)
    case PoliceOfficerPage            => _ => controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
    case _                            => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case SpecialConstablePage         => specialConstable(CheckMode)
    case CommunitySupportOfficerPage  => communitySupportOfficer(CheckMode)
    case MetropolitanPolicePage       => metropolitanPolice(CheckMode)
    case PoliceOfficerPage            => _ => controllers.routes.EmployerContributionController.onPageLoad(CheckMode)
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  def specialConstable(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(SpecialConstablePage) match {
      case Some(true)   => controllers.routes.CannotClaimExpenseController.onPageLoad()
      case Some(false)  => routes.CommunitySupportOfficerController.onPageLoad(mode)
      case _            => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def communitySupportOfficer(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(CommunitySupportOfficerPage) match {
      case Some(true)   => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false)  => routes.MetropolitanPoliceController.onPageLoad(mode)
      case _            => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def metropolitanPolice(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(MetropolitanPolicePage) match {
      case Some(true)    => controllers.routes.CannotClaimExpenseController.onPageLoad()
      case Some(false)   => routes.PoliceOfficerController.onPageLoad(mode)
      case _             => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
