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
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import play.api.mvc._
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnauthenticatedIdentifierActionSpec extends SpecBase with ScalaFutures with IntegrationPatience with MockitoSugar {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  "Un Auth Action" when {

    "return 200 when there is a mongoKey" in {

      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val authConnector = application.injector.instanceOf[AuthConnector]

      val unAuthAction = new UnauthenticatedIdentifierAction(authConnector, frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)
      val result = controller.onPageLoad()(fakeRequest.withSession(frontendAppConfig.mongoKey -> "key"))

      status(result) mustBe OK

      application.stop()
    }

    "update session with mongoKey" in {

      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val authConnector = application.injector.instanceOf[AuthConnector]

      val unAuthAction = new UnauthenticatedIdentifierAction(authConnector, frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)
      val request = fakeRequest.withSession(frontendAppConfig.mongoKey -> "key")
      val result: Future[Result] = controller.onPageLoad()(request)

      whenReady(result) {
        result =>
          result.session(request).data.get(frontendAppConfig.mongoKey) must contain("key")
      }

      application.stop()
    }

    "return 200 when there is no mongoKey but a sessionId is present" in {

      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val authConnector = application.injector.instanceOf[AuthConnector]

      val unAuthAction = new UnauthenticatedIdentifierAction(authConnector, frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)
      val result = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "key"))

      status(result) mustBe OK

      application.stop()
    }

    "update session with mongoKey when there is no mongoKey but a sessionId is present" in {
      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val authConnector = application.injector.instanceOf[AuthConnector]

      val unAuthAction = new UnauthenticatedIdentifierAction(authConnector, frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)
      val request = fakeRequest.withSession(SessionKeys.sessionId -> "key")
      val result = controller.onPageLoad()(request)

      whenReady(result) {
        result =>
          result.session(request).data.get(frontendAppConfig.mongoKey) must contain("key")
      }

      application.stop()
    }

    "return exception when there is no mongoKey and no session" in {

      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val authConnector = application.injector.instanceOf[AuthConnector]

      val unAuthAction = new UnauthenticatedIdentifierAction(authConnector, frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)

      val exception = intercept[Exception] {
        controller.onPageLoad()(fakeRequest)
      }

      exception.getMessage mustBe "[UnauthenticatedIdentifierAction] No mongoKey created"

      application.stop()
    }
  }
}
