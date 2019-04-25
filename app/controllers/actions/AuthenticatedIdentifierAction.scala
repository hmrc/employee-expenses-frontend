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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes._
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.libs.json.Reads
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthProvider.Verify
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.OptionalRetrieval
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierActionImpl @Inject()(
                                                   override val authConnector: AuthConnector,
                                                   config: FrontendAppConfig,
                                                   val parser: BodyParsers.Default
                                                 )(implicit val executionContext: ExecutionContext) extends AuthenticatedIdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised(AuthProviders(AuthProvider.Verify) or (AffinityGroup.Individual and ConfidenceLevel.L200))
      .retrieve(OptionalRetrieval("nino", Reads.StringReads) and OptionalRetrieval("internalId", Reads.StringReads)) {
        x =>
          val nino = x.a.getOrElse(throw new UnauthorizedException("Unable to retrieve nino"))
          val internalId = x.b.getOrElse(throw new UnauthorizedException("Unable to retrieve internalId"))

          block(IdentifierRequest(request, Authed(internalId), Some(nino)))
      } recover {
      case _: UnauthorizedException | _: NoActiveSession =>
        unauthorised(hc.sessionId.map(_.value))
      case _: InsufficientConfidenceLevel =>
        insufficientConfidence(request.getQueryString("key"))
      case _: InsufficientEnrolments | _: UnsupportedAuthProvider | _: UnsupportedAffinityGroup | _: UnsupportedCredentialRole =>
        Redirect(UnauthorisedController.onPageLoad())
      case e =>
        Logger.error(s"[AuthenticatedIdentifierAction][authorised] failed $e", e)
        Redirect(TechnicalDifficultiesController.onPageLoad())
    }
  }

  def unauthorised(sessionId: Option[String]): Result = {
    sessionId match {
      case Some(id) =>
        Redirect(config.loginUrl, Map("continue" -> Seq(s"${config.loginContinueUrl + id}")))
      case _ =>
        Redirect(SessionExpiredController.onPageLoad())
    }
  }

  def insufficientConfidence(queryString: Option[String]): Result = {
    queryString match {
      case Some(key) =>
        Redirect(s"${config.ivUpliftUrl}?origin=EE&confidenceLevel=200" +
          s"&completionURL=${config.authorisedCallback + key}" +
          s"&failureURL=${config.unauthorisedCallback}")
      case _ =>
        Redirect(UnauthorisedController.onPageLoad())
    }
  }

}

trait AuthenticatedIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]
