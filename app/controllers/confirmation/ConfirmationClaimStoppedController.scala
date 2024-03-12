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

package controllers.confirmation

import controllers.actions.{AuthenticatedIdentifierAction, DataRequiredAction, DataRetrievalAction}
import models.mergedJourney.ClaimStopped
import controllers.mergedJourney.routes.MergedJourneyController
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.confirmation.ConfirmationClaimStoppedView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ConfirmationClaimStoppedController @Inject()(override val messagesApi: MessagesApi,
                                                   identify: AuthenticatedIdentifierAction,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   confirmationClaimStoppedView: ConfirmationClaimStoppedView,
                                                  )(implicit val ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    if (request.userAnswers.isMergedJourney) {
      Ok(confirmationClaimStoppedView(Some(MergedJourneyController.mergedJourneyContinue(journey = "fre", status = ClaimStopped).url)))
    } else {
      Ok(confirmationClaimStoppedView())
    }
  }
}
