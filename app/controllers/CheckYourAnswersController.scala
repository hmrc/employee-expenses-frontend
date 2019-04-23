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

package controllers

import com.google.inject.Inject
import config.NavConstant
import controllers.actions._
import controllers.routes._
import javax.inject.Named
import models.FlatRateExpenseOptions._
import models.auditing.AuditData
import models.auditing.AuditEventType._
import models.{CheckYourAnswersText, FlatRateExpenseOptions, NormalMode, UserAnswers}
import navigation.Navigator
import pages.authenticated._
import pages.{ClaimAmountAndAnyDeductions, FREResponse}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import service.SubmissionService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
            cyaHelper.removeFRECode,
            cyaHelper.yourEmployer,
            cyaHelper.yourAddress
          ).flatten))

          Ok(view(sections, checkYourAnswersText(removeFre, freResponse)))
        case _ =>
          Redirect(SessionExpiredController.onPageLoad())
      }
  }

  def checkYourAnswersText(removeFre: Boolean, freResponse: FlatRateExpenseOptions): CheckYourAnswersText = {
    (removeFre, freResponse) match {
      case (true, _) =>
        CheckYourAnswersText(heading = "heading", disclaimerHeading = "stopClaim", disclaimer = "confirmInformationChangeFre", button = "acceptStopClaim")
      case (false, FRENoYears) =>
        CheckYourAnswersText(heading = "title.claimExpenses", disclaimerHeading = "claimExpenses", disclaimer = "confirmInformationNoFre", button = "acceptClaimExpenses")
      case (false, FREAllYearsAllAmountsSameAsClaimAmount | FREAllYearsAllAmountsDifferent) =>
        CheckYourAnswersText(heading = "title.claimExpenses", disclaimerHeading = "changeClaim", disclaimer = "confirmInformationChangeFre", button = "acceptChangeClaim")
      case _ =>
        CheckYourAnswersText(heading = "title.claimExpenses", disclaimerHeading = "claimExpenses", disclaimer = "confirmInformationNoFre", button = "acceptClaimExpenses")
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val dataToAudit: AuditData =
        AuditData(nino = request.nino.get, userAnswers = request.userAnswers.data)

      (
        request.userAnswers.get(TaxYearSelectionPage),
        request.userAnswers.get(ClaimAmountAndAnyDeductions),
        request.userAnswers.get(RemoveFRECodePage)
      ) match {
        case (Some(taxYears), Some(_), Some(removeYear)) =>
          submissionService.removeFRE(request.nino.get, taxYears, removeYear).map(
            result =>
              auditAndRedirect(result, dataToAudit, request.userAnswers)
          )
        case (Some(taxYears), Some(claimAmountAndAnyDeductions), None) =>
          submissionService.submitFRE(request.nino.get, taxYears, claimAmountAndAnyDeductions).map(
            result =>
              auditAndRedirect(result, dataToAudit, request.userAnswers)
          )
        case _ =>
          Future.successful(Redirect(TechnicalDifficultiesController.onPageLoad()))
      }
  }

  def auditAndRedirect(result: Seq[HttpResponse],
                       auditData: AuditData,
                       userAnswers: UserAnswers
                      )(implicit hc: HeaderCarrier): Result = {

    if (result.nonEmpty && result.forall(_.status == 204)) {
      auditConnector.sendExplicitAudit(UpdateFlatRateExpenseSuccess.toString, auditData)
      Redirect(navigator.nextPage(CheckYourAnswersPage, NormalMode)(userAnswers))
    } else if (result.nonEmpty && result.exists(_.status == 423)) {
      Redirect(PhoneUsController.onPageLoad())
    } else {
      auditConnector.sendExplicitAudit(UpdateFlatRateExpenseFailure.toString, auditData)
      Redirect(TechnicalDifficultiesController.onPageLoad())
    }
  }
}
