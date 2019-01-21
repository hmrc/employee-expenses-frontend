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
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.{BodyParsers, Results}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global

class UnAuthActionSpec extends SpecBase with ScalaFutures with MockitoSugar {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad() = authAction { _ => Results.Ok }
  }

  "Un Auth Action" when {

    "return 200 when there is a mongoKey" in {

      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val unAuthAction = new UnauthenticatedIdentifierAction(frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)
      val result = controller.onPageLoad()(fakeRequest.withSession("mongoKey" -> "key"))

      status(result) mustBe OK

      application.stop()
    }

    "return 200 when there is no mongoKey but a sessionId is present" in {

      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val unAuthAction = new UnauthenticatedIdentifierAction(frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)
      val result = controller.onPageLoad()(fakeRequest.withSession("sessionId" -> "key"))

      status(result) mustBe OK

      application.stop()
    }

    "return exception when there is no mongoKey and no session" in {

      val application = applicationBuilder(userAnswers = None).build()

      val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

      val unAuthAction = new UnauthenticatedIdentifierAction(frontendAppConfig, bodyParsers)
      val controller = new Harness(unAuthAction)

      val exception = intercept[Exception] {
        controller.onPageLoad()(fakeRequest)
      }

      exception.getMessage mustBe "[UnauthenticatedIdentifierAction] No mongoKey created"

      application.stop()
    }
  }
}
