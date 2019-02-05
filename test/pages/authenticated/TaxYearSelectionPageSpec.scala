package pages.authenticated

import models.TaxYearSelection
import pages.behaviours.PageBehaviours

class TaxYearSelectionPageSpec extends PageBehaviours {

  "TaxYearSelectionPage" must {

    beRetrievable[Seq[TaxYearSelection]](TaxYearSelectionPage)

    beSettable[Seq[TaxYearSelection]](TaxYearSelectionPage)

    beRemovable[Seq[TaxYearSelection]](TaxYearSelectionPage)
  }
}
