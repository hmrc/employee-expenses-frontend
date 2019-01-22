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
import models._
import pages.transport.TypeOfTransportPage

class TransportNavigatorSpec extends SpecBase {

  val navigator = new TransportNavigator

  "Navigator" when {

    "in Normal mode" must {

      "on TypeOfTransportContoller" must {
        "go to AirlineJobList when Airlines is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, Airlines).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe routes.AirlineJobListController.onPageLoad(NormalMode)
        }

        "go to GarageHandOrCleanerController when Public Transport is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, PublicTransport).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe routes.GarageHandOrCleanerController.onPageLoad(NormalMode)
        }

        "go to WhichRailwayTrade when Railways is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, Railways).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe routes.WhichRailwayTradeController.onPageLoad(NormalMode)
        }

        "go to TransportCarpenter when SeamanCarpenter is selected" in {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, SeamanCarpenter).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe routes.TransportCarpenterController.onPageLoad(NormalMode)
        }

        "go to TransportVechileTrade when Vehicles is selected" ignore {
          val answers = emptyUserAnswers.set(TypeOfTransportPage, Airlines).success.value
          navigator.nextPage(TypeOfTransportPage, NormalMode)(answers) mustBe routes.AirlineJobListController.onPageLoad(NormalMode)
        }
      }
    }

    "in Check mode" must {


    }
  }
}
