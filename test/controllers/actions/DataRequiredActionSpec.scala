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

package controllers.actions

import base.SpecBase
import controllers.confirmation.{routes => confRoutes}
import controllers.routes
import models.requests.{DataRequest, OptionalDataRequest}
import models.{NormalMode, UserAnswers}
import navigation.AuthenticatedNavigator
import org.mockito.ArgumentMatchers.{eq => eqs}
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.SubmittedClaim
import pages.authenticated.Submission
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val mockNavigator: AuthenticatedNavigator = mock[AuthenticatedNavigator]

  class FilterUnderTest extends DataRequiredActionImpl(mockNavigator) {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  "DataRequiredAction" should {
    "redirect to the session expired page" when {
      "there are no userAnswers in the session" in {
        val optionalDataRequest =
          OptionalDataRequest(FakeRequest("GET", "/"), Authed("internalId"), Some(fakeNino), None)
        val futureResult = new FilterUnderTest().callRefine(optionalDataRequest)

        whenReady(futureResult) { result =>
          result.isLeft mustBe true
          result.swap
            .getOrElse(throw new NoSuchElementException("Either.left.get on Right"))
            .header
            .status mustBe SEE_OTHER
          result.swap
            .getOrElse(throw new NoSuchElementException("Either.left.get on Right"))
            .header
            .headers
            .get(LOCATION)
            .contains(routes.SessionExpiredController.onPageLoad.url) mustBe true
        }
      }
    }

    "redirect to the confirmation page (which one is decided by Submission navigator logic)" when {
      "the SubmittedClaim session value is present" in {
        val fakeRequest = FakeRequest("GET", routes.MultipleEmploymentsController.onPageLoad(NormalMode).url)

        val userAnswers = UserAnswers(
          Json.obj(SubmittedClaim.toString -> true)
        )

        when(mockNavigator.nextPage(eqs(Submission), eqs(NormalMode)))
          .thenReturn(_ => confRoutes.ConfirmationCurrentYearOnlyController.onPageLoad())

        val optionalDataRequest =
          OptionalDataRequest(fakeRequest, Authed("internalId"), Some(fakeNino), Some(userAnswers))
        val futureResult = new FilterUnderTest().callRefine(optionalDataRequest)

        whenReady(futureResult) { result =>
          result.isLeft mustBe true
          result.swap
            .getOrElse(throw new NoSuchElementException("Either.left.get on Right"))
            .header
            .status mustBe SEE_OTHER
          result.swap
            .getOrElse(throw new NoSuchElementException("Either.left.get on Right"))
            .header
            .headers
            .get(LOCATION)
            .contains(confRoutes.ConfirmationCurrentYearOnlyController.onPageLoad().url) mustBe true
        }
      }
    }

    "not redirect to the confirmation page" when {
      "the SubmittedClaim session value is present and the current URL is the confirmation page" in {
        val fakeRequest = FakeRequest("GET", confRoutes.ConfirmationCurrentYearOnlyController.onPageLoad().url)

        val userAnswers = UserAnswers(
          Json.obj(SubmittedClaim.toString -> true)
        )

        val optionalDataRequest =
          OptionalDataRequest(fakeRequest, Authed("internalId"), Some(fakeNino), Some(userAnswers))
        val futureResult = new FilterUnderTest().callRefine(optionalDataRequest)

        whenReady(futureResult)(result => result.isRight mustBe true)
      }
    }
  }

}
