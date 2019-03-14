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
import models.{Mode, Rates}
import navigation.Navigator
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions, EmployerContributionPage, ExpensesEmployerPaidPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.ClaimAmountService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ClaimAmountView

import scala.concurrent.{ExecutionContext, Future}

class ClaimAmountController @Inject()(
                                       appConfig: FrontendAppConfig,
                                       override val messagesApi: MessagesApi,
                                       @Named(NavConstant.generic) navigator: Navigator,
                                       identify: UnauthenticatedIdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ClaimAmountView,
                                       claimAmountService: ClaimAmountService,
                                       config: FrontendAppConfig,
                                       sessionRepository: SessionRepository
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  import claimAmountService._

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {

    implicit request =>
      (request.userAnswers.get(EmployerContributionPage), request.userAnswers.get(ClaimAmount), request.userAnswers.get(ExpensesEmployerPaidPage)) match {
        case (Some(_), Some(claimAmount), employerContribution) =>
          val claimAmountAndAnyDeductions: Int = calculateClaimAmount(request.userAnswers, claimAmount)

          for {
            saveClaimAmountAndAnyDeductions <- Future.fromTry(request.userAnswers.set(ClaimAmountAndAnyDeductions, claimAmountAndAnyDeductions))
            _ <- sessionRepository.set(request.identifier, saveClaimAmountAndAnyDeductions)
          } yield {

            val claimAmountsAndRates = Rates(
              basicRate = appConfig.taxPercentageBand1,
              higherRate = appConfig.taxPercentageBand2,
              calculatedBasicRate = calculateTax(appConfig.taxPercentageBand1, claimAmountAndAnyDeductions),
              calculatedHigherRate = calculateTax(appConfig.taxPercentageBand2, claimAmountAndAnyDeductions),
              prefix = None
            )

            val scottishClaimAmountsAndRates = Rates(
              basicRate = appConfig.taxPercentageScotlandBand1,
              higherRate = appConfig.taxPercentageScotlandBand2,
              calculatedBasicRate = calculateTax(appConfig.taxPercentageScotlandBand1, claimAmountAndAnyDeductions),
              calculatedHigherRate = calculateTax(appConfig.taxPercentageScotlandBand2, claimAmountAndAnyDeductions),
              prefix = Some('S')
            )

            Ok(view(
              claimAmount = claimAmount,
              employerContribution = employerContribution,
              claimAmountsAndRates = claimAmountsAndRates,
              scottishClaimAmountsAndRates = scottishClaimAmountsAndRates,
              onwardRoute = navigator.nextPage(ClaimAmount, mode)(request.userAnswers).url
            ))
          }

        case _ =>
          Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}
