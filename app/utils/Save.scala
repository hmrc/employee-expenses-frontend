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

package utils

import com.google.inject.Inject
import controllers.actions.{Authed, UnAuthed}
import models.UserAnswers
import models.requests.DataRequest
import play.api.mvc.AnyContent
import repositories.{AuthedSessionRepository, SessionRepository}

import scala.concurrent.Future

class Save @Inject()(sessionRepository: SessionRepository,
                     authedSessionRepository: AuthedSessionRepository
                    ) extends SaveToSession {

  def toSession(request: DataRequest[AnyContent], userAnswers: UserAnswers): Future[Boolean] = {
    request.identifier match {
      case _: Authed =>
        authedSessionRepository.set(userAnswers)
      case _: UnAuthed =>
        sessionRepository.set(userAnswers)
    }
  }
}

trait SaveToSession {
  def toSession(request: DataRequest[AnyContent], userAnswers: UserAnswers): Future[Boolean]
}
