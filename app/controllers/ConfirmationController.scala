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
import controllers.actions._
import javax.inject.Inject
import models.{FlatRateExpenseOptions, Rates, TaxYearSelection}
import pages.{ClaimAmountAndAnyDeductions, FREResponse}
import pages.authenticated.{RemoveFRECodePage, TaxYearSelectionPage, YourAddressPage, YourEmployerPage}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.ClaimAmountService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.confirmation._

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        identify: AuthenticatedIdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        claimAmountService: ClaimAmountService,
                                        appConfig: FrontendAppConfig,
                                        taiConnector: TaiConnector,
                                        currentYearConfirmationView: CurrentYearConfirmationView,
                                        previousYearConfirmationView: PreviousYearsConfirmationView,
                                        previousCurrentConfirmationView: PreviousCurrentYearsConfirmationView,
                                        claimStoppedConfirmationView: ClaimStoppedConfirmationView,

                                        sessionRepository: SessionRepository
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val claimAmount = request.userAnswers.get(ClaimAmountAndAnyDeductions)
      val updateEmployerInfo: Boolean = request.userAnswers.get(YourEmployerPage).isDefined
      val updateAddressInfo: Boolean = request.userAnswers.get(YourAddressPage).isDefined
      val removeFreOption = request.userAnswers.get(RemoveFRECodePage).isDefined
      val freReponse: Option[FlatRateExpenseOptions] = request.userAnswers.get(FREResponse)

      val taxYearSelection: Option[Seq[TaxYearSelection]] = request.userAnswers.get(TaxYearSelectionPage)

      (claimAmount, request.nino, removeFreOption, taxYearSelection, freReponse) match {
        case (_, _, true, _, _) => {
          sessionRepository.remove(request.identifier)
          Future.successful(Ok(claimStoppedConfirmationView()))
        }

        case (Some(fullClaimAmount), Some(nino), false, Some(taxYears), Some(freResponse)) =>
          val currentYearSelected = taxYears.contains(TaxYearSelection.CurrentYear)
          val currentYearMinus1 = taxYears.contains(TaxYearSelection.CurrentYearMinus1)

          taiConnector.taiTaxCodeRecords(nino).map {
            result =>
              sessionRepository.remove(request.identifier)
              val claimAmountsAndRates: Seq[Rates] = claimAmountService.getRates(result, fullClaimAmount)
              if (currentYearSelected && taxYears.length == 1) {
                Ok(currentYearConfirmationView(claimAmountsAndRates, fullClaimAmount, updateEmployerInfo, updateAddressInfo, freResponse))
              } else if (currentYearSelected && taxYears.length > 1) {
                Ok(previousCurrentConfirmationView(claimAmountsAndRates, fullClaimAmount, updateEmployerInfo, updateAddressInfo, currentYearMinus1, freResponse))
              } else {
                Ok(previousYearConfirmationView(claimAmountsAndRates, fullClaimAmount, updateEmployerInfo, updateAddressInfo, currentYearMinus1, freResponse))
              }

          }.recoverWith {
            case e =>
              Logger.error(s"[ConfirmationController][taiConnector.taiTaxCodeRecord] Call failed $e", e)
              Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        case _ =>
          Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }}
