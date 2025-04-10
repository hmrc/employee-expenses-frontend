/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.ThirdIndustryOptionsFormProvider
import javax.inject.{Inject, Named}
import models.ThirdIndustryOptions.{BanksBuildingSocieties, Leisure, Prisons}
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.{ClaimAmount, ThirdIndustryOptionsPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ThirdIndustryOptionsView

import scala.concurrent.{ExecutionContext, Future}

class ThirdIndustryOptionsController @Inject() (
    override val messagesApi: MessagesApi,
    @Named(NavConstant.generic) navigator: Navigator,
    identify: UnauthenticatedIdentifierAction,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    formProvider: ThirdIndustryOptionsFormProvider,
    val controllerComponents: MessagesControllerComponents,
    view: ThirdIndustryOptionsView,
    sessionRepository: SessionRepository
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = identify.andThen(getData).andThen(requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(ThirdIndustryOptionsPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] =
    identify.andThen(getData).andThen(requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <-
                value match {
                  case Leisure =>
                    Future.fromTry(
                      request.userAnswers
                        .set(ThirdIndustryOptionsPage, value)
                        .flatMap(_.set(ClaimAmount, ClaimAmounts.leisure))
                    )
                  case Prisons =>
                    Future.fromTry(
                      request.userAnswers
                        .set(ThirdIndustryOptionsPage, value)
                        .flatMap(_.set(ClaimAmount, ClaimAmounts.prisons))
                    )
                  case BanksBuildingSocieties =>
                    Future.fromTry(
                      request.userAnswers
                        .set(ThirdIndustryOptionsPage, BanksBuildingSocieties)
                        .flatMap(_.set(ClaimAmount, ClaimAmounts.defaultRate))
                    )
                  case _ => Future.fromTry(request.userAnswers.set(ThirdIndustryOptionsPage, value))
                }
              _ <- sessionRepository.set(request.identifier, updatedAnswers)
            } yield Redirect(navigator.nextPage(ThirdIndustryOptionsPage, mode)(updatedAnswers))
        )
    }

}
