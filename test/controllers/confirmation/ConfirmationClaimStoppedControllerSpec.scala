/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.confirmation

import base.SpecBase
import controllers.actions.Authed
import controllers.confirmation.routes._
import models.TaxYearSelection._
import models._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.authenticated._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.confirmation._

import scala.concurrent.ExecutionContext.Implicits.global

class ConfirmationClaimStoppedControllerSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience {

  "ConfirmationClaimStoppedController" must {

    "return OK and ConfirmationClaimStoppedView for a GET with specific answers" in {

      val userAnswers = currentYearFullUserAnswers.set(RemoveFRECodePage, TaxYearSelection.CurrentYearMinus1).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, ConfirmationClaimStoppedController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConfirmationClaimStoppedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }

    "Remove session on page load" in {

      val application = applicationBuilder(userAnswers = Some(currentYearFullUserAnswers))
        .build()

      val request = FakeRequest(GET, ConfirmationClaimStoppedController.onPageLoad().url)

      val result = route(application, request).value

      whenReady(result) {
        _ =>
          val sessionRepository = application.injector.instanceOf[SessionRepository]
          sessionRepository.get(Authed(userAnswersId)).map(_ mustBe None)
      }

      application.stop()
    }
  }
}
