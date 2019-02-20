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

package controllers.transport

import config.{ClaimAmounts, NavConstant}
import controllers.actions._
import forms.TransportVehicleTradeFormProvider
import javax.inject.{Inject, Named}
import models.{Enumerable, Mode, TransportVehicleTrade}
import navigation.Navigator
import pages.ClaimAmount
import pages.transport.TransportVehicleTradePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.transport.TransportVehicleTradeView

import scala.concurrent.{ExecutionContext, Future}

class TransportVehicleTradeController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 sessionRepository: SessionRepository,
                                                 @Named(NavConstant.transport) navigator: Navigator,
                                                 identify: UnauthenticatedIdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 formProvider: TransportVehicleTradeFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: TransportVehicleTradeView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TransportVehicleTradePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TransportVehicleTradePage, value))
            amount: Int = value match {
              case TransportVehicleTrade.Builder => ClaimAmounts.Transport.VehicleTrade.buildersRepairersWagonLifters
              case TransportVehicleTrade.VehicleRepairerWagonLifter => ClaimAmounts.Transport.VehicleTrade.buildersRepairersWagonLifters
              case TransportVehicleTrade.RailwayVehiclePainter => ClaimAmounts.Transport.Railways.vehiclePainters
              case TransportVehicleTrade.Letterer => ClaimAmounts.Transport.VehicleTrade.paintersLetterersAssistants
              case TransportVehicleTrade.BuildersAssistantOrRepairersAssistant => ClaimAmounts.Transport.VehicleTrade.paintersLetterersAssistants
              case TransportVehicleTrade.NoneOfTheAbove => ClaimAmounts.Transport.VehicleTrade.allOther
            }
            newAnswers <- Future.fromTry(updatedAnswers.set(ClaimAmount, amount))
            _ <- sessionRepository.set(newAnswers)
          } yield Redirect(navigator.nextPage(TransportVehicleTradePage, mode)(newAnswers))
        }
      )
  }
}
