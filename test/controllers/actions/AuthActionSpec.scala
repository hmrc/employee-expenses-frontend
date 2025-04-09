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
import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys, UnauthorizedException}
import utils.RetrievalOps._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase with MockitoSugar {

  class Harness(authAction: AuthenticatedIdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction(_ => Results.Ok)
  }

  val mockAuthConnector: AuthConnector     = mock[AuthConnector]
  val mockAppConfig: FrontendAppConfig     = app.injector.instanceOf[FrontendAppConfig]
  val mockBodyParsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

  type AuthRetrievals = Option[String] ~ Option[String] ~ Option[AffinityGroup] ~ ConfidenceLevel

  def retrievals(
      nino: Option[String] = Some(fakeNino),
      internalId: Option[String] = Some(userAnswersId),
      affinityGroup: Option[AffinityGroup] = Some(AffinityGroup.Individual),
      confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200
  ): Harness = {

    when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any())).thenReturn(
      Future.successful(
        nino ~ internalId ~ affinityGroup ~ confidenceLevel
      )
    )

    val authAction = new AuthenticatedIdentifierActionImpl(
      mockAuthConnector,
      mockAppConfig,
      mockBodyParsers
    )(implicitly)

    new Harness(authAction)
  }

  "Auth Action" when {

    "the user tries to access the service with <200 confidence level" must {
      "redirect an Individual user to IVUplift" in {
        val controller = retrievals(confidenceLevel = ConfidenceLevel.L50)
        val result     = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "sessionId"))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(
          "http://localhost:9948/iv-stub/uplift?" +
            "origin=EE&" +
            "confidenceLevel=200&" +
            "completionURL=http://localhost:9334/employee-expenses/session-key?key=sessionId&" +
            "failureURL=http://localhost:9334/employee-expenses/identity-verification-failed"
        )
      }

      "redirect an Org user to IVUplift" in {
        val controller =
          retrievals(affinityGroup = Some(AffinityGroup.Organisation), confidenceLevel = ConfidenceLevel.L50)
        val result = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "sessionId"))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(
          "http://localhost:9948/iv-stub/uplift?" +
            "origin=EE&" +
            "confidenceLevel=200&" +
            "completionURL=http://localhost:9334/employee-expenses/session-key?key=sessionId&" +
            "failureURL=http://localhost:9334/employee-expenses/identity-verification-failed"
        )
      }
    }

    "the user tries to access the service with an Agent affinity group" must {
      "redirect the user to the unauthorised page" in {
        val controller = retrievals(affinityGroup = Some(AffinityGroup.Agent))
        val result     = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "sessionId"))

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }

    "the user is logged in " must {

      "return an IdentifierRequest with an Authed(internalId) when user passes auth checks" in {

        val application = applicationBuilder(userAnswers = None).build()

        val authConnector = mock[AuthConnector]

        when(authConnector.authorise[AuthRetrievals](any(), any())(any(), any())).thenReturn(
          Future.successful(
            Some(fakeNino) ~ Some(userAnswersId) ~ Some(AffinityGroup.Individual) ~ ConfidenceLevel.L200
          )
        )

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(authConnector, frontendAppConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe OK

        application.stop()
      }
    }

    "the user hasn't logged in" must {

      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authConnector = mock[AuthConnector]

        when(authConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(new ~(None, None)))

        val authAction =
          new AuthenticatedIdentifierActionImpl(
            new FakeFailingAuthConnector(new MissingBearerToken),
            frontendAppConfig,
            bodyParsers
          )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "key"))

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)

        application.stop()
      }
    }

    "the user fails auth with UnauthorizedException" must {

      "redirect the user to technical difficulties with logs" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authConnector = mock[AuthConnector]

        when(authConnector.authorise[Option[String] ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(new ~(None, None)))

        val authAction =
          new AuthenticatedIdentifierActionImpl(
            new FakeFailingAuthConnector(new UnauthorizedException("unAuth")),
            frontendAppConfig,
            bodyParsers
          )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest.withSession(SessionKeys.sessionId -> "key"))

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must endWith("/technical-difficulties")

        application.stop()
      }
    }

    "the user's session has expired" must {

      "redirect to unauthenticated" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new BearerTokenExpired),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)

        application.stop()
      }
    }

    "the user doesn't have sufficient enrolments" must {

      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new InsufficientEnrolments),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)

        application.stop()
      }
    }

    "session Id present: the user doesn't have sufficient confidence level" must {

      "redirect the user to I.V." in {
        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new InsufficientConfidenceLevel),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest("", "?key=id").withSession(SessionKeys.sessionId -> "id"))

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(
          "http://localhost:9948/iv-stub/uplift?" +
            "origin=EE&" +
            "confidenceLevel=200&" +
            "completionURL=http://localhost:9334/employee-expenses/session-key?key=id&" +
            "failureURL=http://localhost:9334/employee-expenses/identity-verification-failed"
        )

        application.stop()
      }
    }

    "session Id absent: the user doesn't have sufficient confidence level" must {

      "redirect the user to session expired" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new InsufficientConfidenceLevel),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)

        application.stop()
      }
    }

    "the user used an unaccepted auth provider" must {

      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new UnsupportedAuthProvider),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)

        application.stop()
      }
    }

    "the user has an unsupported affinity group" must {

      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new UnsupportedAffinityGroup),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)

        application.stop()
      }
    }

    "the user has an unsupported credential role" must {

      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new UnsupportedCredentialRole),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)

        application.stop()
      }
    }

    "an unexpected exception occurs" must {

      "redirect the user to the technical difficulties page" in {

        val application = applicationBuilder(userAnswers = None).build()

        val bodyParsers = application.injector.instanceOf[BodyParsers.Default]

        val authAction = new AuthenticatedIdentifierActionImpl(
          new FakeFailingAuthConnector(new Exception),
          frontendAppConfig,
          bodyParsers
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(routes.TechnicalDifficultiesController.onPageLoad.url)

        application.stop()
      }
    }
  }

}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext
  ): Future[A] =
    Future.failed(exceptionToReturn)

}

class FakePassingAuthConnector @Inject() (stubbedRetrievalResult: Future[_]) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext
  ): Future[A] =
    stubbedRetrievalResult.map(_.asInstanceOf[A])(global)

}
