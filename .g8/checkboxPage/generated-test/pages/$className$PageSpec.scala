package pages

import models.$className$
import pages.behaviours.PageBehaviours

class $className$Spec extends PageBehaviours {

  "$className$Page" must {

    beRetrievable[Set[$className$]]($className$Page)

    beSettable[Set[$className$]]($className$Page)

    beRemovable[Set[$className$]]($className$Page)
  }
}
