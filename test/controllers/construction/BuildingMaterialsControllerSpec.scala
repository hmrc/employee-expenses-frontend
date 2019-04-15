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

package controllers.construction

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import forms.construction.BuildingMaterialsFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.ClaimAmount
import pages.construction.BuildingMaterialsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.construction.BuildingMaterialsView

import scala.concurrent.Future

class BuildingMaterialsControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience with OptionValues with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new BuildingMaterialsFormProvider()
  val form = formProvider()
  val mockSessionRepository: SessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  lazy val buildingMaterialsRoute = routes.BuildingMaterialsController.onPageLoad(NormalMode).url

  "BuildingMaterials Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, buildingMaterialsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[BuildingMaterialsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(BuildingMaterialsPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, buildingMaterialsRoute)

      val view = application.injector.instanceOf[BuildingMaterialsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.construction).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, buildingMaterialsRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, buildingMaterialsRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[BuildingMaterialsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, buildingMaterialsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, buildingMaterialsRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  "save 'buildingMaterials' to ClaimAmount when 'Yes' is selected" in {

    val ua1 = emptyUserAnswers

    val application = applicationBuilder(userAnswers = Some(ua1))
      .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
      .build()

    val request = FakeRequest(POST, buildingMaterialsRoute)
      .withFormUrlEncodedBody(("value", "true"))

    val result = route(application, request).value

    val ua2 =
      ua1
        .set(ClaimAmount, ClaimAmounts.Construction.buildingMaterials).success.value
        .set(BuildingMaterialsPage, true).success.value

    whenReady(result) {
      _ =>
        verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
    }

    application.stop()
  }

  "save 'allOther' to ClaimAmount when 'No' is selected" in {

    val ua1 = emptyUserAnswers

    val application = applicationBuilder(userAnswers = Some(ua1))
      .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
      .build()

    val request = FakeRequest(POST, buildingMaterialsRoute)
      .withFormUrlEncodedBody(("value", "false"))

    val result = route(application, request).value

    val ua2 =
      ua1
        .set(ClaimAmount, ClaimAmounts.Construction.allOther).success.value
        .set(BuildingMaterialsPage, false).success.value

    whenReady(result) {
      _ =>
        verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
    }

    application.stop()
  }
}
