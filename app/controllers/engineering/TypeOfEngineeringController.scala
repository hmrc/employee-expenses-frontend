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

package controllers.engineering

import config.ClaimAmountsConfig
import controllers.actions._
import forms.engineering.TypeOfEngineeringFormProvider
import javax.inject.{Inject, Named}
import models.{Enumerable, Mode, TypeOfEngineering}
import navigation.Navigator
import pages.ClaimAmount
import pages.engineering.TypeOfEngineeringPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.engineering.TypeOfEngineeringView

import scala.concurrent.{ExecutionContext, Future}

class TypeOfEngineeringController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       @Named("Engineering") navigator: Navigator,
                                       identify: UnauthenticatedIdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: TypeOfEngineeringFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: TypeOfEngineeringView,
                                       claimAmounts: ClaimAmountsConfig
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TypeOfEngineeringPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TypeOfEngineeringPage, value))
            _              <- value match {
                                case TypeOfEngineering.NoneOfTheAbove => Future.fromTry(updatedAnswers.set(ClaimAmount, claimAmounts.defaultRate))
                                case _ => Future.successful(updatedAnswers)
                               }
          } yield Redirect(navigator.nextPage(TypeOfEngineeringPage, mode)(updatedAnswers))
        }
      )
  }
}
