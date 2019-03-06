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

import akka.actor.Status.Success
import config.FrontendAppConfig
import connectors.TaiConnector
import controllers.actions._
import javax.inject.Inject
import models.{TaxCodeRecord, TaxYearSelection}
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions}
import pages.authenticated.{RemoveFRECodePage, TaxYearSelectionPage, YourAddressPage, YourEmployerPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.{ClaimAmountService, TaiService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ConfirmationView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        sessionRepository: SessionRepository,
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
              if (result.head.taxCode(0).toString.equalsIgnoreCase("s")) {
                val scottishBasicRate = appConfig.taxPercentageScotlandBand1
                val scottishHigherRate = appConfig.taxPercentageScotlandBand2

                Ok(view(validTaxYearSelection, removeFreOption, updateEmployerInfo, updateAddressInfo, fullClaimAmount, scottishBasicRate, scottishHigherRate)).withNewSession
              } else {
                val basicRate = appConfig.taxPercentageBand1
                val higherRate = appConfig.taxPercentageBand2

                Ok(view(validTaxYearSelection, removeFreOption, updateEmployerInfo, updateAddressInfo, fullClaimAmount, basicRate, higherRate)).withNewSession
              }
          }.recoverWith {
            case _ =>
              Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
          }
        case _ =>
          Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}
