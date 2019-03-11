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

package controllers

import base.SpecBase
import controllers.actions._
import models.requests.IdentifierRequest
import models.{NormalMode, UserAnswers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{AuthedSessionRepository, SessionRepository}
import uk.gov.hmrc.auth.core.AuthorisedFunctions
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class IndexControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with MockitoSugar {


  "Index Controller" must {

    "redirect to the first page of the application and the correct view for a GET when user answers is empty when unauthed" in {

      val application = applicationBuilder(userAnswers = None).build()

      val injector = application.injector

      val controller =
        new IndexController(
          controllerComponents = injector.instanceOf[MessagesControllerComponents],
          identify = injector.instanceOf[UnauthenticatedIdentifierAction],
          getData = injector.instanceOf[DataRetrievalAction],
          sessionRepository = injector.instanceOf[SessionRepository],
          authedSessionRepository = injector.instanceOf[AuthedSessionRepository]
        )(ec = injector.instanceOf[ExecutionContext])

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }

    "redirect to the first page of the application and the correct view for a GET when user answers is empty when authed" in {

      val mockUnauthenticatedIdentifierAction = mock[UnauthenticatedIdentifierAction]

      def x = IdentifierRequest(fakeRequest, Authed(userAnswersId), Some(fakeNino)) => Future[Result]

      when(mockUnauthenticatedIdentifierAction.invokeBlock(any(), any())) thenReturn

      val application = applicationBuilder(userAnswers = None).build()

      val injector = application.injector

      val controller =
        new IndexController(
          controllerComponents = injector.instanceOf[MessagesControllerComponents],
          identify = injector.instanceOf[UnauthenticatedIdentifierAction],
          getData = injector.instanceOf[DataRetrievalAction],
          sessionRepository = injector.instanceOf[SessionRepository],
          authedSessionRepository = injector.instanceOf[AuthedSessionRepository]
        )(ec = injector.instanceOf[ExecutionContext])

      val request = IdentifierRequest(FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url), Authed(userAnswersId), Some(fakeNino))

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }

    "redirect to the first page of the application and the correct view for a GET when user answers is not empty when unauthed" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers("testId"))).build()

      val injector = application.injector

      val controller =
        new IndexController(
          controllerComponents = injector.instanceOf[MessagesControllerComponents],
          identify = injector.instanceOf[UnauthenticatedIdentifierAction],
          getData = injector.instanceOf[DataRetrievalAction],
          sessionRepository = injector.instanceOf[SessionRepository],
          authedSessionRepository = injector.instanceOf[AuthedSessionRepository]
        )(ec = injector.instanceOf[ExecutionContext])

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }

    "redirect to the first page of the application and the correct view for a GET when user answers is not empty when authed" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers("testId"))).build()

      val injector = application.injector

      val controller =
        new IndexController(
          controllerComponents = injector.instanceOf[MessagesControllerComponents],
          identify = injector.instanceOf[UnauthenticatedIdentifierAction],
          getData = injector.instanceOf[DataRetrievalAction],
          sessionRepository = injector.instanceOf[SessionRepository],
          authedSessionRepository = injector.instanceOf[AuthedSessionRepository]
        )(ec = injector.instanceOf[ExecutionContext])

      val request = IdentifierRequest(FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url), Authed(userAnswersId), Some(fakeNino))

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }
  }
}
