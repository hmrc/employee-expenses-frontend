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
import controllers.actions._
import javax.inject.Inject
import models.TaxYearSelection
import pages.ClaimAmount
import pages.authenticated.{RemoveFRECodePage, TaxYearSelectionPage, YourAddressPage, YourEmployerPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.ClaimAmountService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ConfirmationView

import scala.concurrent.ExecutionContext

class ConfirmationController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       sessionRepository: SessionRepository,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ConfirmationView,
                                       claimAmountService: ClaimAmountService,
                                       appConfig: FrontendAppConfig
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val claimAmount: Option[Int] = request.userAnswers.get(ClaimAmount)
      val taxYearSelection: Option[Seq[TaxYearSelection]] = request.userAnswers.get(TaxYearSelectionPage)
      val removeFre: Option[TaxYearSelection] = request.userAnswers.get(RemoveFRECodePage)
      val updateEmployerInfo: Option[Boolean] = request.userAnswers.get(YourEmployerPage)
      val updateAddressInfo: Option[Boolean] = request.userAnswers.get(YourAddressPage)

      (taxYearSelection, removeFre, claimAmount) match {
        case (Some(taxYears), removeFreOption, Some(fullClaimAmount)) =>
          val basicRate = claimAmountService.calculateTax(appConfig.taxPercentageBand1, fullClaimAmount)
          val higherRate = claimAmountService.calculateTax(appConfig.taxPercentageBand2, fullClaimAmount)

          Ok(view(taxYears, removeFreOption, updateEmployerInfo, updateAddressInfo, fullClaimAmount, basicRate, higherRate))
        case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
      }
  }
}
