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

package controllers.authenticated

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import controllers.actions.{Authed, IdentifierAction}
import controllers.authenticated.routes._
import models.{NormalMode, UserAnswers}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.{AuthedSessionRepository, SessionRepository}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

@Singleton
class RedirectMongoKeyController @Inject()(
                                            config: FrontendAppConfig,
                                            identify: IdentifierAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            appConfig: FrontendAppConfig,
                                            sessionRepository: SessionRepository,
                                            authedSessionRepository: AuthedSessionRepository
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController {

  def onPageLoad(key: String, journeyId: Option[String]): Action[AnyContent] = identify.async {
    implicit request =>
      for {
        ua <- sessionRepository.get(key).map{_.get}
        id = request.identifier.asInstanceOf[Authed]
        _ <- authedSessionRepository.set(UserAnswers(id.internalId, ua.data))
        _ <- sessionRepository.set(UserAnswers(key, Json.obj()))
      } yield {
        Redirect(TaxYearSelectionController.onPageLoad(NormalMode))
      }
  }
}

/*
implicit request =>
sessionRepository.get(key).map {
  userAnswers =>
  (userAnswers, request.identifier) match {
  case (Some(ua), id: Authed) =>
  authedSessionRepository.set(UserAnswers(id.internalId, ua.data))
  sessionRepository.set(UserAnswers(key, Json.obj()))
  case _ =>
  Redirect(TechnicalDifficultiesController.onPageLoad())
}
}
  Redirect(TaxYearSelectionController.onPageLoad(NormalMode))
}*/
