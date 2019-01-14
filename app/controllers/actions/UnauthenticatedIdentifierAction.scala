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
import controllers.routes
import javax.inject.Singleton
import models.requests.IdentifierRequest
import play.api.mvc.Results.Redirect
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnauthenticatedIdentifierAction @Inject()(
                                                 override val authConnector: AuthConnector,
                                                 config: FrontendAppConfig,
                                                 val parser: BodyParsers.Default
                                               )
                                               (implicit val executionContext: ExecutionContext) extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised().retrieve(Retrievals.internalId and Retrievals.nino) {
      case Some(internalId) ~ Some(nino) =>
        block(IdentifierRequest(request, request.session.data("sessionId"), Some(nino)))
    } recoverWith {
      case _ => hc.sessionId match {
        case Some(id) =>
          block(IdentifierRequest(request, id.value))
        case _ =>
          Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
    }
  }
}
