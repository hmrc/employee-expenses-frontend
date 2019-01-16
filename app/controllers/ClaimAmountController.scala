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
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{ClaimAmount, ExpensesEmployerPaidPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ClaimAmountView

import scala.concurrent.ExecutionContext

class ClaimAmountController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: UnauthenticatedIdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ClaimAmountView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode : Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      request.userAnswers.get(ClaimAmount).map {
        amount =>

          val deduction: Double = request.userAnswers.get(ExpensesEmployerPaidPage).map(_.toDouble).getOrElse(0)
          val band1Calc: Double = ((amount - deduction) / 100) * 20
          val band2Calc: Double = ((amount - deduction) / 100) * 40
          val band1: String = BigDecimal(band1Calc).setScale(2).toString
          val band2: String = BigDecimal(band2Calc).setScale(2).toString

          Ok(view(amount, Some(band1), Some(band2)))
      }.getOrElse (Redirect(routes.SessionExpiredController.onPageLoad()))
  }

  def onSubmit(mode : Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      Redirect(navigator.nextPage(ClaimAmount, mode)(request.userAnswers))
  }
}
