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

package controllers.transport

import config.{ClaimAmounts, NavConstant}
import controllers.actions._
import forms.transport.AirlineJobListFormProvider
import models.Mode

import javax.inject.{Inject, Named}
import navigation.Navigator
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.UseIformFreOnlyView

import scala.concurrent.{ExecutionContext, Future}

class UsePrintAndPostController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           @Named(NavConstant.transport) navigator: Navigator,
                                           identify: UnauthenticatedIdentifierAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           val controllerComponents: MessagesControllerComponents,
                                           freOnlyPrintAndPostView:UseIformFreOnlyView,
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      Ok(freOnlyPrintAndPostView())
  }
}
