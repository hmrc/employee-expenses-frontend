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
import com.google.inject.Inject
import controllers.routes
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.mvc.Results.Redirect
import play.api.test.Helpers._
import play.api.mvc.{BodyParsers, Request, Result, Results}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class SessionActionSpec extends SpecBase {

  class Harness(action: IdentifierAction) {
    def onPageLoad() = action { request => Results.Ok }
  }

  "Session Action" when {

    "there's no active session" must {

      "redirect to the session expired page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val sessionAction = new SessionIdentifierAction(frontendAppConfig, bodyParsers, fakeNino)

        val controller = new Harness(sessionAction)

        val result = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(controllers.routes.SessionExpiredController.onPageLoad().url)

        application.stop()
      }
    }

    "there's an active session" must {

      "perform the action" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val sessionAction = new SessionIdentifierAction(frontendAppConfig, bodyParsers, fakeNino)

        val controller = new Harness(sessionAction)

        val request = fakeRequest.withSession(SessionKeys.sessionId -> "foo")

        val result = controller.onPageLoad()(request)

        status(result) mustBe OK

        application.stop()
      }
    }
  }
}

class SessionIdentifierAction @Inject()(
                                         config: FrontendAppConfig,
                                         val parser: BodyParsers.Default,
                                         nino: String
                                       )
                                       (implicit val executionContext: ExecutionContext) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    hc.sessionId match {
      case Some(session) =>
        block(IdentifierRequest(request, UnAuthed(session.value), Some(nino)))
      case None =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }
}
