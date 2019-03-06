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

import controllers.actions._
import forms.authenticated.AlreadyClaimingFREDifferentAmountsFormProvider
import javax.inject.{Inject, Named}
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.authenticated.AlreadyClaimingFREDifferentAmountsPage
import pages.{ClaimAmountAndAnyDeductions, FREAmounts}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.authenticated.AlreadyClaimingFREDifferentAmountsView

import scala.concurrent.{ExecutionContext, Future}

class AlreadyClaimingFREDifferentAmountsController @Inject()(
                                                              override val messagesApi: MessagesApi,
                                                              sessionRepository: SessionRepository,
                                                              @Named("Authenticated") navigator: Navigator,
                                                              identify: IdentifierAction,
                                                              getData: DataRetrievalAction,
                                                              requireData: DataRequiredAction,
                                                              formProvider: AlreadyClaimingFREDifferentAmountsFormProvider,
                                                              val controllerComponents: MessagesControllerComponents,
                                                              view: AlreadyClaimingFREDifferentAmountsView
                                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AlreadyClaimingFREDifferentAmountsPage) match {
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
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AlreadyClaimingFREDifferentAmountsPage, value))
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AlreadyClaimingFREDifferentAmountsPage, mode)(updatedAnswers))
            }
          )

        case _ =>
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }
}
