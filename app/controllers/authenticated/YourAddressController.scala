/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.authenticated

import config.NavConstant
import connectors.CitizenDetailsConnector
import controllers.actions._
import controllers.authenticated.routes._
import controllers.routes._
import javax.inject.{Inject, Named}
import models.{Address, Mode}
import navigation.Navigator
import pages.CitizenDetailsAddress
import pages.authenticated.YourAddressPage
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class YourAddressController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       citizenDetailsConnector: CitizenDetailsConnector,
                                       sessionRepository: SessionRepository,
                                       @Named(NavConstant.authenticated) navigator: Navigator,
                                       identify: AuthenticatedIdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      citizenDetailsConnector.getAddress(request.nino.get).flatMap {
        response =>
          response.status match {
            case OK =>
              Json.parse(response.body).validate[Address] match {
                case JsSuccess(address, _) if validAddress(address) =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(CitizenDetailsAddress, address))
                      _ <- sessionRepository.set(request.identifier, updatedAnswers)
                    } yield Redirect(navigator.nextPage(YourAddressPage, mode)(updatedAnswers))
               case _ =>
                  Future.successful(Redirect(navigator.nextPage(YourAddressPage, mode)(request.userAnswers)))
              }
            case LOCKED =>
              Future.successful(Redirect(PhoneUsController.onPageLoad()))
            case _ =>
              Future.successful(Redirect(navigator.nextPage(YourAddressPage, mode)(request.userAnswers)))
          }
      }.recoverWith {
        case e =>
          Logger.error(s"[YourAddressController][citizenDetailsConnector.getAddress] failed $e", e)
          Future.successful(Redirect(TechnicalDifficultiesController.onPageLoad()))
      }
  }

  private def validAddress(address: Address): Boolean =
    address.line1.exists(_.trim.nonEmpty) && address.postcode.exists(_.trim.nonEmpty)

}
