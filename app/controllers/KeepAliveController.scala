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

package controllers

import controllers.actions.{Authed, AuthenticatedIdentifierAction}
import javax.inject.Inject
import models.requests.IdentifierRequest
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext.Implicits.global

class KeepAliveController @Inject()(
                                     identify: AuthenticatedIdentifierAction,
                                     sessionRepository: SessionRepository,
                                     val controllerComponents: MessagesControllerComponents
                                   ) extends FrontendBaseController with I18nSupport {

  def keepAlive: Action[AnyContent] = identify.async {
    implicit request: IdentifierRequest[AnyContent] =>

      val id = request.identifier.asInstanceOf[Authed]

      sessionRepository.updateTimeToLive(id).map {
        case true => Ok("OK")
        case _ => Redirect(routes.TechnicalDifficultiesController.onPageLoad())
      }
  }
}