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

package controllers

import controllers.actions._
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.{ClaimAmount, EmployerContributionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.ClaimAmountService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ClaimAmountView

import scala.concurrent.ExecutionContext

class ClaimAmountController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       @Named("Generic") navigator: Navigator,
                                       identify: UnauthenticatedIdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ClaimAmountView,
                                       claimAmountService: ClaimAmountService
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode : Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      (request.userAnswers.get(EmployerContributionPage), request.userAnswers.get(ClaimAmount)) match {
        case (Some(_), Some(_)) =>
          Ok(view(
            claimAmount = claimAmountService.actualClaimAmount(request.userAnswers),
            band1 = claimAmountService.band1(request.userAnswers),
            band2 = claimAmountService.band2(request.userAnswers)
          ))
        case _ =>
          Redirect(routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode : Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      Redirect(navigator.nextPage(ClaimAmount, mode)(request.userAnswers))
  }
}
