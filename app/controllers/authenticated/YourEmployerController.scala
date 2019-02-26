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

import connectors.TaiConnector
import controllers.actions._
import forms.authenticated.YourEmployerFormProvider
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.authenticated.YourEmployerPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Lang, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.authenticated.YourEmployerView

import scala.concurrent.{ExecutionContext, Future}

class YourEmployerController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        @Named("Authenticated") navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: YourEmployerFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        taiConnector: TaiConnector,
                                        view: YourEmployerView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val preparedForm = request.userAnswers.get(YourEmployerPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      request.nino match {
        case Some(nino) =>
          taiConnector.taiTaxCodeRecords(nino).map {
            taxCodeRecords =>
              taxCodeRecords.filter(_.primary).head.employerName match {
                case employerName => Ok(view(preparedForm, mode, employerName))
                case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
              }
          }
        case _ =>
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      request.nino match {
        case Some(nino) =>
          taiConnector.taiTaxCodeRecords(nino).flatMap {
            taxCodeRecords =>
              taxCodeRecords.filter(_.primary).head.employerName match {
                case employerName =>

                  form.bindFromRequest().fold(
                    (formWithErrors: Form[_]) =>
                      Future.successful(BadRequest(view(formWithErrors, mode, employerName))),

                    value => {
                      for {
                        updatedAnswers <- Future.fromTry(request.userAnswers.set(YourEmployerPage, value))
                        _ <- sessionRepository.set(updatedAnswers)
                      } yield Redirect(navigator.nextPage(YourEmployerPage, mode)(updatedAnswers))
                    }
                  )
                case _ => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
              }
          }
        case _ =>
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }

  }
}
