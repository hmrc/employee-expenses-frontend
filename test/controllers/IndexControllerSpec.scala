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

package controllers

import base.SpecBase
import config.FrontendAppConfig
import controllers.actions.{FakeDataRetrievalAction, UnauthenticatedIdentifierActionImpl}
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.mockito.MockitoSugar
import pages.mergedJourney.MergedJourneyFlag
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.{BodyParsers, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience {

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  "onPageLoad" must {
    "redirect to the first page of the application and reset user answers when user answers is not empty when authed" in {
      val argCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

      val mockAuthConnector = mock[AuthConnector]
      when(mockAuthConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new~(Some(fakeNino), Some(userAnswersId))))

      val passingAuthAction = new UnauthenticatedIdentifierActionImpl(
        mockAuthConnector,
        frontendAppConfig,
        app.injector.instanceOf[BodyParsers.Default]
      )

      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = passingAuthAction,
        getData = new FakeDataRetrievalAction(Some(minimumUserAnswers)),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)
      argCaptor.getValue.data mustBe Json.obj(MergedJourneyFlag.toString -> false)
    }
    "redirect to the first page of the application and create a user answers when user answers is empty when authed" in {
      val argCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

      val mockAuthConnector = mock[AuthConnector]
      when(mockAuthConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new~(Some(fakeNino), Some(userAnswersId))))

      val passingAuthAction = new UnauthenticatedIdentifierActionImpl(
        mockAuthConnector,
        frontendAppConfig,
        app.injector.instanceOf[BodyParsers.Default]
      )

      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = passingAuthAction,
        getData = new FakeDataRetrievalAction(None),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url)
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)
      argCaptor.getValue.data mustBe Json.obj(MergedJourneyFlag.toString -> false)
    }
    "redirect to the first page of the application and reset user answers when user answers is not empty when unauthed" in {
      val argCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = app.injector.instanceOf[UnauthenticatedIdentifierActionImpl],
        getData = new FakeDataRetrievalAction(Some(minimumUserAnswers)),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)
      argCaptor.getValue.data mustBe Json.obj()
    }
    "redirect to the first page of the application and create a user answers when user answers is empty when unauthed" in {
      val argCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = app.injector.instanceOf[UnauthenticatedIdentifierActionImpl],
        getData = new FakeDataRetrievalAction(None),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")
      val result = controller.onPageLoad()(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)
      argCaptor.getValue.data mustBe Json.obj()
    }
    "redirect to the first page of the application and create a user answers when user answers is empty when authed and on merged journey" in {
      val argCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

      val mockAuthConnector = mock[AuthConnector]
      when(mockAuthConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new~(Some(fakeNino), Some(userAnswersId))))

      val passingAuthAction = new UnauthenticatedIdentifierActionImpl(
        mockAuthConnector,
        frontendAppConfig,
        app.injector.instanceOf[BodyParsers.Default]
      )

      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = passingAuthAction,
        getData = new FakeDataRetrievalAction(None),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url)
      val result = controller.onPageLoad(isMergedJourney = true)(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)
      argCaptor.getValue.data mustBe Json.obj(MergedJourneyFlag.toString -> true)
    }
  }

  "start" must {
    "redirect to index with merged journey flag when authed and on merged journey" in {
      val mockAuthConnector = mock[AuthConnector]
      when(mockAuthConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new~(Some(fakeNino), Some(userAnswersId))))

      val passingAuthAction = new UnauthenticatedIdentifierActionImpl(
        mockAuthConnector,
        frontendAppConfig,
        app.injector.instanceOf[BodyParsers.Default]
      )

      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = passingAuthAction,
        getData = new FakeDataRetrievalAction(Some(minimumUserAnswers.set(MergedJourneyFlag, true).get)),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.start.url)
      val result = controller.start(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.IndexController.onPageLoad(true).url)
    }
    "redirect to index if user is authed without user answers" in {
      val mockAuthConnector = mock[AuthConnector]
      when(mockAuthConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new~(Some(fakeNino), Some(userAnswersId))))

      val passingAuthAction = new UnauthenticatedIdentifierActionImpl(
        mockAuthConnector,
        frontendAppConfig,
        app.injector.instanceOf[BodyParsers.Default]
      )

      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = passingAuthAction,
        getData = new FakeDataRetrievalAction(None),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.start.url)
      val result = controller.start(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.IndexController.onPageLoad().url)
    }
    "redirect to index if user is unauthed" in {
      val controller = new IndexController(
        controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
        identify = app.injector.instanceOf[UnauthenticatedIdentifierActionImpl],
        getData = new FakeDataRetrievalAction(None),
        sessionRepository = mockSessionRepository,
        appConfig = app.injector.instanceOf[FrontendAppConfig]
      )

      val request = FakeRequest(method = "GET", path = routes.IndexController.start.url).withSession(SessionKeys.sessionId -> "key")
      val result = controller.start(request)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) must contain(routes.IndexController.onPageLoad().url)
    }
  }
}
