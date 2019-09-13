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
import controllers.routes.SessionExpiredController
import javax.inject.{Inject, Named}
import models.TaxYearSelection.{CurrentYear, CurrentYearMinus1}
import models.{NormalMode, TaxYearSelection}
import navigation.Navigator
import pages.authenticated.{HowYouWillGetYourExpensesPage, TaxYearSelectionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.{HowYouWillGetYourExpensesCurrentAndPreviousYearView, HowYouWillGetYourExpensesCurrentView, HowYouWillGetYourExpensesPreviousView}

import scala.concurrent.ExecutionContext

class HowYouWillGetYourExpensesController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     identify: AuthenticatedIdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     @Named(NavConstant.authenticated) navigator: Navigator,
                                                     currentView: HowYouWillGetYourExpensesCurrentView,
                                                     previousView: HowYouWillGetYourExpensesPreviousView,
                                                     currentAndPreviousYearView: HowYouWillGetYourExpensesCurrentAndPreviousYearView
                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val redirectUrl = navigator.nextPage(HowYouWillGetYourExpensesPage, NormalMode)(request.userAnswers).url
      val taxYearSelection: Option[Seq[TaxYearSelection]] = request.userAnswers.get(TaxYearSelectionPage)

      taxYearSelection match {
        case Some(taxYearSelection) if taxYearSelection.contains(CurrentYear) && taxYearSelection.length > 1 =>
          Ok(currentAndPreviousYearView(redirectUrl, containsCurrentYearMinus1(taxYearSelection)))
        case Some(taxYearSelection) if taxYearSelection.contains(CurrentYear) =>
          Ok(currentView(redirectUrl))
        case Some(taxYearSelection) =>
          Ok(previousView(redirectUrl, containsCurrentYearMinus1(taxYearSelection)))
      }
  }

  private def containsCurrentYearMinus1(taxYearSelections: Seq[TaxYearSelection]): Boolean = {
    taxYearSelections.contains(CurrentYearMinus1)

  }
}
