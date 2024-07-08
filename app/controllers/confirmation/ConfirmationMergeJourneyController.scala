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

package controllers.confirmation

import config.NavConstant
import controllers.actions.{AuthenticatedIdentifierAction, DataRequiredAction, DataRetrievalAction}
import controllers.routes.SessionExpiredController
import models.NormalMode
import navigation.Navigator
import pages.authenticated.{TaxYearSelectionPage, YourEmployerPage}
import pages.confirmation.ConfirmationMergeJourneyPage
import pages.{ClaimAmountAndAnyDeductions, FREResponse}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.confirmation.ConfirmationMergeJourneyView

import javax.inject.{Inject, Named}

class ConfirmationMergeJourneyController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          @Named(NavConstant.authenticated) navigator: Navigator,
                                          identify: AuthenticatedIdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          val controllerComponents: MessagesControllerComponents,
                                          confirmationMergeJourneyView: ConfirmationMergeJourneyView,
                                        ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      (
        request.userAnswers.get(FREResponse),
        request.userAnswers.get(YourEmployerPage),
        request.userAnswers.get(ClaimAmountAndAnyDeductions),
        request.userAnswers.get(TaxYearSelectionPage)
      ) match {
        case (Some(_), Some(_), Some(_), Some(_)) =>
          Ok(confirmationMergeJourneyView(continueUrl = navigator.nextPage(ConfirmationMergeJourneyPage, NormalMode)(request.userAnswers).url))
        case _ => Redirect(SessionExpiredController.onPageLoad)
      }
  }
}
