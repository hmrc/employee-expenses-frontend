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

import controllers.manufacturing.routes
import javax.inject.Inject
import models.TypeOfManufacturing._
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.manufacturing._
import play.api.mvc.Call

class ManufacturingNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfManufacturingPage => TypeOfManufacturing(NormalMode)
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  def TypeOfManufacturing(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(TypeOfManufacturingPage) match {
      case Some(Aluminium) => controllers.manufacturing.routes.AluminiumOccupationList1Controller.onPageLoad(mode)
      case Some(BrassCopper) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(Glass) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(IronSteel) => controllers.manufacturing.routes.IronSteelOccupationListController.onPageLoad(mode)
      case Some(PreciousMetals) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(WoodFurniture) => controllers.manufacturing.routes.WoodFurnitureOccupationList1Controller.onPageLoad(mode)
      case Some(NoneOfAbove) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
