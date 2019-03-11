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
import models.NormalMode
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with MockitoSugar {


  "Index Controller" must {

    "redirect to the first page of the application and the correct view for a GET when user answers is empty when unauthed" in {

      val application = applicationBuilder(userAnswers = None).build()

      val controller = application.injector.instanceOf[IndexController]

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }

    "redirect to the first page of the application and the correct view for a GET when user answers is empty when authed" in {

      val mockAuthConnector = mock[AuthConnector]

      val application = applicationBuilder(None)
        .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
        .build()

      when(mockAuthConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(Some(fakeNino), Some(userAnswersId))))

      val controller = application.injector.instanceOf[IndexController]

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url)

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }

    "redirect to the first page of the application and the correct view for a GET when user answers is not empty when unauthed" in {

      val application = applicationBuilder(userAnswers = Some(minimumUserAnswers)).build()

      val controller = application.injector.instanceOf[IndexController]

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url).withSession(SessionKeys.sessionId -> "key")

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }

    "redirect to the first page of the application and the correct view for a GET when user answers is not empty when authed" in {

      val mockAuthConnector = mock[AuthConnector]

      val application = applicationBuilder(Some(minimumUserAnswers))
        .overrides(bind[AuthConnector].toInstance(mockAuthConnector))
        .build()

      when(mockAuthConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(Some(fakeNino), Some(userAnswersId))))

      val controller = application.injector.instanceOf[IndexController]

      val request = FakeRequest(method = "GET", path = routes.IndexController.onPageLoad().url)

      val result = controller.onPageLoad(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result) must contain(routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

      application.stop()
    }
  }
}
