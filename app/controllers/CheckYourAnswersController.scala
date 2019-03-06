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
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import javax.inject.Named
import models.FlatRateExpenseOptions.FRENoYears
import models.auditing.AuditData
import models.auditing.AuditEventType._
import navigation.Navigator
import pages.authenticated.{RemoveFRECodePage, TaxYearSelectionPage}
import pages.{ClaimAmountAndAnyDeductions, FREResponse}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import service.SubmissionService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
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

      val sections = Seq(AnswerSection(None, Seq(
        cyaHelper.industryType,
        cyaHelper.employerContribution,
        cyaHelper.expensesEmployerPaid,
        cyaHelper.taxYearSelection,
        cyaHelper.yourAddress,
        cyaHelper.yourEmployer
      ).flatten))

      Ok(view(sections))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val dataToAudit: AuditData =
        AuditData(nino = request.nino.get, userAnswers = request.userAnswers.data)

      (
        request.userAnswers.get(FREResponse),
        request.userAnswers.get(TaxYearSelectionPage),
        request.userAnswers.get(ClaimAmountAndAnyDeductions),
        request.userAnswers.get(RemoveFRECodePage)
      ) match {
        case (Some(FRENoYears), Some(taxYears), Some(claimAmount), None) =>
          submissionService.submitFRENotInCode(request.nino.get, taxYears, claimAmount).map(
            result =>
              auditAndRedirect(result, dataToAudit)
          )
        case (Some(_), Some(taxYears), Some(_), Some(removeYear)) =>
          submissionService.submitRemoveFREFromCode(request.nino.get, taxYears, removeYear).map(
            result =>
              auditAndRedirect(result, dataToAudit)
          )
        case _ =>
          Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad()))
      }
  }

  def auditAndRedirect(result: Boolean, auditData: AuditData)
                      (implicit hc: HeaderCarrier): Result = {
    if (result) {
      auditConnector.sendExplicitAudit(UpdateFlatRateExpenseSuccess.toString, auditData)
      Redirect(routes.CheckYourAnswersController.onPageLoad())
    } else {
      auditConnector.sendExplicitAudit(UpdateFlatRateExpenseFailure.toString, auditData)
      Redirect(routes.TechnicalDifficultiesController.onPageLoad())
    }
  }
}
