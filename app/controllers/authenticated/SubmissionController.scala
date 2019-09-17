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
import controllers.authenticated.routes.{HowYouWillGetYourExpensesController, YourEmployerController}
import controllers.routes.{PhoneUsController, SessionExpiredController, TechnicalDifficultiesController}
import javax.inject.{Inject, Named}
import models.TaxYearSelection.CurrentYear
import models.auditing.AuditData
import models.auditing.AuditEventType.{UpdateFlatRateExpenseFailure, UpdateFlatRateExpenseSuccess}
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import pages.ClaimAmountAndAnyDeductions
import pages.authenticated.{ChangeWhichTaxYearsPage, CheckYourAnswersPage, RemoveFRECodePage, TaxYearSelectionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import service.SubmissionService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class SubmissionController @Inject()(override val messagesApi: MessagesApi,
                                     identify: AuthenticatedIdentifierAction,
                                     getData: DataRetrievalAction,
                                     requireData: DataRequiredAction,
                                     submissionService: SubmissionService,
                                     auditConnector: AuditConnector,
                                     val controllerComponents: MessagesControllerComponents,
                                     @Named(NavConstant.authenticated) navigator: Navigator
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val dataToAudit: AuditData =
        AuditData(nino = request.nino.get, userAnswers = request.userAnswers.data)

      (
        request.userAnswers.get(TaxYearSelectionPage),
        request.userAnswers.get(ClaimAmountAndAnyDeductions),
        request.userAnswers.get(RemoveFRECodePage),
        request.userAnswers.get(ChangeWhichTaxYearsPage)
      ) match {
        case (Some(taxYears), Some(_), Some(removeYear), None) =>
          submissionService.removeFRE(request.nino.get, taxYears, removeYear).map(
            result =>
              auditAndRedirect(result, dataToAudit, request.userAnswers)
          )
        case (Some(taxYearsSelection), Some(claimAmountAndAnyDeductions), None, changeYears) =>
          val taxYears = changeYears match {
            case Some(changeYears) => changeYears
            case _ => taxYearsSelection
          }
          submissionService.submitFRE(request.nino.get, taxYears, claimAmountAndAnyDeductions).map(
            result => {
              auditAndRedirect(result, dataToAudit, request.userAnswers)
            }
          )
        case _ =>
          Future.successful(Redirect(SessionExpiredController.onPageLoad()))
      }
  }

  private def auditAndRedirect(result: Seq[HttpResponse],
                               auditData: AuditData,
                               userAnswers: UserAnswers
                              )(implicit hc: HeaderCarrier): Result = {

    if (result.nonEmpty && result.forall(_.status == 204)) {
      auditConnector.sendExplicitAudit(UpdateFlatRateExpenseSuccess.toString, auditData)

      userAnswers.get(TaxYearSelectionPage) match {
        case None => ???
        case Some(selectedYears) if selectedYears.contains(CurrentYear) => ???

      }


//      Redirect(navigator.nextPage(CheckYourAnswersPage, NormalMode)(userAnswers))
    } else if (result.nonEmpty && result.exists(_.status == 423)) {
      Redirect(PhoneUsController.onPageLoad())
    } else {
      auditConnector.sendExplicitAudit(UpdateFlatRateExpenseFailure.toString, auditData)
      Redirect(TechnicalDifficultiesController.onPageLoad())
    }
  }

}
