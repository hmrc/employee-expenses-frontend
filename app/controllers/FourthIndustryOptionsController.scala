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

import config.{ClaimAmounts, NavConstant}
import controllers.actions._
import forms.FourthIndustryOptionsFormProvider
import javax.inject.{Inject, Named}
import models.FourthIndustryOptions._
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.{ClaimAmount, FourthIndustryOptionsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.FourthIndustryOptionsView

import scala.concurrent.{ExecutionContext, Future}

class FourthIndustryOptionsController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 sessionRepository: SessionRepository,
                                                 @Named(NavConstant.generic) navigator: Navigator,
                                                 identify: UnauthenticatedIdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 formProvider: FourthIndustryOptionsFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: FourthIndustryOptionsView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(FourthIndustryOptionsPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(FourthIndustryOptionsPage, value))
            newAnswers <- value match {
              case Agriculture => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.agriculture))
              case FireService => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.fireService))
              case Leisure => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.leisure))
              case Prisons => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.prisons))
              case _ => Future.fromTry(updatedAnswers.set(ClaimAmount, ClaimAmounts.defaultRate))
            }
            _ <- sessionRepository.set(newAnswers)
          } yield Redirect(navigator.nextPage(FourthIndustryOptionsPage, mode)(newAnswers))
        }
      )
  }
}
