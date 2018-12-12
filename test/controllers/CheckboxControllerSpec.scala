/*
 * Copyright 2018 HM Revenue & Customs
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
import forms.CheckboxFormProvider
import models.{NormalMode, Checkbox, UserData}
import navigation.{FakeNavigator, Navigator}
import pages.CheckboxPage
import play.api.inject.bind
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.CheckboxView

class CheckboxControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val checkboxRoute = routes.CheckboxController.onPageLoad(NormalMode).url

  val formProvider = new CheckboxFormProvider()
  val form = formProvider()

  "Checkbox Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userData = Some(emptyUserData)).build()

      val request = FakeRequest(GET, checkboxRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckboxView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userData = UserData(userDataId, Json.obj(CheckboxPage.toString -> JsString(Checkbox.values.head.toString)))

      val application = applicationBuilder(userData = Some(userData)).build()

      val request = FakeRequest(GET, checkboxRoute)

      val view = application.injector.instanceOf[CheckboxView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(Checkbox.values.head), NormalMode)(fakeRequest, messages).toString
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userData = Some(emptyUserData))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, checkboxRoute)
          .withFormUrlEncodedBody(("value", Checkbox.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userData = Some(emptyUserData)).build()

      val request =
        FakeRequest(POST, checkboxRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[CheckboxView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userData = None).build()

      val request = FakeRequest(GET, checkboxRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userData = None).build()

      val request =
        FakeRequest(POST, checkboxRoute)
          .withFormUrlEncodedBody(("value", Checkbox.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
