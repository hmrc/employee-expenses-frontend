/*
 * Copyright 2021 HM Revenue & Customs
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

package config

import javax.inject.Singleton

sealed trait ClaimAmounts

@Singleton
object ClaimAmounts {

  lazy val defaultRate: Int = 60
  lazy val agriculture: Int = 100
  lazy val fireService: Int = 80
  lazy val leisure: Int = 60
  lazy val prisons: Int = 80
  lazy val forestry: Int = 100
  lazy val docksAndWaterways: Int = 80

  object Healthcare {
    lazy val ambulanceStaff: Int = 185
    lazy val list1: Int = 143
    lazy val list2: Int = 125
    lazy val catering = 125
    lazy val allOther: Int = 80
  }

  object ConstructionalEngineering {
    lazy val list1: Int = 140
    lazy val list2: Int = 140
    lazy val list3: Int = 80
    lazy val apprentice: Int = 60
    lazy val allOther: Int = 100
  }

  object Shipyard {
    lazy val list1: Int = 140
    lazy val list2: Int = 140
    lazy val labourer: Int = 80
    lazy val apprentice: Int = 60
    lazy val allOther: Int = 100
  }

  object AncillaryEngineering {
    lazy val patternMaker: Int = 140
    lazy val labourerSupervisorUnskilledWorker: Int = 80
    lazy val apprentice: Int = 60
    lazy val allOther: Int = 120
  }

  object FactoryEngineering {
    lazy val list1: Int = 140
    lazy val list2: Int = 120
    lazy val apprentice: Int = 60
    lazy val allOther: Int = 80
  }

  object Transport {

    object Airlines {
      lazy val pilotsFlightDeck: Int = 1022
      lazy val cabinCrew: Int = 720
    }

    object PublicTransport {
      lazy val garageHands: Int = 80
      lazy val conductorsDrivers: Int = 60
    }

    object Railways {
      lazy val vehicleRepairersWagonLifters: Int = 140
      lazy val vehiclePainters: Int = 80
      lazy val allOther: Int = 100
    }

    object VehicleTrade {
      lazy val buildersRepairersWagonLifters: Int = 140
      lazy val paintersLetterersAssistants: Int = 80
      lazy val allOther: Int = 60
    }

    object Seamen {
      lazy val passengerLiners: Int = 165
      lazy val cargoTankersCoastersFerries: Int = 140
    }
  }

  object Police {
    lazy val communitySupportOfficer: Int = 140
    lazy val policeOfficer: Int = 140
  }

  object Security {
    lazy val nhsSecurity: Int = 80
  }

  object Clothing {
    lazy val clothingList: Int = 80
  }

  object Construction {
    lazy val joinersCarpenters: Int = 140
    lazy val stoneMasons: Int = 120
    lazy val list1: Int = 80
    lazy val list2: Int = 60
    lazy val asphaltCement: Int = 80
    lazy val roofingFelt: Int = 80
    lazy val labourerNavvy: Int = 60
    lazy val tileMaker: Int = 60
    lazy val buildingMaterials: Int = 80
    lazy val allOther: Int = 120
  }

  object Electrical {
    lazy val onlyLaundry: Int = 60
    lazy val allOther: Int = 120
  }

  object Heating {
    lazy val list = 120
    lazy val allOther = 100
  }

  object Printing {
    lazy val list1 = 140
    lazy val list2 = 60
    lazy val allOther = 100
  }

  object Manufacturing {

    lazy val brassCopper = 120
    lazy val glass = 80
    lazy val quarryingPreciousMetals = 100

    object Aluminium {
      lazy val list1 = 140
      lazy val list2 = 140
      lazy val list3 = 80
      lazy val apprentice = 60
      lazy val allOther = 120
    }

    object IronSteel {
      lazy val list1 = 80
      lazy val allOther = 140
      lazy val apprentice = 60
    }

    object IronMining {
      lazy val list1 = 120
      lazy val allOther = 100
    }

    object WoodFurniture {
      lazy val list1 = 140
      lazy val list2 = 120
      lazy val list3 = 60
      lazy val allOther = 100
    }
  }

  object Textiles {
    lazy val list1: Int = 120
    lazy val allOther: Int = 80
  }
}
