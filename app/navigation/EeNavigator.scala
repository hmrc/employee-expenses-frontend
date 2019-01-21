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
import models.FirstIndustryOptions._
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.{FirstIndustryOptionsPage, Page}
import play.api.mvc.Call

class EeNavigator  @Inject()() extends Navigator {
  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case FirstIndustryOptionsPage => ua => firstIndustryOptions(NormalMode)(ua)
    case _ => ua => routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case FirstIndustryOptionsPage => ua => firstIndustryOptions(CheckMode)(ua)
    case _ => ua => routes.SessionExpiredController.onPageLoad()
  }

  private def firstIndustryOptions(mode: Mode)(userAnswers: UserAnswers): Call =
    userAnswers.get(FirstIndustryOptionsPage) match {
      case Some(Healthcare) =>
        controllers.healthcare.routes.AmbulanceStaffController.onPageLoad(NormalMode)
      case Some(FoodAndCatering) =>
        routes.SessionExpiredController.onPageLoad()
      case Some(Retail) =>
        routes.SessionExpiredController.onPageLoad()
      case Some(Engineering) =>
        controllers.engineering.routes.TypeOfEngineeringController.onPageLoad(NormalMode)
      case Some(TransportAndDistribution) =>
        routes.SessionExpiredController.onPageLoad()
      case Some(NoneOfTheAbove) =>
        routes.SessionExpiredController.onPageLoad()
      case _ =>
        routes.SessionExpiredController.onPageLoad()
    }
}
