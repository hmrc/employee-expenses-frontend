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

package controllers.confirmation

import config.FrontendAppConfig
import controllers.actions.{AuthenticatedIdentifierAction, DataRequiredAction, DataRetrievalAction}
import controllers.routes._
import javax.inject.Inject
import models.TaxYearSelection.CurrentYear
import models.{Address, Rates, TaiTaxYear, TaxYearSelection}
import pages.authenticated.{YourAddressPage, YourEmployerPage}
import pages.{CitizenDetailsAddress, ClaimAmountAndAnyDeductions, FREResponse}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.{ClaimAmountService, TaiService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.confirmation.ConfirmationCurrentYearOnlyView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationCurrentYearOnlyController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       identify: AuthenticatedIdentifierAction,
                                                       getData: DataRetrievalAction,
                                                       requireData: DataRequiredAction,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       claimAmountService: ClaimAmountService,
                                                       taiService: TaiService,
                                                       sessionRepository: SessionRepository,
                                                       confirmationCurrentYearOnlyView: ConfirmationCurrentYearOnlyView
                                                     )(implicit ec: ExecutionContext, appConfig: FrontendAppConfig) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      (
        request.userAnswers.get(FREResponse),
        request.userAnswers.get(YourEmployerPage),
        request.userAnswers.get(ClaimAmountAndAnyDeductions)
      ) match {
        case (Some(freResponse), Some(employer), Some(claimAmountAndAnyDeductions)) =>
          val taxYear = TaiTaxYear(TaxYearSelection.getTaxYear(CurrentYear))
          taiService.taxCodeRecords(request.nino.get, taxYear).map {
            result =>
              val claimAmountsAndRates: Seq[Rates] = claimAmountService.getRates(result, claimAmountAndAnyDeductions)
              val addressOption: Option[Address] = request.userAnswers.get(CitizenDetailsAddress)
              sessionRepository.remove(request.identifier)
              Ok(confirmationCurrentYearOnlyView(claimAmountsAndRates, claimAmountAndAnyDeductions, Some(employer), addressOption, freResponse))
          }.recoverWith {
            case e =>
              Logger.error(s"[ConfirmationCurrentYearOnlyController][taiConnector.taiTaxCodeRecord] Call failed $e", e)
              Future.successful(Redirect(TechnicalDifficultiesController.onPageLoad()))
          }
        case _ =>
          Future.successful(Redirect(SessionExpiredController.onPageLoad()))
      }
  }
}
