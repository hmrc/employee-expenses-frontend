package pages

import models.ChangeWhichTaxYears
import pages.authenticated.ChangeWhichTaxYearsPage
import pages.behaviours.PageBehaviours

class ChangeWhichTaxYearsPageSpec extends PageBehaviours {

  "ChangeWhichTaxYearsPage" must {

    beRetrievable[Seq[ChangeWhichTaxYears]](ChangeWhichTaxYearsPage)

    beSettable[Seq[ChangeWhichTaxYears]](ChangeWhichTaxYearsPage)

    beRemovable[Seq[ChangeWhichTaxYears]](ChangeWhichTaxYearsPage)
  }
}
