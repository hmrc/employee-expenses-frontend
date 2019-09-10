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

import javax.inject.Inject
import models.{CheckMode, NormalMode, UserAnswers}
import pages.Page
import pages.construction._
import play.api.mvc.Call

class ConstructionNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case ConstructionOccupationsPage      => _ => controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
    case _                                => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case ConstructionOccupationsPage      => _ => controllers.routes.EmployerContributionController.onPageLoad(CheckMode)
    case _                                => _ => controllers.routes.SessionExpiredController.onPageLoad()
  }
}
