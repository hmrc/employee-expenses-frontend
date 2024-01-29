/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.mergedJourney

import config.FrontendAppConfig
import controllers.actions.{Authed, AuthenticatedIdentifierAction}
import controllers.routes
import models.mergedJourney.{JourneyPending, JourneySkipped, MergedJourney}
import models.requests.IdentifierRequest
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MergedJourneyController @Inject()(identify: AuthenticatedIdentifierAction,
                                        sessionRepository: SessionRepository,
                                        val controllerComponents: MessagesControllerComponents,
                                        appConfig: FrontendAppConfig
                                       )(implicit val ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def setupMergedJourney(wfh: Boolean, psubs: Boolean, fre: Boolean): Action[AnyContent] = identify.async { implicit request =>
    if (appConfig.mergedJourneyEnabled) {
      request.identifier match {
        case id: Authed => sessionRepository.setMergedJourney(MergedJourney(
          internalId = id.internalId,
          wfh = if (wfh) JourneyPending else JourneySkipped,
          psubs = if (psubs) JourneyPending else JourneySkipped,
          fre = if (fre) JourneyPending else JourneySkipped
        )).map(_ => Ok("OK"))
        case _ => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad))
      }
    } else {
      Future.successful(NotImplemented)
    }
  }

  def mergedJourneyRefreshSession: Action[AnyContent] = identify.async { implicit request =>
    request.identifier match {
      case id: Authed => sessionRepository.updateMergedJourneyTimeToLive(id).map {
        case true => Ok("OK")
        case _ => Redirect(routes.TechnicalDifficultiesController.onPageLoad)
      }
      case _ => Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad))
    }
  }
}
