/*
 * Copyright 2024 HM Revenue & Customs
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

import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.mvc.Results.Redirect
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthConnector

import java.net.URLEncoder
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MergedJourneyIdentifierActionImpl @Inject()(override val authConnector: AuthConnector,
                                                  config: FrontendAppConfig,
                                                  override val parser: BodyParsers.Default
                                                 )(implicit override val executionContext: ExecutionContext)
  extends AuthenticatedIdentifierActionImpl(authConnector, config, parser) with MergedJourneyIdentifierAction {

  override def unauthorised(sessionId: Option[String], request: Request[_]): Result = {
    Redirect(config.loginUrl, Map("continue" -> Seq(request.uri)))
  }

  override def insufficientConfidence(queryString: String, request: Request[_]): Result = {
    Redirect(s"${config.ivUpliftUrl}?origin=EE&confidenceLevel=200" +
      s"&completionURL=${URLEncoder.encode(request.uri, "UTF-8")}" +
      s"&failureURL=${config.unauthorisedCallback}")
  }
}

trait MergedJourneyIdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]