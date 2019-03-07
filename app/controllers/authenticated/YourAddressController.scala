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

package controllers.authenticated

import config.NavConstant
import connectors.CitizenDetailsConnector
import controllers.actions._
import controllers.authenticated.routes._
import controllers.routes._
import forms.authenticated.YourAddressFormProvider
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.CitizenDetailsAddress
import pages.authenticated.YourAddressPage
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.authenticated.YourAddressView

import scala.concurrent.{ExecutionContext, Future}

class YourAddressController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       citizenDetailsConnector: CitizenDetailsConnector,
                                       sessionRepository: SessionRepository,
                                       @Named(NavConstant.authenticated) navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: YourAddressFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: YourAddressView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(YourAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      for {
        address <- citizenDetailsConnector.getAddress(request.nino.get)
      } yield {
        if (address.line1.exists(_.trim.nonEmpty) && address.postcode.exists(_.trim.nonEmpty)) {
          Ok(view(preparedForm, mode, address))
        } else {
          Redirect(UpdateYourAddressController.onPageLoad())
        }
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      citizenDetailsConnector.getAddress(request.nino.get).flatMap {
        address =>
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, address))),

            value => {
              for {
                ua1 <- Future.fromTry(request.userAnswers.set(YourAddressPage, value))
                ua2 <- Future.fromTry(ua1.set(CitizenDetailsAddress, address))
                _ <- sessionRepository.set(ua2)
              } yield Redirect(navigator.nextPage(YourAddressPage, mode)(ua2))
            }
          )
      }.recoverWith {
        case e =>
          Logger.error(s"[YourAddressController][citizenDetailsConnector.getAddress] failed $e", e)
          Future.successful(Redirect(SessionExpiredController.onPageLoad()))
      }
  }
}
