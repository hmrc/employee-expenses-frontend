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
import pages.engineering._
import pages.Page
import play.api.mvc.Call

class EngineeringNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfEngineeringPage => typeOfEngineeringOptions(NormalMode)
    case ConstructionalEngineeringList1Page => constructionalEngineeringList1(NormalMode)
    case ConstructionalEngineeringList2Page => constructionalEngineeringList2(NormalMode)
    case ConstructionalEngineeringList3Page => constructionalEngineeringList3(NormalMode)
    case ConstructionalEngineeringApprenticePage => constructionalEngineeringApprentice(NormalMode)
    case AncillaryEngineeringWhichTradePage => ancillaryEngineeringWhichTrade(NormalMode)
    case FactoryEngineeringList1Page => factoryEngineeringList1(NormalMode)
    case FactoryEngineeringList2Page => factoryEngineeringList2(NormalMode)
    case FactoryEngineeringApprenticePage => factoryEngineeringApprentice(NormalMode)
    case _ => _ => routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfEngineeringPage => typeOfEngineeringOptions(CheckMode)
    case ConstructionalEngineeringList1Page => constructionalEngineeringList1(CheckMode)
    case ConstructionalEngineeringList2Page => constructionalEngineeringList2(CheckMode)
    case ConstructionalEngineeringList3Page => constructionalEngineeringList3(CheckMode)
    case ConstructionalEngineeringApprenticePage => constructionalEngineeringApprentice(CheckMode)
    case AncillaryEngineeringWhichTradePage => ancillaryEngineeringWhichTrade(CheckMode)
    case FactoryEngineeringList1Page => factoryEngineeringList1(CheckMode)
    case FactoryEngineeringList2Page => factoryEngineeringList2(CheckMode)
    case FactoryEngineeringApprenticePage => factoryEngineeringApprentice(CheckMode)
    case _ => _ => routes.SessionExpiredController.onPageLoad()
  }

  private def typeOfEngineeringOptions(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(TypeOfEngineeringPage) match {
      case Some(ConstructionalEngineering) => controllers.engineering.routes.ConstructionalEngineeringList1Controller.onPageLoad(mode)
      case Some(TradesRelatingToEngineering) => controllers.engineering.routes.AncillaryEngineeringWhichTradeController.onPageLoad(mode)
      case Some(FactoryOrWorkshopEngineering) => controllers.engineering.routes.FactoryEngineeringList1Controller.onPageLoad(mode)
      case Some(NoneOfTheAbove) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def constructionalEngineeringList1(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ConstructionalEngineeringList1Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.engineering.routes.ConstructionalEngineeringList2Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def constructionalEngineeringList2(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ConstructionalEngineeringList2Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.engineering.routes.ConstructionalEngineeringList3Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def constructionalEngineeringList3(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ConstructionalEngineeringList3Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.engineering.routes.ConstructionalEngineeringApprenticeController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def constructionalEngineeringApprentice(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(ConstructionalEngineeringApprenticePage) match {
      case Some(true) | Some(false) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def ancillaryEngineeringWhichTrade(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(AncillaryEngineeringWhichTradePage) match {
      case Some(_) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def factoryEngineeringList1(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(FactoryEngineeringList1Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.engineering.routes.FactoryEngineeringList2Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def factoryEngineeringList2(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(FactoryEngineeringList2Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => controllers.engineering.routes.FactoryEngineeringApprenticeController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def factoryEngineeringApprentice(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(FactoryEngineeringApprenticePage) match {
      case Some(true) | Some(false) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
