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

import base.SpecBase
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  class Harness(sessionRepository: SessionRepository) extends DataRetrievalActionImpl(sessionRepository) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "set userAnswers in unauthed to 'None' in the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get(UnAuthed(userAnswersId))).thenReturn(Future(None))
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, UnAuthed(userAnswersId), Some(fakeNino)))

        whenReady(futureResult)(result => result.userAnswers.isEmpty mustBe true)
      }

      "set userAnswers in authed to 'None' in the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get(Authed(userAnswersId))).thenReturn(Future(None))
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, Authed(userAnswersId), Some(fakeNino)))

        whenReady(futureResult)(result => result.userAnswers.isEmpty mustBe true)
      }
    }

    "there is data in the cache" must {

      "build a userAnswers in unathed object and add it to the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get(UnAuthed(userAnswersId))).thenReturn(Future(Some(new UserAnswers(Json.obj()))))
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, UnAuthed(userAnswersId), Some(fakeNino)))

        whenReady(futureResult)(result => result.userAnswers.isDefined mustBe true)
      }

      "build a userAnswers in authed object and add it to the request" in {

        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get(Authed(userAnswersId))).thenReturn(Future(Some(new UserAnswers())))
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, Authed(userAnswersId), Some(fakeNino)))

        whenReady(futureResult)(result => result.userAnswers.isDefined mustBe true)
      }
    }
  }

}
