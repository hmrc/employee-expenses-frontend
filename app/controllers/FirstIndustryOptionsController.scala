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

import com.google.inject.Inject
import config.{ClaimAmounts, NavConstant}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, UnauthenticatedIdentifierAction}
import forms.FirstIndustryOptionsFormProvider
import javax.inject.Named
import models.{Enumerable, FirstIndustryOptions, Mode}
import navigation.Navigator
import pages.{ClaimAmount, FirstIndustryOptionsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.FirstIndustryOptionsView

import scala.concurrent.{ExecutionContext, Future}

class FirstIndustryOptionsController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                identify: UnauthenticatedIdentifierAction,
                                                getData: DataRetrievalAction,
                                                requireData: DataRequiredAction,
                                                formProvider: FirstIndustryOptionsFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: FirstIndustryOptionsView,
                                                sessionRepository: SessionRepository,
                                                @Named(NavConstant.generic) navigator: Navigator
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form: Form[FirstIndustryOptions] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(FirstIndustryOptionsPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(FirstIndustryOptionsPage, value))
            newAnswers <- if (value == FirstIndustryOptions.Retail) Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.defaultRate)) else Future.successful(updatedAnswers)
            _ <- sessionRepository.set(newAnswers)
          } yield Redirect(navigator.nextPage(FirstIndustryOptionsPage, mode)(updatedAnswers))
        }
      )
  }
}
