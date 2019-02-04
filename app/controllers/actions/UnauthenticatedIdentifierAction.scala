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
import javax.inject.Singleton
import models.requests.IdentifierRequest
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UnauthenticatedIdentifierAction @Inject()(
                                                 config: FrontendAppConfig,
                                                 val parser: BodyParsers.Default
                                               )
                                               (implicit val executionContext: ExecutionContext) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    implicit val hideAccountMenu: Boolean = hc.authorization.isEmpty

    val existingMongoKey = request.session.get(config.mongoKey)

    val mongoKey: String = existingMongoKey
      .orElse(hc.sessionId.map(_.value))
      .getOrElse(throw new Exception("[UnauthenticatedIdentifierAction] No mongoKey created"))

    block(IdentifierRequest(request, mongoKey)).map {
      result =>
        if (existingMongoKey.isEmpty) {
          result.addingToSession(config.mongoKey -> mongoKey)(request)
        } else {
          result
        }
    }
  }
}
