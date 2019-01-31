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

import controllers.police._
import javax.inject.Inject
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.police.{CommunitySupportOfficerPage, SpecialConstablePage}
import play.api.mvc.Call

class PoliceNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case SpecialConstablePage => specialConstable(NormalMode)
    case CommunitySupportOfficerPage => communitySupportOfficer(NormalMode)
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  def specialConstable(mode: Mode)(userAnswers: UserAnswers) = {
    userAnswers.get(SpecialConstablePage) match {

      case Some(true) => ???
      case Some(false) => routes.CommunitySupportOfficerController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()

    }
  }

  def communitySupportOfficer(mode: Mode)(userAnswers: UserAnswers) = {
    userAnswers.get(CommunitySupportOfficerPage) match {

      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => routes.MetropolitanPoliceController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()

    }
  }

}
