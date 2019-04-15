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

import base.SpecBase
import controllers.confirmation.routes._
import controllers.routes._
import models.FlatRateExpenseOptions._
import models.{FlatRateExpenseOptions, TaxYearSelection}
import models.TaxYearSelection._
import models.auditing._
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.authenticated.{RemoveFRECodePage, TaxYearSelectionPage}
import pages.{ClaimAmountAndAnyDeductions, FREResponse}
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.SubmissionService
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience with BeforeAndAfterEach {

  private val mockSubmissionService = mock[SubmissionService]
  private val mockAuditConnector = mock[AuditConnector]
  private val cyaHelperMinimumUa = new CheckYourAnswersHelper(minimumUserAnswers)
  private val cyaHelperFullUa = new CheckYourAnswersHelper(fullUserAnswers)

  private val minimumSections = Seq(AnswerSection(None, Seq(
    cyaHelperMinimumUa.industryType,
    cyaHelperMinimumUa.employerContribution,
    cyaHelperMinimumUa.expensesEmployerPaid,
    cyaHelperMinimumUa.taxYearSelection,
    cyaHelperMinimumUa.alreadyClaimingFRESameAmount,
    cyaHelperMinimumUa.removeFRECode
  ).flatten))

  private val fullSections = Seq(AnswerSection(None, Seq(
    cyaHelperFullUa.industryType,
    cyaHelperFullUa.employerContribution,
    cyaHelperFullUa.expensesEmployerPaid,
    cyaHelperFullUa.taxYearSelection,
    cyaHelperFullUa.yourAddress,
    cyaHelperFullUa.yourEmployer
  ).flatten))

  override def beforeEach(): Unit = reset(mockAuditConnector)

  "Check Your Answers Controller" when {
    "onPageLoad" must {
      "return OK and the correct view for a GET for a stopped claim" in {

        val userAnswers = minimumUserAnswers.set(FREResponse, FRENoYears).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(minimumSections, checkYourAnswersTextStopFre)(request, messages).toString

        application.stop()
      }

      "return OK and the correct view for a GET for a changed claim" in {

        val userAnswers =
          fullUserAnswers
            .set(FREResponse, FlatRateExpenseOptions.FREAllYearsAllAmountsDifferent).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fullSections, checkYourAnswersTextChangeFre)(request, messages).toString

        application.stop()
      }

      "return OK and the correct view for a GET for a new claim" in {

        val application = applicationBuilder(userAnswers = Some(fullUserAnswers)).build()

        val request = FakeRequest(GET, CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fullSections, checkYourAnswersTextNoFre)(request, messages).toString

        application.stop()
      }

      "return OK and default text to new claim with irregular data" in {
        val userAnswers =
          fullUserAnswers
            .set(FREResponse, ComplexClaim).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fullSections, checkYourAnswersTextNoFre)(request, messages).toString

        application.stop()
      }

      "redirect to session expired when no freResponse is found" in {

        val application = applicationBuilder(userAnswers = Some(minimumUserAnswers)).build()

        val request = FakeRequest(GET, CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "onSubmit" must {
      "removeFRE and redirect to ConfirmationClaimStoppedController when submission success" in {
        when(mockSubmissionService.removeFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(true))

        val userAnswers = minimumUserAnswers
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1)).success.value
          .set(RemoveFRECodePage, CurrentYear).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(POST, CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("updateFlatRateExpenseSuccess"), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual userAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual ConfirmationClaimStoppedController.onPageLoad().url
        }

        application.stop()

      }

      "submitFRE and redirect to ConfirmationCurrentYearOnlyController when submission success" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(true))

        val application = applicationBuilder(Some(fullUserAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, fullUserAnswers.data))
          ).build()

        val request = FakeRequest(POST, CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("updateFlatRateExpenseSuccess"), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual fullUserAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual ConfirmationCurrentYearOnlyController.onPageLoad().url
        }

        application.stop()

      }

      "submitFRE and redirect to ConfirmationCurrentAndPreviousYearsController when submission success" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(true))

        val userAnswers = minimumUserAnswers
          .set(ClaimAmountAndAnyDeductions, 100).success.value
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1)).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(POST, CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("updateFlatRateExpenseSuccess"), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual userAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual ConfirmationCurrentAndPreviousYearsController.onPageLoad().url
        }

        application.stop()

      }

      "submitFRE and redirect to ConfirmationPreviousYearsOnlyController when submission success" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(true))

        val userAnswers = minimumUserAnswers
          .set(ClaimAmountAndAnyDeductions, 100).success.value
          .set(TaxYearSelectionPage, Seq(CurrentYearMinus2, CurrentYearMinus3)).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(POST, CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("updateFlatRateExpenseSuccess"), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual userAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual ConfirmationPreviousYearsOnlyController.onPageLoad().url
        }

        application.stop()

      }

      "redirect to tech difficulties when submitFRE fails" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(false))

        val application = applicationBuilder(Some(fullUserAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, fullUserAnswers.data))
          ).build()

        val request = FakeRequest(POST, CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("updateFlatRateExpenseFailure"), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual fullUserAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual TechnicalDifficultiesController.onPageLoad().url
        }

        application.stop()

      }

      "redirect to tech difficulties when removeFRE fails" in {
        when(mockSubmissionService.removeFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(false))

        val userAnswers = minimumUserAnswers
          .set(FREResponse, FREAllYearsAllAmountsSameAsClaimAmount).success.value
          .set(RemoveFRECodePage, TaxYearSelection.CurrentYear).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("updateFlatRateExpenseFailure"), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual userAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual TechnicalDifficultiesController.onPageLoad().url
        }

        application.stop()

      }

      "redirect to tech difficulties when given no data" in {
        val application = applicationBuilder(Some(emptyUserAnswers))
          .build()

        val request = FakeRequest(POST, CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual TechnicalDifficultiesController.onPageLoad().url

        application.stop()

      }
    }
  }
}
