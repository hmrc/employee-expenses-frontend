package pages

import pages.behaviours.PageBehaviours

class MyNewPagePageSpec extends PageBehaviours {

  "MyNewPagePage" must {

    beRetrievable[Boolean](MyNewPagePage)

    beSettable[Boolean](MyNewPagePage)

    beRemovable[Boolean](MyNewPagePage)
  }
}
