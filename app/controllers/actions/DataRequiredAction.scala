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

import controllers.routes
import javax.inject.Inject
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class DataRequiredActionImpl @Inject()(implicit val executionContext: ExecutionContext) extends DataRequiredAction {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    request.userAnswers match {
      case None =>
        Future.successful(Left(Redirect(routes.SessionExpiredController.onPageLoad())))
      case Some(data) =>
        Future.successful(Right(DataRequest(request.request, request.mongoKey, request.nino, data)))
    }
  }
}

trait DataRequiredAction extends ActionRefiner[OptionalDataRequest, DataRequest]
