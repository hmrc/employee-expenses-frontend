/*
 * Copyright 2018 HM Revenue & Customs
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
import forms.CheckboxFormProvider
import javax.inject.Inject
import models.{Checkbox, Enumerable, Mode, UserAnswers}
import navigation.Navigator
import pages.CheckboxPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.CheckboxView

import scala.concurrent.{ExecutionContext, Future}

class CheckboxController @Inject()(override val messagesApi: MessagesApi,
                                   sessionRepository: SessionRepository,
                                   navigator: Navigator,
                                   identify: IdentifierAction,
                                   getData: DataRetrievalAction,
                                   requireData: DataRequiredAction,
                                   formProvider: CheckboxFormProvider,
                                   val controllerComponents: MessagesControllerComponents,
                                   view: CheckboxView
                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  //  val form: Form[Seq[Checkbox.Value]] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) {
    implicit request => {
      val preparedForm: Form[Seq[Checkbox.Value]] = request.userAnswers.map {
        _.get[Seq[Checkbox.Value]](CheckboxPage) match {
          case value => formProvider().fill(value)
          case _ => formProvider()
        }
      }
      Ok(view(preparedForm, mode))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      formProvider().bindFromRequest().fold(
        (formWithErrors: Form[Seq[Checkbox.Value]]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),
        value => {
          val updatedAnswers = request.userAnswers.getOrElse(UserAnswers(request.internalId)).set(CheckboxPage, value)

          sessionRepository.set(updatedAnswers.userData).map(
            _ =>
              Redirect(navigator.nextPage(CheckboxPage, mode)(updatedAnswers))
          )
        }
      )
  }
}
