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

import config.{FrontendAppConfig, NavConstant}
import controllers.actions._
import forms.ExpensesEmployerPaidFormProvider
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.ExpensesEmployerPaidPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.{AuthedSessionRepository, SessionRepository}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.SaveToSession
import views.html.ExpensesEmployerPaidView

import scala.concurrent.{ExecutionContext, Future}

class ExpensesEmployerPaidController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                sessionRepository: SessionRepository,
                                                authedSessionRepository: AuthedSessionRepository,
                                                @Named(NavConstant.generic) navigator: Navigator,
                                                identify: UnauthenticatedIdentifierAction,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                formProvider: ExpensesEmployerPaidFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: ExpensesEmployerPaidView,
                                                config: FrontendAppConfig,
                                                save: SaveToSession
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ExpensesEmployerPaidPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ExpensesEmployerPaidPage, value))
            _ <- save.toSession(request, updatedAnswers)
          } yield Redirect(navigator.nextPage(ExpensesEmployerPaidPage, mode)(updatedAnswers))
        }
      )
  }
}
