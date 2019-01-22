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
import models.TypeOfEngineering._
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.engineering.ConstructionalEngineeringList1Page
import pages.{Page, TypeOfEngineeringPage}
import play.api.mvc.Call

class EngineeringNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfEngineeringPage => userAnswers => typeOfEngineeringOptions(NormalMode)(userAnswers)
    case ConstructionalEngineeringList1Page => userAnswers => constructionalEngineeringList1(NormalMode)(userAnswers)
    case _ => _ => routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfEngineeringPage => userAnswers => typeOfEngineeringOptions(CheckMode)(userAnswers)
    case _ => _ => routes.SessionExpiredController.onPageLoad()
  }

  private def typeOfEngineeringOptions(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(TypeOfEngineeringPage) match {
      case Some(ConstructionalEngineering) =>
        controllers.engineering.routes.ConstructionalEngineeringList1Controller.onPageLoad(mode)
      case Some(TradesRelatingToEngineering) =>
        controllers.engineering.routes.AncillaryEngineeringWhichTradeController.onPageLoad(mode)
      case Some(FactoryOrWorkshopEngineering) =>
        controllers.engineering.routes.FactoryEngineeringList1Controller.onPageLoad(mode)
      case Some(NoneOfTheAbove) =>
        controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def constructionalEngineeringList1(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ConstructionalEngineeringList1Page) match {
      case Some(true) =>
        controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) =>
        controllers.engineering.routes.ConstructionalEngineeringList2Controller.onPageLoad(mode)
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
