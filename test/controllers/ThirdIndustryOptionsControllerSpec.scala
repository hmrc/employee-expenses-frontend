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
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import forms.ThirdIndustryOptionsFormProvider
import generators.Generators
import models.ThirdIndustryOptions._
import models.{NormalMode, ThirdIndustryOptions, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import pages.{ClaimAmount, ThirdIndustryOptionsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ThirdIndustryOptionsView

import scala.concurrent.Future

class ThirdIndustryOptionsControllerSpec extends SpecBase
  with ScalaFutures with MockitoSugar with IntegrationPatience with PropertyChecks with Generators with OptionValues {

  private val userAnswers = emptyUserAnswers
  private val mockSessionRepository = mock[SessionRepository]

  def onwardRoute = Call("GET", "/foo")

  lazy val thirdIndustryOptionsRoute = routes.ThirdIndustryOptionsController.onPageLoad(NormalMode).url

  val formProvider = new ThirdIndustryOptionsFormProvider()
  val form = formProvider()

  "ThirdIndustryOptions Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, thirdIndustryOptionsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ThirdIndustryOptionsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ThirdIndustryOptionsPage, ThirdIndustryOptions.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, thirdIndustryOptionsRoute)

      val view = application.injector.instanceOf[ThirdIndustryOptionsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(ThirdIndustryOptions.values.head), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.generic).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val thirdIndustryOptions: Gen[ThirdIndustryOptions] = Gen.oneOf(ThirdIndustryOptions.values)

      forAll(thirdIndustryOptions) {
        thirdIndustryOption =>

          val request =
            FakeRequest(POST, thirdIndustryOptionsRoute)
              .withFormUrlEncodedBody(("value", thirdIndustryOption.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url
      }

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, thirdIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ThirdIndustryOptionsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, thirdIndustryOptionsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, thirdIndustryOptionsRoute)
          .withFormUrlEncodedBody(("value", ThirdIndustryOptions.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }


    for (choice <- ThirdIndustryOptions.values) {
      val userAnswers2 = choice match {
        case BanksBuildingSocieties => userAnswers
          .set(ThirdIndustryOptionsPage, choice).success.value
          .set(ClaimAmount, ClaimAmounts.defaultRate).success.value
        case Education  => userAnswers
          .set(ThirdIndustryOptionsPage, choice).success.value
          .set(ClaimAmount, ClaimAmounts.defaultRate).success.value
        case NoneOfAbove => userAnswers
          .set(ThirdIndustryOptionsPage, choice).success.value
          .set(ClaimAmount, ClaimAmounts.defaultRate).success.value
        case _ => userAnswers
          .set(ThirdIndustryOptionsPage, choice).success.value
      }

      s"save correct data when '$choice' is selected" in {
        when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

        val request = FakeRequest(POST, thirdIndustryOptionsRoute).withFormUrlEncodedBody(("value", choice.toString))

        val result = route(application, request).value

        whenReady(result) {
          _ =>
            verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
        }

        application.stop()
      }
    }
  }
}
