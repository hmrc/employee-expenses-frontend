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
import controllers.actions.{DataRetrievalAction, UnauthenticatedIdentifierAction}
import models.{NormalMode, UserAnswers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.ExecutionContext


class IndexControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience {


  "Index Controller" must {

    "redirect to the first page of the application and the correct view for a GET when user answers is empty" in {

      val application = applicationBuilder(userAnswers = None).build()

      val injector = application.injector

      val controller =
        new IndexController(
          controllerComponents = injector.instanceOf[MessagesControllerComponents],
          identify = injector.instanceOf[UnauthenticatedIdentifierAction],
          getData = injector.instanceOf[DataRetrievalAction],
          sessionRepository = injector.instanceOf[SessionRepository]
        )(ec = injector.instanceOf[ExecutionContext])

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      whenReady(result) {
        result =>
          result.session(request).data.get(frontendAppConfig.mongoKey) must contain("key")
      }

      application.stop()
    }

    "redirect to the first page of the application and the correct view for a GET when user answers is not empty" in {

      val application = applicationBuilder(userAnswers = Some(UserAnswers("testId"))).build()

      val injector = application.injector

      val controller =
        new IndexController(
          controllerComponents = injector.instanceOf[MessagesControllerComponents],
          identify = injector.instanceOf[UnauthenticatedIdentifierAction],
          getData = injector.instanceOf[DataRetrievalAction],
          sessionRepository = injector.instanceOf[SessionRepository]
        )(ec = injector.instanceOf[ExecutionContext])

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      whenReady(result) {
        result =>
          result.session(request).data.get(frontendAppConfig.mongoKey) must contain("key")
      }

      application.stop()
    }
  }
}
