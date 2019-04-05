package navigation


import base.SpecBase
import models._
import pages.engineering.TypeOfEngineeringPage
import pages.shipyard._


class ShipyardNavigatorSpec extends SpecBase {
  private val modes = Seq(NormalMode, CheckMode)
  private val navigator = new ShipyardNavigator

  "ShipyardNavigator" when {

    for (mode <- modes) {
      s"in $mode" must {


        "go to ApprenticePage when Apprentice or storekeeper is selected" in {
          val answers = emptyUserAnswers.set(ApprenticeStorekeeperPage, ApprenticeStorekeeperPage).success.value

          navigator.nextPage(TypeOfEngineeringPage, mode)(answers) mustBe
            controllers.engineering.routes.ConstructionalEngineeringApprenticeController.onPageLoad(mode)

        }

      }
    }
  }