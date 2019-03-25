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
import controllers.actions._
import forms.authenticated.AlreadyClaimingFRESameAmountFormProvider
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.authenticated.AlreadyClaimingFRESameAmountPage
import pages.{ClaimAmount, ClaimAmountAndAnyDeductions, FREAmounts}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.authenticated.AlreadyClaimingFRESameAmountView

import scala.concurrent.{ExecutionContext, Future}

class AlreadyClaimingFRESameAmountController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        sessionRepository: SessionRepository,
                                                        @Named(NavConstant.authenticated) navigator: Navigator,
                                                        identify: AuthenticatedIdentifierAction,
                                                        getData: DataRetrievalAction,
                                                        requireData: DataRequiredAction,
                                                        formProvider: AlreadyClaimingFRESameAmountFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: AlreadyClaimingFRESameAmountView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AlreadyClaimingFRESameAmountPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      (request.userAnswers.get(ClaimAmountAndAnyDeductions), request.userAnswers.get(FREAmounts)) match {
        case (Some(claimAmount), Some(freAmounts)) =>
          Ok(view(preparedForm, mode, claimAmount, freAmounts))
        case _ =>
          Redirect(controllers.routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      (request.userAnswers.get(ClaimAmountAndAnyDeductions), request.userAnswers.get(FREAmounts)) match {
        case (Some(claimAmount), Some(freAmounts)) =>
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, claimAmount, freAmounts))),
            value => {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AlreadyClaimingFRESameAmountPage, value))
                _              <- sessionRepository.set(request.identifier, updatedAnswers)
              } yield Redirect(navigator.nextPage(AlreadyClaimingFRESameAmountPage, mode)(updatedAnswers))
            }
          )
        case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }
}
