/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.Inject
import config.NavConstant
import controllers.actions._
import controllers.routes._
import javax.inject.Named
import models.FlatRateExpenseOptions._
import models.{CheckYourAnswersText, FlatRateExpenseOptions, NormalMode}
import navigation.Navigator
import pages.FREResponse
import pages.authenticated._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import service.SubmissionService
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.authenticated.CheckYourAnswersView

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: AuthenticatedIdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            @Named(NavConstant.authenticated) navigator: Navigator,
                                            submissionService: SubmissionService,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            auditConnector: AuditConnector
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val cyaHelper = new CheckYourAnswersHelper(request.userAnswers)
      val removeFre: Boolean = request.userAnswers.get(RemoveFRECodePage).isDefined

      request.userAnswers.get(FREResponse) match {
        case Some(freResponse) =>
          val sections = Seq(AnswerSection(None, Seq(
            cyaHelper.industryType,
            cyaHelper.employerContribution,
            cyaHelper.expensesEmployerPaid,
            cyaHelper.taxYearSelection,
            cyaHelper.alreadyClaimingFRESameAmount,
            cyaHelper.alreadyClaimingFREDifferentAmounts,
            cyaHelper.changeWhichTaxYears,
            cyaHelper.removeFRECode
          ).flatten))

          Ok(view(sections, checkYourAnswersText(removeFre, freResponse)))
        case _ =>
          Redirect(SessionExpiredController.onPageLoad())
      }
  }

  private def checkYourAnswersText(removeFre: Boolean, freResponse: FlatRateExpenseOptions): CheckYourAnswersText = {
    (removeFre, freResponse) match {
      case (true, _) =>
        CheckYourAnswersText(heading = "heading", disclaimerHeading = "stopClaim", disclaimer = "confirmInformationChangeFre", button = "acceptStopClaim")
      case (false, FRENoYears) =>
        CheckYourAnswersText(heading = "title", disclaimerHeading = "claimExpenses", disclaimer = "confirmInformationNoFre", button = "acceptClaimExpenses")
      case (false, FREAllYearsAllAmountsSameAsClaimAmount | FRESomeYears) =>
        CheckYourAnswersText(heading = "title", disclaimerHeading = "changeClaim", disclaimer = "confirmInformationChangeFre", button = "acceptChangeClaim")
      case _ =>
        CheckYourAnswersText(heading = "title", disclaimerHeading = "claimExpenses", disclaimer = "confirmInformationNoFre", button = "acceptClaimExpenses")
    }
  }

  def acceptAndClaim(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      Redirect(navigator.nextPage(CheckYourAnswersPage, NormalMode)(request.userAnswers))
  }
}
