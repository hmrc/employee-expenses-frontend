/*
 * Copyright 2021 HM Revenue & Customs
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
import controllers.actions._
import controllers.authenticated.routes._
import controllers.routes._
import models.{NormalMode, UserAnswers}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthRedirectController @Inject()(
                                        identify: AuthenticatedIdentifierAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        sessionRepository: SessionRepository
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController {

  def onPageLoad(key: String): Action[AnyContent] = identify.async {
    implicit request =>
      val id: Authed = request.identifier.asInstanceOf[Authed]

      sessionRepository.get(UnAuthed(key)).flatMap {
        unAuthUA =>
        sessionRepository.get(id).flatMap {
          authUA =>
            (unAuthUA, authUA) match {
              case (Some(unAuthUA), None) =>
                for {
                  _ <- sessionRepository.set(request.identifier, UserAnswers(id.internalId, unAuthUA.data))
                  _ <- sessionRepository.remove(UnAuthed(key))
                } yield {
                  Redirect(TaxYearSelectionController.onPageLoad(NormalMode))
                }
              case (_, Some(authUA)) =>
                Future.successful(Redirect(TaxYearSelectionController.onPageLoad(NormalMode)))
              case _ =>
                Future.successful(Redirect(SessionExpiredController.onPageLoad()))
            }
        }
      }
  }
}
