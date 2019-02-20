package controllers

import base.SpecBase
import forms.ChangeWhichTaxYearsFormProvider
import models.{ChangeWhichTaxYears, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import pages.authenticated.ChangeWhichTaxYearsPage
import play.api.inject.bind
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ChangeWhichTaxYearsView

class ChangeWhichTaxYearsControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val changeWhichTaxYearsRoute = routes.ChangeWhichTaxYearsController.onPageLoad(NormalMode).url

  val formProvider = new ChangeWhichTaxYearsFormProvider()
  val form = formProvider()

  "ChangeWhichTaxYears Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, changeWhichTaxYearsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ChangeWhichTaxYearsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ChangeWhichTaxYearsPage, ChangeWhichTaxYears.values).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, changeWhichTaxYearsRoute)

      val view = application.injector.instanceOf[ChangeWhichTaxYearsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(ChangeWhichTaxYears.values), NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith("Generic").toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request =
        FakeRequest(POST, changeWhichTaxYearsRoute)
          .withFormUrlEncodedBody(("value[0]", ChangeWhichTaxYears.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, changeWhichTaxYearsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ChangeWhichTaxYearsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, changeWhichTaxYearsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, changeWhichTaxYearsRoute)
          .withFormUrlEncodedBody(("value", ChangeWhichTaxYears.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
