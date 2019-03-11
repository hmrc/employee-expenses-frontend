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

import config.{FrontendAppConfig, NavConstant}
import controllers.actions._
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions, EmployerContributionPage, ExpensesEmployerPaidPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.{AuthedSessionRepository, SessionRepository}
import service.ClaimAmountService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.SaveToSession
import views.html.ClaimAmountView

import scala.concurrent.{ExecutionContext, Future}

class ClaimAmountController @Inject()(
                                       appConfig: FrontendAppConfig,
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       authedSessionRepository: AuthedSessionRepository,
                                       @Named(NavConstant.generic) navigator: Navigator,
                                       identify: UnauthenticatedIdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ClaimAmountView,
                                       claimAmountService: ClaimAmountService,
                                       config: FrontendAppConfig,
                                       save: SaveToSession
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  import claimAmountService._

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {

    implicit request =>
      (request.userAnswers.get(EmployerContributionPage), request.userAnswers.get(ClaimAmount), request.userAnswers.get(ExpensesEmployerPaidPage)) match {
        case (Some(_), Some(claimAmount), employerContribution) =>
          val claimAmountAndAnyDeductions: Int = calculateClaimAmount(request.userAnswers, claimAmount)

          for {
            saveClaimAmountAndAnyDeductions <- Future.fromTry(request.userAnswers.set(ClaimAmountAndAnyDeductions, claimAmountAndAnyDeductions))
            _ <- save.toSession(request, saveClaimAmountAndAnyDeductions)
          } yield {
              Ok(view(
                claimAmount = claimAmount,
                employerContribution = employerContribution,
                band1 = appConfig.taxPercentageBand1,
                calculatedBand1 = calculateTax(appConfig.taxPercentageBand1, claimAmountAndAnyDeductions),
                band2 = appConfig.taxPercentageBand2,
                calculatedBand2 = calculateTax(appConfig.taxPercentageBand2, claimAmountAndAnyDeductions),
                scotlandBand1 = appConfig.taxPercentageScotlandBand1,
                calculatedScotlandBand1 = calculateTax(appConfig.taxPercentageScotlandBand1, claimAmountAndAnyDeductions),
                scotlandBand2 = appConfig.taxPercentageScotlandBand2,
                calculatedScotlandBand2 = calculateTax(appConfig.taxPercentageScotlandBand2, claimAmountAndAnyDeductions),
                onwardRoute = navigator.nextPage(ClaimAmount, mode)(request.userAnswers).url
              ))
          }

        case _ =>
          Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}
