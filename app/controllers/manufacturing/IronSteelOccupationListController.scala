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

package controllers.manufacturing

import config.{ClaimAmounts, NavConstant}
import controllers.actions._
import forms.manufacturing.IronSteelOccupationListFormProvider
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.ClaimAmount
import pages.manufacturing.IronSteelOccupationListPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.SaveToSession
import views.html.manufacturing.IronSteelOccupationListView

import scala.concurrent.{ExecutionContext, Future}

class IronSteelOccupationListController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   @Named(NavConstant.manufacturing) navigator: Navigator,
                                                   identify: UnauthenticatedIdentifierAction,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   formProvider: IronSteelOccupationListFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: IronSteelOccupationListView,
                                                   save: SaveToSession
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(IronSteelOccupationListPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          for {
            updatedAnswers <- if (value) {
              Future.fromTry(request.userAnswers.set(IronSteelOccupationListPage, value)
                .flatMap(_.set(ClaimAmount, ClaimAmounts.Manufacturing.IronSteel.list1))
              )
            } else {
              Future.fromTry(request.userAnswers.set(IronSteelOccupationListPage, value))
            }
            _ <- save.toSession(request, updatedAnswers)
          } yield Redirect(navigator.nextPage(IronSteelOccupationListPage, mode)(updatedAnswers))
        }
      )
  }
}
