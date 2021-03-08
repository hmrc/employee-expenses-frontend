/*
 * Copyright 2021 HM Revenue & Customs
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
import controllers.routes.TechnicalDifficultiesController
import javax.inject.Singleton
import models.requests.IdentifierRequest
import play.api.Logger
import play.api.libs.json.Reads
import play.api.mvc.Results.Redirect
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.OptionalRetrieval
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, UnauthorizedException}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnauthenticatedIdentifierActionImpl @Inject()(
                                                     override val authConnector: AuthConnector,
                                                     config: FrontendAppConfig,
                                                     val parser: BodyParsers.Default
                                                   )(implicit val executionContext: ExecutionContext) extends UnauthenticatedIdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised()
      .retrieve(OptionalRetrieval("nino", Reads.StringReads) and OptionalRetrieval("internalId", Reads.StringReads)) {
      x =>
        val nino = x.a.getOrElse(throw new UnauthorizedException("Unable to retrieve nino"))
        val internalId = x.b.getOrElse(throw new UnauthorizedException("Unable to retrieve internalId"))

        block(IdentifierRequest(request, Authed(internalId), Some(nino)))
    }.recoverWith {
      case _: AuthorisationException | _: HttpException =>
        val sessionId: String = hc.sessionId.map(_.value).getOrElse(throw new Exception("no sessionId"))
        block(IdentifierRequest(request, UnAuthed(sessionId)))
      case e =>
        Logger.error(s"[UnauthenticatedIdentifierAction][authorised] failed $e", e)
        Future.successful(Redirect(TechnicalDifficultiesController.onPageLoad()))
    }
  }
}

trait UnauthenticatedIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

