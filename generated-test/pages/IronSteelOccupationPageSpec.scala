package pages

import pages.behaviours.PageBehaviours

class IronSteelOccupationPageSpec extends PageBehaviours {

  "IronSteelOccupationPage" must {

    beRetrievable[Boolean](IronSteelOccupationPage)

    beSettable[Boolean](IronSteelOccupationPage)

    beRemovable[Boolean](IronSteelOccupationPage)
  }
}
