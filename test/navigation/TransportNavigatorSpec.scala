/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import base.SpecBase
import controllers.transport.routes
import models.TypeOfTransport.{NoneOfTheAbove, _}
import models.WhichRailwayTrade._
import models._
import org.scalatestplus.mockito.MockitoSugar
import pages.Page
import pages.transport._

class TransportNavigatorSpec extends SpecBase with MockitoSugar {

  private val navigator = new TransportNavigator
  private val modes = Seq(NormalMode, CheckMode)


  "TransportNavigator" when {

    for (mode <- modes) {
      s"in $mode" must {

        "go to SessionExpired when not a transport page" in {
          navigator.nextPage(mock[Page], mode)(emptyUserAnswers) mustBe
            controllers.routes.SessionExpiredController.onPageLoad()
        }

        "on TypeOfTransportController" must {
          "go to AirlineJobList when 'Airlines' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfTransportPage, Airlines).success.value
            navigator.nextPage(TypeOfTransportPage, mode)(answers) mustBe
              routes.AirlineJobListController.onPageLoad(mode)
          }

          "go to GarageHandOrCleanerController when 'Public Transport' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfTransportPage, PublicTransport).success.value
            navigator.nextPage(TypeOfTransportPage, mode)(answers) mustBe
              routes.GarageHandOrCleanerController.onPageLoad(mode)
          }

          "go to WhichRailwayTrade when 'Railways' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfTransportPage, Railways).success.value
            navigator.nextPage(TypeOfTransportPage, mode)(answers) mustBe
              routes.WhichRailwayTradeController.onPageLoad(mode)
          }

          "go to TransportCarpenter when 'SeamanCarpenter' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfTransportPage, SeamanCarpenter).success.value
            navigator.nextPage(TypeOfTransportPage, mode)(answers) mustBe
              routes.TransportCarpenterController.onPageLoad(mode)
          }

          "go to TransportVehicleTrade when 'Vehicles' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfTransportPage, Vehicles).success.value
            navigator.nextPage(TypeOfTransportPage, mode)(answers) mustBe
              routes.TransportVehicleTradeController.onPageLoad(mode)
          }

          "go to EmployerContributionController when 'None of the above' is selected" in {
            val answers = emptyUserAnswers.set(TypeOfTransportPage, NoneOfTheAbove).success.value
            navigator.nextPage(TypeOfTransportPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to Session Expired when no option available" in {
            navigator.nextPage(TypeOfTransportPage, mode)(emptyUserAnswers) mustBe
              controllers.routes.SessionExpiredController.onPageLoad()
          }
        }

        "on AirlineJobListController" must {
          "goto EmployerContributionController when 'Yes' selected" in {
            val answers = emptyUserAnswers.set(AirlineJobListPage, true).success.value
            navigator.nextPage(AirlineJobListPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "go to CabinCrewController when 'No' selected" in {
            val answers = emptyUserAnswers.set(AirlineJobListPage, false).success.value
            navigator.nextPage(AirlineJobListPage, mode)(answers) mustBe
              routes.CabinCrewController.onPageLoad(mode)
          }

        }
        "on CabinCrewController" must{
          "go to EmployerContributionController when 'Yes' selected on CabinCrewPage" in {
            val answers = emptyUserAnswers.set(CabinCrewPage, true).success.value
            navigator.nextPage(CabinCrewPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
          "go to EmployerContributionController when 'No' selected on CabinCrewPage" in {
            val answers = emptyUserAnswers.set(CabinCrewPage, false).success.value
            navigator.nextPage(CabinCrewPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }

        "on GarageHandOrCleanerController" must {
          "goto EmployerContributionController when 'Yes' selected" in {
            val answers = emptyUserAnswers.set(GarageHandOrCleanerPage, true).success.value
            navigator.nextPage(GarageHandOrCleanerPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "goto EmployerContributionController when 'No' selected" in {
            val answers = emptyUserAnswers.set(GarageHandOrCleanerPage, false).success.value
            navigator.nextPage(GarageHandOrCleanerPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }

        "on WhichRailwayTrade" must {
          for (trade <- WhichRailwayTrade.values) {
            s"goto EmployerContributionController when '$trade' selected" in {
              val answers = emptyUserAnswers.set(WhichRailwayTradePage, trade).success.value
              navigator.nextPage(WhichRailwayTradePage, mode)(answers) mustBe
                controllers.routes.EmployerContributionController.onPageLoad(mode)
            }
          }
        }

        "on TransportCarpenterController" must {
          "goto EmployerContributionController when 'Yes' selected" in {
            val answers = emptyUserAnswers.set(TransportCarpenterPage, true).success.value
            navigator.nextPage(TransportCarpenterPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }

          "goto EmployerContributionController when 'No' selected" in {
            val answers = emptyUserAnswers.set(TransportCarpenterPage, false).success.value
            navigator.nextPage(TransportCarpenterPage, mode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(mode)
          }
        }

        "on TransportVehicleTrade" must {
          for (trade <- TransportVehicleTrade.values) {
            s"goto EmployerContributionController when '$trade' selected" in {
              val answers = emptyUserAnswers.set(TransportVehicleTradePage, trade).success.value
              navigator.nextPage(TransportVehicleTradePage, mode)(answers) mustBe
                controllers.routes.EmployerContributionController.onPageLoad(mode)
            }
          }
        }
      }
    }
  }
}
