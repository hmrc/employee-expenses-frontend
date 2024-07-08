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

import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierActionImpl @Inject()(override val authConnector: AuthConnector,
                                                  config: FrontendAppConfig,
                                                  val parser: BodyParsers.Default
                                                 )(implicit val executionContext: ExecutionContext) extends AuthenticatedIdentifierAction with AuthorisedFunctions with Logging {

  object LT200 {
    def unapply(confLevel: ConfidenceLevel): Option[ConfidenceLevel] =
      if (confLevel.level < ConfidenceLevel.L200.level) Some(confLevel) else None
  }

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised()
      .retrieve(v2.Retrievals.nino and v2.Retrievals.internalId and v2.Retrievals.affinityGroup and v2.Retrievals.confidenceLevel) {
        case _ ~ _ ~ Some(AffinityGroup.Agent) ~ _ =>
          Future(Redirect(controllers.routes.UnauthorisedController.onPageLoad))
        case _ ~ _ ~ Some(AffinityGroup.Individual | AffinityGroup.Organisation) ~ LT200(_) =>
          Future.successful(upliftIfSessionNotExpired(hc.sessionId.map(_.value))(request))
        case Some(nino) ~ Some(internalId) ~ _ ~ _ =>
          block(
            IdentifierRequest(
              request,
              Authed(internalId),
              Some(nino)
            )
          )
        case _ =>
          throw new UnauthorizedException("Unauthorized exception: missing nino or internal id")
      } recover {
      case _: NoActiveSession =>
        unauthorised(hc.sessionId.map(_.value), request)
      case _: InsufficientConfidenceLevel =>
        upliftIfSessionNotExpired(hc.sessionId.map(_.value))(request)
      case _: AuthorisationException =>
        Redirect(controllers.routes.UnauthorisedController.onPageLoad)
      case e =>
        logger.error(s"[AuthenticatedIdentifierAction][authorised] failed $e", e)
        Redirect(controllers.routes.TechnicalDifficultiesController.onPageLoad)
    }
  }

  def unauthorised(sessionId: Option[String], request: Request[_]): Result = {
    sessionId match {
      case Some(id) =>
        Redirect(config.loginUrl, Map("continue" -> Seq(s"${config.loginContinueUrl + id}")))
      case _ =>
        Redirect(controllers.routes.SessionExpiredController.onPageLoad)
    }
  }

  def insufficientConfidence(queryString: String, request: Request[_]): Result = {
    Redirect(s"${config.ivUpliftUrl}?origin=EE&confidenceLevel=200" +
      s"&completionURL=${config.authorisedCallback + queryString}" +
      s"&failureURL=${config.unauthorisedCallback}")
  }

  def upliftIfSessionNotExpired(sessionId: Option[String])(implicit request: Request[_]): Result = {
    sessionId match {
      case Some(id) => insufficientConfidence(request.getQueryString("key").getOrElse(id), request)
      case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad)
    }
  }

}

trait AuthenticatedIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]
