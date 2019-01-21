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
import models.{EmployerContribution, Mode}
import navigation.Navigator
import pages.{ClaimAmount, EmployerContributionPage, ExpensesEmployerPaidPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
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
                                       view: ClaimAmountView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  def calculatePercentage (amount: Double, deduction: Option[Double] = None, percentage: Double): String =  {
    val calc = deduction match {
      case Some(deduction) => ((amount - deduction) / 100) * percentage
      case _ => (amount / 100) * percentage
    }
    BigDecimal(calc).setScale(2).toString
  }

  def onPageLoad(mode : Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      request.userAnswers.get(ClaimAmount) match {
        case Some(amount) => {
          request.userAnswers.get(EmployerContributionPage) match {
            case Some(EmployerContribution.Some) => {
              request.userAnswers.get(ExpensesEmployerPaidPage) match {
                case Some(deduction) => {
                  val band1 = calculatePercentage(amount, Some(deduction), percentage = 20)
                  val band2 = calculatePercentage(amount, Some(deduction), percentage = 40)

                  Ok(view(amount, Some(band1), Some(band2)))
                }
                case _ =>
                  Redirect(routes.SessionExpiredController.onPageLoad())
              }
            }
            case Some(EmployerContribution.None) => {
              val band1 = calculatePercentage(amount, percentage = 20)
              val band2 = calculatePercentage(amount, percentage = 40)

              Ok(view(amount, Some(band1), Some(band2)))
            }
            case _ =>
              Redirect(routes.SessionExpiredController.onPageLoad())
          }
        }
        case _ =>
          Redirect(routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode : Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      Redirect(navigator.nextPage(ClaimAmount, mode)(request.userAnswers))
  }
}
