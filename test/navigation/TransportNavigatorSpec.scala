/*
 * Copyright 2019 HM Revenue & Customs
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
import models.TypeOfTransport._
import models.WhichRailwayTrade._
import models._
import pages.transport._

class TransportNavigatorSpec extends SpecBase {

  val navigator = new TransportNavigator

  "Navigator" when {

    "in Normal mode" must {

      "on TypeOfTransportController" must {
        "go to AirlineJobList when Airlines is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, Airlines).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe
            routes.AirlineJobListController.onPageLoad(NormalMode)
        }

        "go to GarageHandOrCleanerController when Public Transport is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, PublicTransport).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe
            routes.GarageHandOrCleanerController.onPageLoad(NormalMode)
        }

        "go to WhichRailwayTrade when Railways is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, Railways).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe
            routes.WhichRailwayTradeController.onPageLoad(NormalMode)
        }

        "go to TransportCarpenter when SeamanCarpenter is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, SeamanCarpenter).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe
            routes.TransportCarpenterController.onPageLoad(NormalMode)
        }

        "go to TransportVehicleTrade when Vehicles is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, Airlines).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe
            routes.AirlineJobListController.onPageLoad(NormalMode)
        }
      }

      "on AirlineJobListController" must {
        "goto EmployerContributionController when 'Yes' selected" in {
          val answers = emptyUserAnswers.set(AirlineJobListPage, true).success.value
          navigator.nextPage(AirlineJobListPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "goto EmployerContributionController when 'No' selected" in {
          val answers = emptyUserAnswers.set(AirlineJobListPage, false).success.value
          navigator.nextPage(AirlineJobListPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

      "on GarageHandOrCleanerController" must {
        "goto EmployerContributionController when 'Yes' selected" in {
          val answers = emptyUserAnswers.set(GarageHandOrCleanerPage, true).success.value
          navigator.nextPage(GarageHandOrCleanerPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "goto EmployerContributionController when 'No' selected" in {
          val answers = emptyUserAnswers.set(GarageHandOrCleanerPage, false).success.value
          navigator.nextPage(GarageHandOrCleanerPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

      "on WhichRailwayTrade" must {
        for (trade <- WhichRailwayTrade.values) {
          s"goto  when '$trade' selected" in {
            val answers = emptyUserAnswers.set(WhichRailwayTradePage, trade).success.value
            navigator.nextPage(WhichRailwayTradePage, NormalMode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
          }
        }
      }

      "on TransportCarpenterController" must {
        "goto  when 'Yes' selected" in {
          val answers = emptyUserAnswers.set(TransportCarpenterPage, true).success.value
          navigator.nextPage(TransportCarpenterPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "goto  when 'No' selected" in {
          val answers = emptyUserAnswers.set(TransportCarpenterPage, false).success.value
          navigator.nextPage(TransportCarpenterPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }
      }

      "on TransportVehicleTrade" must {
        for (trade <- TransportVehicleTrade.values) {
          s"goto  when '$trade' selected" in {
            val answers = emptyUserAnswers.set(TransportVehicleTradePage, trade).success.value
            navigator.nextPage(TransportVehicleTradePage, NormalMode)(answers) mustBe
              controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
          }
        }
      }
    }

    "in Check mode" must {


    }
  }
}
