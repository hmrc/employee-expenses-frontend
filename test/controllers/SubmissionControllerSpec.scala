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
import connectors.{CitizenDetailsConnector, TaiConnector}
import controllers.authenticated.routes.SubmissionController
import controllers.confirmation.routes._
import controllers.routes.{PhoneUsController, SessionExpiredController, TechnicalDifficultiesController}
import models.FlatRateExpenseOptions.{FREAllYearsAllAmountsSameAsClaimAmount, _}
import models.TaxYearSelection.{CurrentYear, CurrentYearMinus1, CurrentYearMinus2, CurrentYearMinus3, CurrentYearMinus4, _}
import models.auditing.AuditData
import models.auditing.AuditEventType.{UpdateFlatRateExpenseFailure, UpdateFlatRateExpenseSuccess}
import models.{AlreadyClaimingFREDifferentAmounts, FlatRateExpense, FlatRateExpenseAmounts, TaiTaxYear, TaxYearSelection}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import pages.authenticated.{AlreadyClaimingFREDifferentAmountsPage, ChangeWhichTaxYearsPage, RemoveFRECodePage, TaxYearSelectionPage}
import pages.{ClaimAmountAndAnyDeductions, FREAmounts, FREResponse}
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, redirectLocation, route, status, _}
import service.SubmissionService
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.Future

class SubmissionControllerSpec extends SpecBase with PropertyChecks with MockitoSugar with BeforeAndAfterEach
  with ScalaFutures with IntegrationPatience {

  private val mockSubmissionService = mock[SubmissionService]
  private val mockAuditConnector = mock[AuditConnector]
  private val mockTaiConnector = mock[TaiConnector]
  private val mockCitizenDetailsConnector = mock[CitizenDetailsConnector]

  override def beforeEach(): Unit = {
    reset(mockAuditConnector)
    reset(mockSubmissionService)
    reset(mockTaiConnector)
    reset(mockCitizenDetailsConnector)
  }

  private val onwardRoute = Call("GET", "/foo")

  "on submit" must {

    "remove" when {

      "removeFRE and redirect to ConfirmationClaimStoppedController when submission success" in {
        when(mockSubmissionService.removeFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(Seq(HttpResponse(204))))

        val userAnswers = minimumUserAnswers
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1)).success.value
          .set(RemoveFRECodePage, CurrentYear).success.value

        val application = applicationBuilder(Some(userAnswers), Some(onwardRoute))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseSuccess.toString), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual userAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual onwardRoute.url
        }

        application.stop()

      }

      "redirect to tech difficulties when removeFRE fails" in {
        when(mockSubmissionService.removeFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(Seq(HttpResponse(500))))

        val userAnswers = minimumUserAnswers
          .set(FREResponse, FREAllYearsAllAmountsSameAsClaimAmount).success.value
          .set(RemoveFRECodePage, TaxYearSelection.CurrentYear).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseFailure.toString), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual userAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual TechnicalDifficultiesController.onPageLoad().url
        }

        application.stop()

      }

      "not removeFRE and not audit and redirect to PhoneUsController when citizen details returns 423" in {
        when(mockSubmissionService.removeFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(Seq(HttpResponse(423))))

        val userAnswers = minimumUserAnswers
          .set(FREResponse, FREAllYearsAllAmountsSameAsClaimAmount).success.value
          .set(RemoveFRECodePage, TaxYearSelection.CurrentYear).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(0)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseFailure.toString), captor.capture())(any(), any(), any())

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual PhoneUsController.onPageLoad().url
        }

        application.stop()
      }
    }

    "submit" must {

      "submitFRE and redirect to ConfirmationCurrentYearOnlyController when submission success" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(Seq(HttpResponse(204))))

        val application = applicationBuilder(Some(currentYearFullUserAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, currentYearFullUserAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseSuccess.toString), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual currentYearFullUserAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual ConfirmationCurrentYearOnlyController.onPageLoad().url
        }

        application.stop()

      }

      "submitFRE and redirect to ConfirmationCurrentAndPreviousYearsController when submission success" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(Seq(HttpResponse(204))))

        val userAnswers = minimumUserAnswers
          .set(ClaimAmountAndAnyDeductions, 100).success.value
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1)).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseSuccess.toString), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual userAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual ConfirmationCurrentAndPreviousYearsController.onPageLoad().url
        }

        application.stop()

      }

      "not submitFRE and not audit and redirect to PhoneUsController when citizen details returns 423" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(Seq(HttpResponse(423))))

        val userAnswers = minimumUserAnswers
          .set(ClaimAmountAndAnyDeductions, 100).success.value
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1)).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(0)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseSuccess.toString), captor.capture())(any(), any(), any())

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual PhoneUsController.onPageLoad().url
        }

        application.stop()

      }

      "submitFRE and redirect to ConfirmationPreviousYearsOnlyController when submission success" in {
        when(mockSubmissionService.submitFRE(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(Seq(HttpResponse(204))))

        val userAnswers = minimumUserAnswers
          .set(ClaimAmountAndAnyDeductions, 100).success.value
          .set(TaxYearSelectionPage, Seq(CurrentYearMinus2, CurrentYearMinus3)).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, userAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseSuccess.toString), captor.capture())(any(), any(), any())

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
          .thenReturn(Future.successful(Seq(HttpResponse(500))))

        val application = applicationBuilder(Some(currentYearFullUserAnswers))
          .overrides(
            bind[SubmissionService].toInstance(mockSubmissionService),
            bind[AuditConnector].toInstance(mockAuditConnector),
            bind[AuditData].toInstance(AuditData(fakeNino, currentYearFullUserAnswers.data))
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>

            val captor = ArgumentCaptor.forClass(classOf[AuditData])

            verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo(UpdateFlatRateExpenseFailure.toString), captor.capture())(any(), any(), any())

            val auditData = captor.getValue

            auditData.nino mustEqual fakeNino
            auditData.userAnswers mustEqual currentYearFullUserAnswers.data
            auditData.userAnswers mustBe a[JsObject]

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual TechnicalDifficultiesController.onPageLoad().url
        }

        application.stop()

      }

      "submit the correct number of time for new claims" in {
        when(mockCitizenDetailsConnector.getEtag(any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200, Some(validEtagJson))))

        when(mockTaiConnector.taiFREUpdate(any(), any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(204)))

        val userAnswers = currentYearFullUserAnswers
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1, CurrentYearMinus2, CurrentYearMinus3, CurrentYearMinus4)).success.value
          .set(ClaimAmountAndAnyDeductions, 100).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector),
            bind[TaiConnector].toInstance(mockTaiConnector),
            bind[AuditConnector].toInstance(mockAuditConnector)
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)
        val result = route(application, request).value

        whenReady(result) {
          _ =>
            verify(mockTaiConnector, times(5)).taiFREUpdate(any(), any(), any(), any())(any(), any())

            verify(mockAuditConnector, times(1)).sendExplicitAudit(
              eqTo(UpdateFlatRateExpenseSuccess.toString),
              eqTo(AuditData(fakeNino, userAnswers.data))
            )(any(), any(), any())
        }

        application.stop()
      }

      "submit the correct number of time for change claims" in {
        when(mockCitizenDetailsConnector.getEtag(any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200, Some(validEtagJson))))

        when(mockTaiConnector.taiFREUpdate(any(), any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(204)))

        val userAnswers = minimumUserAnswers
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1, CurrentYearMinus2, CurrentYearMinus3, CurrentYearMinus4)).success.value
          .set(ClaimAmountAndAnyDeductions, 100).success.value
          .set(AlreadyClaimingFREDifferentAmountsPage, AlreadyClaimingFREDifferentAmounts.Change).success.value
          .set(ChangeWhichTaxYearsPage, Seq(CurrentYear, CurrentYearMinus1)).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector),
            bind[TaiConnector].toInstance(mockTaiConnector),
            bind[AuditConnector].toInstance(mockAuditConnector)
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)
        val result = route(application, request).value

        whenReady(result) {
          _ =>
            verify(mockTaiConnector, times(2)).taiFREUpdate(any(), any(), any(), any())(any(), any())

            verify(mockAuditConnector, times(1)).sendExplicitAudit(
              eqTo(UpdateFlatRateExpenseSuccess.toString),
              eqTo(AuditData(fakeNino, userAnswers.data))
            )(any(), any(), any())
        }

        application.stop()
      }

      "submit the correct number of time for remove claims" in {
        when(mockCitizenDetailsConnector.getEtag(any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(200, Some(validEtagJson))))

        when(mockTaiConnector.taiFREUpdate(any(), any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(204)))

        val userAnswers = minimumUserAnswers
          .set(TaxYearSelectionPage, Seq(CurrentYear, CurrentYearMinus1, CurrentYearMinus2, CurrentYearMinus3, CurrentYearMinus4)).success.value
          .set(RemoveFRECodePage, CurrentYearMinus2).success.value

        val application = applicationBuilder(Some(userAnswers))
          .overrides(
            bind[CitizenDetailsConnector].toInstance(mockCitizenDetailsConnector),
            bind[TaiConnector].toInstance(mockTaiConnector),
            bind[AuditConnector].toInstance(mockAuditConnector)
          ).build()

        val request = FakeRequest(GET, SubmissionController.onSubmit().url)

        val result = route(application, request).value

        whenReady(result) {
          _ =>
            verify(mockTaiConnector, times(3)).taiFREUpdate(any(), any(), any(), any())(any(), any())

            verify(mockAuditConnector, times(1)).sendExplicitAudit(
              eqTo(UpdateFlatRateExpenseSuccess.toString),
              eqTo(AuditData(fakeNino, userAnswers.data))
            )(any(), any(), any())
        }

        application.stop()
      }
    }

    "redirect to SessionExpired when data is missing" in {
      val application = applicationBuilder(Some(emptyUserAnswers))
        .build()

      val request = FakeRequest(GET, SubmissionController.onSubmit().url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
