/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.transport.WhichRailwayTradeFormProvider
import javax.inject.{Inject, Named}
import models.{Enumerable, Mode, WhichRailwayTrade}
import navigation.Navigator
import pages.ClaimAmount
import pages.transport.WhichRailwayTradePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import repositories.SessionRepository
import views.html.transport.WhichRailwayTradeView

import scala.concurrent.{ExecutionContext, Future}

class WhichRailwayTradeController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             @Named(NavConstant.transport) navigator: Navigator,
                                             identify: UnauthenticatedIdentifierAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             formProvider: WhichRailwayTradeFormProvider,
                                             val controllerComponents: MessagesControllerComponents,
                                             view: WhichRailwayTradeView,
                                             sessionRepository: SessionRepository
                                           )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhichRailwayTradePage) match {
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
          val claimAmount = value match {
            case WhichRailwayTrade.VehiclePainters => ClaimAmounts.Transport.Railways.vehiclePainters
            case WhichRailwayTrade.VehicleRepairersWagonLifters => ClaimAmounts.Transport.Railways.vehicleRepairersWagonLifters
            case WhichRailwayTrade.NoneOfTheAbove => ClaimAmounts.Transport.Railways.allOther
          }

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichRailwayTradePage, value)
              .flatMap(_.set(ClaimAmount, claimAmount))
            )
            _ <- sessionRepository.set(request.identifier, updatedAnswers)
          } yield Redirect(navigator.nextPage(WhichRailwayTradePage, mode)(updatedAnswers))
        }
      )
  }
}
