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

package controllers.authenticated

import base.SpecBase
import controllers.actions.Authed
import controllers.authenticated.routes._
import controllers.routes._
import models.requests.IdentifierRequest
import models.{FirstIndustryOptions, NormalMode}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.FirstIndustryOptionsPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{AuthedSessionRepository, SessionRepository}

import scala.concurrent.Future

class AuthRedirectControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with MockitoSugar {

  "AuthRedirectController" must {

    "redirect to TaxYearSelection on success" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(minimumUserAnswers))

      when(mockSessionRepository.remove(any())) thenReturn Future.successful(None)

      val application = applicationBuilder(Some(minimumUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val authedSessionRepository = application.injector.instanceOf[AuthedSessionRepository]

      val request = IdentifierRequest(FakeRequest(GET, AuthRedirectController.onPageLoad(userAnswersId, None).url), Authed(userAnswersId), Some(fakeNino))

      val result = route(application, request).value

      status(result) mustEqual 303

      redirectLocation(result).get mustBe TaxYearSelectionController.onPageLoad(NormalMode).url

      whenReady(authedSessionRepository.get(userAnswersId)) {
        _.value.get(FirstIndustryOptionsPage).value mustBe FirstIndustryOptions.Retail
      }

      application.stop()
    }

    "redirect to SessionExpiredController on sessionRepository.get failure" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.get(any())) thenReturn Future.successful(None)

      val application = applicationBuilder(None)
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = IdentifierRequest(FakeRequest(GET, AuthRedirectController.onPageLoad(userAnswersId, None).url), Authed(userAnswersId), Some(fakeNino))

      val result = route(application, request).value

      status(result) mustEqual 303

      redirectLocation(result).get mustBe SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
