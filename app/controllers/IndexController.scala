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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import models.{NormalMode, UserAnswers}
import pages.mergedJourney.MergedJourneyFlag
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(val controllerComponents: MessagesControllerComponents,
                                identify: UnauthenticatedIdentifierAction,
                                getData: DataRetrievalAction,
                                sessionRepository: SessionRepository,
                                appConfig: FrontendAppConfig
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(isMergedJourney: Boolean = false): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    if (request.userAnswers.isEmpty || request.userAnswers.exists(_.isMergedJourney != isMergedJourney)) {
      request.identifier match {
        case id: Authed =>
          sessionRepository
            .set(request.identifier, UserAnswers(Json.obj(
              MergedJourneyFlag.toString -> (isMergedJourney && appConfig.mergedJourneyEnabled)
            )))
            .map(_ => Redirect(routes.MultipleEmploymentsController.onPageLoad(NormalMode)))
        case id: UnAuthed =>
          sessionRepository
            .set(request.identifier, UserAnswers())
            .map(_ => Redirect(routes.MultipleEmploymentsController.onPageLoad(NormalMode)))
      }
    } else {
      Future.successful(Redirect(routes.MultipleEmploymentsController.onPageLoad(NormalMode)))
    }
  }
}
