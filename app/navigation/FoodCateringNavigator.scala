package navigation

import controllers.routes
import javax.inject.Inject
import models.UserAnswers
import pages.Page
import play.api.mvc.Call

class FoodCateringNavigator @Inject()() extends Navigator {

  protected val routeMap: PartialFunction[Page, UserAnswers => Call] = {
    case _ => _ => routes.SessionExpiredController.onPageLoad()
  }

  protected val checkRouteMap: PartialFunction[Page, UserAnswers => Call] = {
    case _ => _ => routes.SessionExpiredController.onPageLoad()
  }
}
