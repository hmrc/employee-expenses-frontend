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

import controllers.confirmation.{routes => confRoutes}
import controllers.routes
import models.NormalMode
import models.requests.{DataRequest, OptionalDataRequest}
import navigation.AuthenticatedNavigator
import pages.SubmittedClaim
import pages.authenticated.Submission
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRequiredActionImpl @Inject() (navigator: AuthenticatedNavigator)(
    implicit val executionContext: ExecutionContext
) extends DataRequiredAction {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = {

    lazy val currentlyOnTheConfirmationPage = Seq(
      confRoutes.ConfirmationCurrentYearOnlyController.onPageLoad().url,
      confRoutes.ConfirmationCurrentAndPreviousYearsController.onPageLoad().url,
      confRoutes.ConfirmationPreviousYearsOnlyController.onPageLoad().url,
      confRoutes.ConfirmationClaimStoppedController.onPageLoad().url,
      confRoutes.ConfirmationMergeJourneyController.onPageLoad().url
    ).contains(request.uri)

    request.userAnswers match {
      case None =>
        Future.successful(Left(Redirect(routes.SessionExpiredController.onPageLoad)))
      case Some(data) if data.get(SubmittedClaim).isDefined && !currentlyOnTheConfirmationPage =>
        Future.successful(Left(Redirect(navigator.nextPage(Submission, NormalMode)(data))))
      case Some(data) =>
        Future.successful(Right(DataRequest(request.request, request.identifier, request.nino, data)))
    }
  }

}

trait DataRequiredAction extends ActionRefiner[OptionalDataRequest, DataRequest]
