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

package controllers.manufacturing

import config.{ClaimAmounts, NavConstant}
import controllers.actions._
import forms.manufacturing.TypeOfManufacturingFormProvider
import javax.inject.{Inject, Named}
import models.TypeOfManufacturing._
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.ClaimAmount
import pages.manufacturing.TypeOfManufacturingPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import repositories.SessionRepository
import views.html.manufacturing.TypeOfManufacturingView

import scala.concurrent.{ExecutionContext, Future}

class TypeOfManufacturingController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               @Named(NavConstant.manufacturing) navigator: Navigator,
                                               identify: UnauthenticatedIdentifierAction,
                                               getData: DataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               formProvider: TypeOfManufacturingFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: TypeOfManufacturingView,
                                               sessionRepository: SessionRepository
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TypeOfManufacturingPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TypeOfManufacturingPage, value))
            newUserAnswers <- value match {
              case BrassCopper => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.Manufacturing.brassCopper))
              case Glass => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.Manufacturing.glass))
              case PreciousMetals => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.Manufacturing.quarryingPreciousMetals))
              case NoneOfAbove => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.defaultRate))
              case _ => Future.successful(updatedAnswers)
            }
            _ <- sessionRepository.set(request.identifier, newUserAnswers)
          } yield Redirect(navigator.nextPage(TypeOfManufacturingPage, mode)(newUserAnswers))
        }
      )
  }
}
