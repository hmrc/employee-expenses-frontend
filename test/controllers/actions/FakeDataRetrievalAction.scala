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

package controllers.actions

import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}

import scala.concurrent.{ExecutionContext, Future}

class FakeDataRetrievalAction(dataToReturn: Option[UserAnswers]) extends DataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] =
    request.identifier match {
      case id: Authed =>
        dataToReturn match {
          case None =>
            Future(OptionalDataRequest(request.request, id, request.nino, None))
          case Some(userAnswers) =>
            Future(OptionalDataRequest(request.request, id, request.nino, Some(userAnswers)))
        }
      case id: UnAuthed =>
        dataToReturn match {
          case None =>
            Future(OptionalDataRequest(request.request, id, request.nino, None))
          case Some(userAnswers) =>
            Future(OptionalDataRequest(request.request, id, request.nino, Some(userAnswers)))
        }
    }

  override protected implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

}
