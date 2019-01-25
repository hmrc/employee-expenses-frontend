package pages

import pages.behaviours.PageBehaviours
import pages.manufacturing.IronSteelOccupationPage

class IronSteelOccupationPageSpec extends PageBehaviours {

  "IronSteelOccupationPage" must {

    beRetrievable[Boolean](IronSteelOccupationPage)

    beSettable[Boolean](IronSteelOccupationPage)

    beRemovable[Boolean](IronSteelOccupationPage)
  }
}
