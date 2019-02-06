package pages

import models.$className$
import pages.behaviours.PageBehaviours

class $className$PageSpec extends PageBehaviours {

  "$className$Page" must {

    beRetrievable[Seq[$className$]]($className$Page)

    beSettable[Seq[$className$]]($className$Page)

    beRemovable[Seq[$className$]]($className$Page)
  }
}
