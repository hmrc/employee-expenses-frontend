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

package repositories

import controllers.actions.{Authed, IdentifierType, UnAuthed}
import javax.inject.{Inject, Singleton}
import models.UserAnswers

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepository @Inject()(authSessionRepository: UnAuthSessionRepository,
                                  unAuthSessionRepository: UnAuthSessionRepository)(implicit ec: ExecutionContext) {

  def get(identifierType: IdentifierType): Future[Option[UserAnswers]] = {
    identifierType match {
      case id: Authed => authSessionRepository.get(id.internalId)
      case id: UnAuthed => unAuthSessionRepository.get(id.sessionId)
    }
  }

  def set(identifierType: IdentifierType, userAnswers: UserAnswers): Future[Boolean] = {
    identifierType match {
      case _: Authed => authSessionRepository.set(userAnswers)
      case _: UnAuthed => unAuthSessionRepository.set(userAnswers)
    }
  }

  def remove(identifierType: IdentifierType): Future[Option[UserAnswers]] = {
    identifierType match {
      case id: Authed => authSessionRepository.remove(id.internalId)
      case id: UnAuthed => unAuthSessionRepository.remove(id.sessionId)
    }
  }

  def updateTimeToLive(id: Authed): Future[Boolean] = {
    get(id).flatMap {
      case Some(ua) => set(id, ua)
      case _ => Future.successful(false)
    }
  }

}
