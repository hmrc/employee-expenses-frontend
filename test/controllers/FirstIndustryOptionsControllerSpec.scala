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
import config.ClaimAmountsConfig
import controllers.actions.{DataRequiredAction, DataRetrievalAction, UnauthenticatedIdentifierAction}
import forms.FirstIndustryOptionsFormProvider
import javax.inject.Named
import models.{FirstIndustryOptions, NormalMode, UserAnswers}
import navigation.{FakeNavigator, GenericNavigator, Navigator}
import org.scalatest.concurrent.ScalaFutures
import pages.FirstIndustryOptionsPage
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.inject.bind
import play.api.mvc.{Call, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FirstIndustryOptionsView

import scala.concurrent.ExecutionContext


class FirstIndustryOptionsControllerSpec extends SpecBase with ScalaFutures {

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
      val request = FakeRequest(POST, firstIndustryOptionsRoute)
        .withFormUrlEncodedBody(("value", FirstIndustryOptions.options.head.value))
      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "save claim amount when 'Retail' is selected" in {
      val application: Application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[Navigator].qualifiedWith("Generic").toInstance(new FakeNavigator(onwardRoute)))
        .build()

      val injector = application.injector

      val controller =
        new FirstIndustryOptionsController(
          messagesApi = injector.instanceOf[MessagesApi],
          identify = injector.instanceOf[UnauthenticatedIdentifierAction],
          getData = injector.instanceOf[DataRetrievalAction],
          requireData = injector.instanceOf[DataRequiredAction],
          formProvider = injector.instanceOf[FirstIndustryOptionsFormProvider],
          controllerComponents = injector.instanceOf[MessagesControllerComponents],
          view = injector.instanceOf[FirstIndustryOptionsView],
          sessionRepository = injector.instanceOf[SessionRepository],
          navigator = injector.instanceOf[GenericNavigator],
          claimAmounts = injector.instanceOf[ClaimAmountsConfig]
        )(ec = injector.instanceOf[ExecutionContext])

      val request = FakeRequest(POST, firstIndustryOptionsRoute)
        .withSession(frontendAppConfig.mongoKey -> "key")
        .withFormUrlEncodedBody(("value", FirstIndustryOptions.Retail.toString))

      val result = controller.onSubmit(NormalMode)(request)

      status(result) mustEqual SEE_OTHER

      whenReady(result) {
        res =>
          println(s"#####\n\n${res.session(request).data}\n\n#####")
      }

      redirectLocation(result).value mustEqual onwardRoute.url

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

}
