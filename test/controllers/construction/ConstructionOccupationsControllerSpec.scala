/*
 * Copyright 2020 HM Revenue & Customs
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
import config.ClaimAmounts
import controllers.actions.UnAuthed
import controllers.routes._
import forms.construction.ConstructionOccupationsFormProvider
import models.ConstructionOccupations.{AsphaltOrCement, BuildingMaterials, JoinerOrCarpenter, LabourerOrNavvy, NoneOfAbove, RoofingFelt, StoneMason, Tilemaker}
import models.{ConstructionOccupations, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import pages.ClaimAmount
import pages.construction.ConstructionOccupationsPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.construction.ConstructionOccupationsView

import scala.concurrent.Future

class ConstructionOccupationsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures with IntegrationPatience with PropertyChecks {

  def onwardRoute = Call("GET", "/foo")

  lazy val constructionOccupationsRoute = routes.ConstructionOccupationsController.onPageLoad(NormalMode).url

  val formProvider = new ConstructionOccupationsFormProvider()
  val form = formProvider()
  private val mockSessionRepository: SessionRepository = mock[SessionRepository]
  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  "ConstructionOccupations Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, constructionOccupationsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ConstructionOccupationsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ConstructionOccupationsPage, ConstructionOccupations.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, constructionOccupationsRoute)

      val view = application.injector.instanceOf[ConstructionOccupationsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(ConstructionOccupations.values.head), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith("Construction").toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )

          .build()


      val request =
        FakeRequest(POST, constructionOccupationsRoute)
          .withFormUrlEncodedBody(("value", ConstructionOccupations.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, constructionOccupationsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ConstructionOccupationsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, constructionOccupationsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, constructionOccupationsRoute)
          .withFormUrlEncodedBody(("value", ConstructionOccupations.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    for (occupation <- ConstructionOccupations.values) {
      val ua1 = emptyUserAnswers
      val ua2 = occupation match {
        case JoinerOrCarpenter =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.joinersCarpenters).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.JoinerOrCarpenter).success.value
        case StoneMason =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.stoneMasons).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.StoneMason).success.value
        case AsphaltOrCement =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.asphaltCement).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.AsphaltOrCement).success.value
        case RoofingFelt =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.roofingFelt).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.RoofingFelt).success.value
        case LabourerOrNavvy =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.labourerNavvy).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.LabourerOrNavvy).success.value
        case Tilemaker =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.tileMaker).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.Tilemaker).success.value
        case BuildingMaterials =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.buildingMaterials).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.BuildingMaterials).success.value
        case NoneOfAbove =>
          ua1.set(ClaimAmount, ClaimAmounts.Construction.allOther).success.value
          .set(ConstructionOccupationsPage, ConstructionOccupations.NoneOfAbove).success.value
      }

      s"save correct amount to ClaimAmount when '$occupation' is selected" in {
        val argCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        when(mockSessionRepository.set(any(), argCaptor.capture())) thenReturn Future.successful(true)

        val application = applicationBuilder(userAnswers = Some(ua1))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

        val request = FakeRequest(POST, constructionOccupationsRoute)
          .withFormUrlEncodedBody(("value", occupation.toString))

        val result = route(application, request).value

        whenReady(result) {
          _ =>
            verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), ua2)
            argCaptor.getValue.data mustBe ua2.data
        }

        application.stop()
      }
    }
  }
}
