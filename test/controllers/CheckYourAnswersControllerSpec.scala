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
import org.scalatest.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.SubmissionService
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView
import org.mockito.Mockito._
import org.mockito.Matchers._
import play.api.inject.bind

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with MockitoSugar {

  private val mockSubmissionService = mock[SubmissionService]
  private val cyaHelper = new CheckYourAnswersHelper(minimumUserAnswers)
  private val minimumSections = Seq(AnswerSection(None, Seq(
    cyaHelper.industryType,
    cyaHelper.employerContribution,
    cyaHelper.expensesEmployerPaid,
    cyaHelper.taxYearSelection,
    cyaHelper.yourAddress
  ).flatten))

  "Check Your Answers Controller" when {
    "onPageLoad" must {
      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(minimumUserAnswers)).build()

        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(minimumSections)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "onSubmit" must {
      "for submitFRENotInCode redirect to CYA when submission success" in {
        when(mockSubmissionService.submitFRENotInCode(any(),any(),any())(any(),any()))
          .thenReturn(Future.successful(true))

        val application = applicationBuilder(Some(minimumUserAnswers))
          .overrides(bind[SubmissionService].toInstance(mockSubmissionService))
          .build()

        val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad().url

        application.stop()

      }

      "for submitRemoveFREFromCode redirect to CYA when submission success" in {
        when(mockSubmissionService.submitRemoveFREFromCode(any(),any(),any(),any())(any(),any()))
          .thenReturn(Future.successful(true))

        val application = applicationBuilder(Some(minimumUserAnswers))
          .overrides(bind[SubmissionService].toInstance(mockSubmissionService))
          .build()

        val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad().url

        application.stop()

      }

      "for submitFRENotInCode redirect to tech difficulties when submission fail" in {
        when(mockSubmissionService.submitFRENotInCode(any(),any(),any())(any(),any()))
          .thenReturn(Future.successful(false))

        val application = applicationBuilder(Some(minimumUserAnswers))
          .overrides(bind[SubmissionService].toInstance(mockSubmissionService))
          .build()

        val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TechnicalDifficultiesController.onPageLoad().url

        application.stop()

      }

      "for submitRemoveFREFromCode redirect to tech difficulties when submission fail" in {
        when(mockSubmissionService.submitRemoveFREFromCode(any(),any(),any(),any())(any(),any()))
          .thenReturn(Future.successful(false))

        val application = applicationBuilder(Some(minimumUserAnswers))
          .overrides(bind[SubmissionService].toInstance(mockSubmissionService))
          .build()

        val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TechnicalDifficultiesController.onPageLoad().url

        application.stop()

      }

      "redirect to tech difficulties when given no data" in {
        val application = applicationBuilder(Some(emptyUserAnswers))
          .build()

        val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TechnicalDifficultiesController.onPageLoad().url

        application.stop()

      }
    }
  }
}
