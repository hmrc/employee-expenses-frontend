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

import config.FrontendAppConfig
import connectors.TaiConnector
import controllers.actions.{AuthenticatedIdentifierAction, DataRequiredAction, DataRetrievalAction}
import controllers.routes._
import javax.inject.Inject
import models.Rates
import pages.authenticated.{YourAddressPage, YourEmployerPage}
import pages.{ClaimAmountAndAnyDeductions, FREResponse}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.ClaimAmountService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.confirmation.CurrentYearConfirmationView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationCurrentYearOnlyController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       identify: AuthenticatedIdentifierAction,
                                                       getData: DataRetrievalAction,
                                                       requireData: DataRequiredAction,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       claimAmountService: ClaimAmountService,
                                                       appConfig: FrontendAppConfig,
                                                       taiConnector: TaiConnector,
                                                       sessionRepository: SessionRepository,
                                                       currentYearConfirmationView: CurrentYearConfirmationView
                                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      (
        request.userAnswers.get(ClaimAmountAndAnyDeductions),
        request.userAnswers.get(FREResponse),
        request.userAnswers.get(YourEmployerPage),
        request.userAnswers.get(YourAddressPage)
      ) match {
        case (Some(claimAmount), Some(freResponse), Some(employer), Some(address)) =>
          taiConnector.taiTaxCodeRecords(request.nino.get).map {
            result =>
              val claimAmountsAndRates: Seq[Rates] = claimAmountService.getRates(result, claimAmount)
              Ok(currentYearConfirmationView(claimAmountsAndRates, claimAmount, freResponse, employer, address))
          }.recoverWith {
            case e =>
              Logger.error(s"[ConfirmationController][taiConnector.taiTaxCodeRecord] Call failed $e", e)
              Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        case _ =>
          Future.successful(Redirect(SessionExpiredController.onPageLoad()))
      }
  }
}
