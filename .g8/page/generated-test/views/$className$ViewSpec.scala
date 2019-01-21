package views

import views.behaviours.ViewBehaviours
import views.html.$className$View

class $className$ViewSpec extends ViewBehaviours {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  "$className$ view" must {

    val view = application.injector.instanceOf[$className$View]

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "$className;format="decap"$")

    behave like pageWithBackLink(applyView)
  }

  application.stop()
}
