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
import config.ClaimAmounts
import forms.FirstIndustryOptionsFormProvider
import generators.Generators
import models.{FirstIndustryOptions, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.prop.PropertyChecks
import pages.{ClaimAmount, FirstIndustryOptionsPage}
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FirstIndustryOptionsView


class FirstIndustryOptionsControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with PropertyChecks with Generators with OptionValues {

  def onwardRoute = Call("GET", "/FOO")

  lazy val firstIndustryOptionsRoute = controllers.routes.FirstIndustryOptionsController.onPageLoad(NormalMode).url
  val formProvider = new FirstIndustryOptionsFormProvider()
  val form = formProvider()

  "FirstIndustryOptionsController" must {

    "return Ok and give us the correct view" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(GET, firstIndustryOptionsRoute)
      val result = route(application, request).value
      val view: FirstIndustryOptionsView = application.injector.instanceOf[FirstIndustryOptionsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(FirstIndustryOptionsPage, FirstIndustryOptions.values.head).success.value
      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()
      val request = FakeRequest(GET, firstIndustryOptionsRoute)
      val result = route(application, request).value
      val view: FirstIndustryOptionsView = application.injector.instanceOf[FirstIndustryOptionsView]


      status(result) mustEqual OK
      contentAsString(result) mustEqual
        view(form.fill(FirstIndustryOptions.values.head), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to next page when valid data is submitted" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[Navigator].qualifiedWith("Generic").toInstance(new FakeNavigator(onwardRoute)))
        .build()

      val firstIndustryOptions: Gen[FirstIndustryOptions] = Gen.oneOf(FirstIndustryOptions.values)

      forAll(firstIndustryOptions) {
        firstIndustryOption =>

          val request = FakeRequest(POST, firstIndustryOptionsRoute)
            .withFormUrlEncodedBody(("value", firstIndustryOption.toString))
          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url
      }

      application.stop()
    }

    "return a bad request and errors when bad data is submitted" in {

      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      val request = FakeRequest(POST, firstIndustryOptionsRoute)
        .withFormUrlEncodedBody(("value", "invalidData"))
      val boundForm = form.bind(Map("value" -> "invalidData"))
      val view: FirstIndustryOptionsView = application.injector.instanceOf[FirstIndustryOptionsView]
      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to session expired for a Get if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None)
        .build()
      val request = FakeRequest(GET, firstIndustryOptionsRoute)
        .withFormUrlEncodedBody(("value", FirstIndustryOptions.options.head.value))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to session expired for a Post if no existing data is found" in {

      val application: Application = applicationBuilder(userAnswers = None)
        .build()
      val request = FakeRequest(POST, firstIndustryOptionsRoute)
        .withFormUrlEncodedBody(("value", FirstIndustryOptions.options.head.value))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  "save ClaimAmount when 'Retail' is selected" in {

    val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .build()

    val sessionRepository = application.injector.instanceOf[SessionRepository]

    val request = FakeRequest(POST, firstIndustryOptionsRoute).withFormUrlEncodedBody(("value", FirstIndustryOptions.Retail.toString))

    route(application, request).value.futureValue

    whenReady(sessionRepository.get(userAnswersId)) {
      _.value.get(ClaimAmount).value mustBe ClaimAmounts.defaultRate
    }

    application.stop()
  }
}
