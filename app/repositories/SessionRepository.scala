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

package repositories

import controllers.actions.{Authed, IdentifierType, UnAuthed}
import models.UserAnswers
import models.mergedJourney.MergedJourney

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepository @Inject()(authSessionRepository: AuthSessionRepository,
                                  unAuthSessionRepository: UnAuthSessionRepository,
                                  mergedJourneySessionRepository: MergedJourneySessionRepository)(implicit ec: ExecutionContext) {

  def get(identifierType: IdentifierType): Future[Option[UserAnswers]] = {
    identifierType match {
      case id: Authed => authSessionRepository.get(id.internalId)
      case id: UnAuthed => unAuthSessionRepository.get(id.sessionId)
    }
  }

  def set(identifierType: IdentifierType, userAnswers: UserAnswers): Future[Boolean] = {
    identifierType match {
      case id: Authed if userAnswers.isMergedJourney =>
        updateMergedJourneyTimeToLive(id)
        authSessionRepository.set(id.internalId, userAnswers)
      case id: Authed => authSessionRepository.set(id.internalId, userAnswers)
      case id: UnAuthed => unAuthSessionRepository.set(id.sessionId, userAnswers)
    }
  }

  def remove(identifierType: IdentifierType): Future[Unit] = {
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

  def getMergedJourney(internalId: String): Future[Option[MergedJourney]] =
    mergedJourneySessionRepository.get(internalId)

  def setMergedJourney(mergedJourney: MergedJourney): Future[Boolean] =
    mergedJourneySessionRepository.set(mergedJourney)

  def updateMergedJourneyTimeToLive(id: Authed): Future[Boolean] =
    mergedJourneySessionRepository.get(id.internalId).flatMap {
      case Some(data) => mergedJourneySessionRepository.set(data)
      case _ => Future.successful(false)
    }

}
