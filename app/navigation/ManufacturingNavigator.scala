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
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.manufacturing._
import play.api.mvc.Call

class ManufacturingNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfManufacturingPage => typeOfManufacturing(NormalMode)
    case AluminiumOccupationList1Page => aluminiumOccupationList1(NormalMode)
    case AluminiumOccupationList2Page => aluminiumOccupationList2(NormalMode)
    case AluminiumOccupationList3Page => aluminiumOccupationList3(NormalMode)
    case AluminiumApprenticePage => _ => controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
    case IronApprenticePage => _ => controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
    case IronMiningPage => ironMining(NormalMode)
    case IronMiningListPage => _ => controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
    case IronSteelOccupationListPage => ironSteelOccupationList(NormalMode)
    case WoodFurnitureOccupationList1Page => woodFurnitureOccupationList1(NormalMode)
    case WoodFurnitureOccupationList2Page => woodFurnitureOccupationList2(NormalMode)
    case WoodFurnitureOccupationList3Page => _ => controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case TypeOfManufacturingPage => typeOfManufacturing(CheckMode)
    case AluminiumOccupationList1Page => aluminiumOccupationList1(CheckMode)
    case AluminiumOccupationList2Page => aluminiumOccupationList2(CheckMode)
    case AluminiumOccupationList3Page => aluminiumOccupationList3(CheckMode)
    case AluminiumApprenticePage => _ => controllers.routes.EmployerContributionController.onPageLoad(CheckMode)
    case IronApprenticePage => _ => controllers.routes.EmployerContributionController.onPageLoad(CheckMode)
    case IronMiningPage => ironMining(CheckMode)
    case IronMiningListPage => _ => controllers.routes.EmployerContributionController.onPageLoad(CheckMode)
    case IronSteelOccupationListPage => ironSteelOccupationList(CheckMode)
    case WoodFurnitureOccupationList1Page => woodFurnitureOccupationList1(CheckMode)
    case WoodFurnitureOccupationList2Page => woodFurnitureOccupationList2(CheckMode)
    case WoodFurnitureOccupationList3Page => _ => controllers.routes.EmployerContributionController.onPageLoad(CheckMode)
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  def typeOfManufacturing(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(TypeOfManufacturingPage) match {
      case Some(Aluminium) => routes.AluminiumOccupationList1Controller.onPageLoad(mode)
      case Some(BrassCopper) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(Glass) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(IronSteel) => routes.IronMiningController.onPageLoad(mode)
      case Some(PreciousMetals) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(WoodFurniture) => routes.WoodFurnitureOccupationList1Controller.onPageLoad(mode)
      case Some(NoneOfAbove) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def aluminiumOccupationList1(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(AluminiumOccupationList1Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => routes.AluminiumOccupationList2Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def aluminiumOccupationList2(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(AluminiumOccupationList2Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => routes.AluminiumOccupationList3Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def aluminiumOccupationList3(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(AluminiumOccupationList3Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => routes.AluminiumApprenticeController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def ironMining(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(IronMiningPage) match {
      case Some(true) => routes.IronMiningListController.onPageLoad(mode)
      case Some(false) => routes.IronSteelOccupationListController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def ironSteelOccupationList(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(IronSteelOccupationListPage) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => routes.IronApprenticeController.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def woodFurnitureOccupationList1(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(WoodFurnitureOccupationList1Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => routes.WoodFurnitureOccupationList2Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  def woodFurnitureOccupationList2(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(WoodFurnitureOccupationList2Page) match {
      case Some(true) => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false) => routes.WoodFurnitureOccupationList3Controller.onPageLoad(mode)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
