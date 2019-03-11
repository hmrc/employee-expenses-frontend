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

package controllers.actions

import base.SpecBase
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import play.api.mvc._
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnauthenticatedIdentifierActionSpec extends SpecBase with ScalaFutures with IntegrationPatience with MockitoSugar {

  class Harness(authAction: UnauthenticatedIdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  "Un Auth Action" must {

    "return an IdentifierRequest with an Authed(internalId) when user passes auth checks" in {

      val application = applicationBuilder(userAnswers = None).build()

      val authConnector = mock[AuthConnector]

      when(authConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(Some(fakeNino), Some(userAnswersId))))

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val unAuthAction = new UnauthenticatedIdentifierActionImpl(authConnector, frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)
      val result = controller.onPageLoad()(fakeRequest)

      status(result) mustBe OK

      application.stop()
    }

    "return an IdentifierRequest with an UnAuthed(sessionId) when user fails auth checks" in {

      val application = applicationBuilder(userAnswers = None).build()

      val authConnector = mock[AuthConnector]

      when(authConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(None, None)))

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val unAuthAction = new UnauthenticatedIdentifierActionImpl(authConnector, frontendAppConfig, bodyParsers)

      val controller = new Harness(unAuthAction)

      val result = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> userAnswersId))

      status(result) mustBe OK

      application.stop()
    }

    "return exception when there is no session id" in {

      val application = applicationBuilder(userAnswers = None).build()

      val authConnector = mock[AuthConnector]

      when(authConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(None, None)))

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val unAuthAction = new UnauthenticatedIdentifierActionImpl(authConnector, frontendAppConfig, bodyParsers)

      val controller = new Harness(unAuthAction)

      val exception = intercept[Exception] {

        whenReady(controller.onPageLoad()(fakeRequest))(identity)

      }

      exception.getMessage must include("no sessionId")

      application.stop()
    }
  }
}
