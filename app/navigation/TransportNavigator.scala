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

import controllers.transport.routes
import javax.inject.Inject
import models.TypeOfTransport._
import models._
import pages.Page
import pages.transport.TypeOfTransportPage
import play.api.mvc.Call

class TransportNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfTransportPage => userAnswers => typeOfTransportOptions(NormalMode)(userAnswers)
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def typeOfTransportOptions(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(TypeOfTransportPage) match {
      case Some(Airlines) => routes.AirlineJobListController.onPageLoad(mode)
      case Some(PublicTransport) => routes.GarageHandOrCleanerController.onPageLoad(mode)
      case Some(Railways) => routes.WhichRailwayTradeController.onPageLoad(mode)
      case Some(SeamanCarpenter) => routes.TransportCarpenterController.onPageLoad(mode)
      case Some(Vehicles) => routes.TransportVehicleTradeController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
