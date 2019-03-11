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
import models.TaxYearSelection
import pages.ClaimAmountAndAnyDeductions
import pages.authenticated.{RemoveFRECodePage, TaxYearSelectionPage, YourAddressPage, YourEmployerPage}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.ClaimAmountService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ConfirmationView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        identify: AuthenticatedIdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: ConfirmationView,
                                        claimAmountService: ClaimAmountService,
                                        appConfig: FrontendAppConfig,
                                        taiConnector: TaiConnector
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val claimAmount: Option[Int] = request.userAnswers.get(ClaimAmountAndAnyDeductions)
      val taxYearSelection: Option[Seq[TaxYearSelection]] = request.userAnswers.get(TaxYearSelectionPage)
      val updateEmployerInfo: Option[Boolean] = request.userAnswers.get(YourEmployerPage)
      val updateAddressInfo: Option[Boolean] = request.userAnswers.get(YourAddressPage)
      val removeFreOption: Option[TaxYearSelection] = request.userAnswers.get(RemoveFRECodePage)
      val nino = request.nino

      (claimAmount, taxYearSelection, nino) match {
        case (Some(fullClaimAmount), Some(validTaxYearSelection), Some(validNino)) =>

          taiConnector.taiTaxCodeRecords(validNino).map {
            result =>
              val basicRate: Int = if (result.head.taxCode(0) equals 'S') {
                appConfig.taxPercentageScotlandBand1
              } else {
                appConfig.taxPercentageBand1
              }

              val higherRate: Int = if (result.head.taxCode(0) equals 'S') {
                appConfig.taxPercentageScotlandBand2
              } else {
                appConfig.taxPercentageBand2
              }

              val claimAmountBasicRate = claimAmountService.calculateTax(basicRate, fullClaimAmount)
              val claimAmountHigherRate = claimAmountService.calculateTax(higherRate, fullClaimAmount)

              Ok(view(
                taxYearSelections = validTaxYearSelection,
                removeFreOption = removeFreOption,
                updateEmployerInfo = updateEmployerInfo,
                updateAddressInfo = updateAddressInfo,
                claimAmount = fullClaimAmount,
                basicRate = basicRate,
                higherRate = higherRate,
                claimAmountBasicRate = claimAmountBasicRate,
                claimAmountHigherRate = claimAmountHigherRate)).withNewSession

          }.recoverWith {
            case e =>
              Logger.error(s"[ConfirmationController][taiConnector.taiTaxCodeRecord] Call failed $e", e)
              Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        case _ =>
          Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}
