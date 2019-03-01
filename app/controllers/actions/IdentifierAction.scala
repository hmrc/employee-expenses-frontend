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
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedIdentifierAction @Inject()(
                                               override val authConnector: AuthConnector,
                                               config: FrontendAppConfig,
                                               val parser: BodyParsers.Default
                                             )
                                             (implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised(ConfidenceLevel.L200 and AffinityGroup.Individual).retrieve(Retrievals.nino) {
      x =>
        val nino = x.getOrElse(throw new UnauthorizedException("Unable to retrieve nino"))

        request.session.get(config.mongoKey) match {
          case Some(key) => block(IdentifierRequest(request, key, Some(nino)))
          case _ =>
            if (request.uri.contains("/employee-expenses/session-key")) {
              block(IdentifierRequest(request, ""))
            } else {
              Future.successful(Redirect(SessionExpiredController.onPageLoad()))
            }
        }
    } recover {
      case _: UnauthorizedException | _: NoActiveSession =>
        unauthorised(request.session.get(config.mongoKey))
      case _: InsufficientConfidenceLevel =>
        insufficientConfidence(request.getQueryString("key"))
      case _: InsufficientEnrolments | _: UnsupportedAuthProvider | _: UnsupportedAffinityGroup | _: UnsupportedCredentialRole =>
        Redirect(UnauthorisedController.onPageLoad())
      case e =>
        Logger.error(s"Technical difficulties error: $e", e)
        Redirect(TechnicalDifficultiesController.onPageLoad())
    }
  }

  def unauthorised(mongoKey: Option[String]): Result = {
    mongoKey match {
      case Some(key) =>
        Redirect(config.loginUrl, Map("continue" -> Seq(s"${config.loginContinueUrl + key}")))
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
        Redirect(SessionExpiredController.onPageLoad())
    }
  }

}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]
