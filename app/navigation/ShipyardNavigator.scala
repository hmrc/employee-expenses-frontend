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

import controllers.routes
import controllers.routes._
import javax.inject.{Inject, Singleton}
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.shipyard._
import play.api.mvc.Call

@Singleton
class ShipyardNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case ShipyardApprenticeStorekeeperPage   => shipyardApprenticeStoreKeeper(NormalMode)
    case ShipyardOccupationList1Page => shipyardOccupationList1(NormalMode)
    case ShipyardOccupationList2Page => shipyardOccupationList2(NormalMode)
    case LabourerPage                => _ => EmployerContributionController.onPageLoad(NormalMode)
    case _ => _                      => routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case ShipyardApprenticeStorekeeperPage   => shipyardApprenticeStoreKeeper(CheckMode)
    case ShipyardOccupationList1Page => shipyardOccupationList1(CheckMode)
    case ShipyardOccupationList2Page => shipyardOccupationList2(CheckMode)
    case LabourerPage                => _ => EmployerContributionController.onPageLoad(CheckMode)
    case _ => _                      => routes.SessionExpiredController.onPageLoad()
  }


  def shipyardApprenticeStoreKeeper(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ShipyardApprenticeStorekeeperPage) match {
      case Some(true) => EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.shipyard.routes.ShipyardOccupationList1Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def shipyardOccupationList1(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ShipyardOccupationList1Page) match {
      case Some(true) => EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.shipyard.routes.ShipyardOccupationList2Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def shipyardOccupationList2(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ShipyardOccupationList2Page) match {
      case Some(true) => EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.shipyard.routes.LabourerController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
