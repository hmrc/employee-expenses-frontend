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

import controllers.construction.routes
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.construction._
import play.api.mvc.Call

class ConstructionNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case JoinerCarpenterPage              => joinerCarpenter(NormalMode)
    case StoneMasonPage                   => stoneMason(NormalMode)
    case ConstructionOccupationList1Page  => constructionOccupationList1(NormalMode)
    case ConstructionOccupationList2Page  => constructionOccupationList2(NormalMode)
    case BuildingMaterialsPage            => buildingMaterials(NormalMode)
    case _                                => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case JoinerCarpenterPage              => joinerCarpenter(CheckMode)
    case StoneMasonPage                   => stoneMason(CheckMode)
    case ConstructionOccupationList1Page  => constructionOccupationList1(CheckMode)
    case ConstructionOccupationList2Page  => constructionOccupationList2(CheckMode)
    case BuildingMaterialsPage            => buildingMaterials(CheckMode)
    case _ => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def joinerCarpenter(mode: Mode)(userAnswers: UserAnswers): Call = {
    userAnswers.get(JoinerCarpenterPage) match {
      case Some(true)   => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false)  => routes.StoneMasonController.onPageLoad(mode)
      case _            => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def stoneMason(mode: Mode)(userAnswers: UserAnswers) = {
    userAnswers.get(StoneMasonPage) match {
      case Some(true)   => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false)  => routes.ConstructionOccupationList1Controller.onPageLoad(mode)
      case _            => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def constructionOccupationList1(mode: Mode)(userAnswers: UserAnswers) = {
    userAnswers.get(ConstructionOccupationList1Page) match {
      case Some(true)   => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false)  => routes.ConstructionOccupationList2Controller.onPageLoad(mode)
      case _            => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
  private def constructionOccupationList2(mode: Mode)(userAnswers: UserAnswers) = {
    userAnswers.get(ConstructionOccupationList2Page) match {
      case Some(true)   => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false)  => routes.BuildingMaterialsController.onPageLoad(mode)
      case _            => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
  private def buildingMaterials(mode: Mode)(userAnswers: UserAnswers) = {
    userAnswers.get(BuildingMaterialsPage) match {
      case Some(true)   => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case Some(false)  => controllers.routes.EmployerContributionController.onPageLoad(mode)
      case _            => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
