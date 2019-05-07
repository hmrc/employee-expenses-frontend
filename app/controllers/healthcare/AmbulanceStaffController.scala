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

package controllers.healthcare

import config.{ClaimAmounts, NavConstant}
import controllers.actions._
import forms.healthcare.AmbulanceStaffFormProvider
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.ClaimAmount
import pages.healthcare.AmbulanceStaffPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import repositories.SessionRepository
import views.html.healthcare.AmbulanceStaffView

import scala.concurrent.{ExecutionContext, Future}

class AmbulanceStaffController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          @Named(NavConstant.healthcare) navigator: Navigator,
                                          identify: UnauthenticatedIdentifierAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          formProvider: AmbulanceStaffFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: AmbulanceStaffView,
                                          sessionRepository: SessionRepository
                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AmbulanceStaffPage) match {
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
          val claimAmount = if(value){ClaimAmounts.Healthcare.ambulanceStaff} else {ClaimAmounts.Healthcare.allOther}
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AmbulanceStaffPage, value)
              .flatMap(_.set(ClaimAmount, claimAmount))
            )
            _ <- sessionRepository.set(request.identifier, updatedAnswers)
          } yield Redirect(navigator.nextPage(AmbulanceStaffPage, mode)(updatedAnswers))
        }
      )
  }
}
