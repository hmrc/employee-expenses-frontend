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

package config

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.Configuration

@Singleton
class ClaimAmountsConfig @Inject() (configuration: Configuration) {

  lazy val defaultRate: Int = configuration.get[Int]("claim-amounts.default-rate")

  object Healthcare {
    lazy val ambulanceStaff: Int = configuration.get[Int]("claim-amounts.healthcare.ambulance-staff")
    lazy val list1: Int = configuration.get[Int]("claim-amounts.healthcare.list-1")
    lazy val list2: Int = configuration.get[Int]("claim-amounts.healthcare.list-2")
    lazy val allOther: Int = configuration.get[Int]("claim-amounts.healthcare.all-other")
  }

  object ConstructionalEngineering {
    lazy val list1: Int = configuration.get[Int]("claim-amounts.engineering.constructional-engineering.list-1")
    lazy val list2: Int = configuration.get[Int]("claim-amounts.engineering.constructional-engineering.list-2")
    lazy val list3: Int = configuration.get[Int]("claim-amounts.engineering.constructional-engineering.list-3")
    lazy val apprentice: Int = configuration.get[Int]("claim-amounts.engineering.constructional-engineering.apprentice")
    lazy val allOther: Int = configuration.get[Int]("claim-amounts.engineering.constructional-engineering.all-other")
  }

  object AncillaryEngineering {
    lazy val patternMaker: Int = configuration.get[Int]("claim-amounts.engineering.ancillary-engineering.pattern-maker")
    lazy val labourerSupervisorUnskilledWorker: Int =
      configuration.get[Int]("claim-amounts.engineering.ancillary-engineering.labourer-supervisor-unskilledWorker")
    lazy val apprentice: Int = configuration.get[Int]("claim-amounts.engineering.ancillary-engineering.apprentice")
    lazy val allOther: Int = configuration.get[Int]("claim-amounts.engineering.ancillary-engineering.all-other")
  }

  object FactoryEngineering {
    lazy val list1: Int = configuration.get[Int]("claim-amounts.engineering.factory-engineering.list-1")
    lazy val list2: Int = configuration.get[Int]("claim-amounts.engineering.factory-engineering.list-2")
    lazy val apprentice: Int = configuration.get[Int]("claim-amounts.engineering.factory-engineering.apprentice")
    lazy val allOther: Int = configuration.get[Int]("claim-amounts.engineering.factory-engineering.all-other")
  }

  object Transport {
    lazy val pilotsFlightDeck: Int = configuration.get[Int]("claim-amounts.transport.airlines.pilots-flight-deck")
    lazy val cabinCrew: Int = configuration.get[Int]("claim-amounts.transport.airlines.cabin-crew")
    lazy val garageHands: Int = configuration.get[Int]("claim-amounts.transport.public-transport.garage-hands")
    lazy val conductorsDrivers: Int = configuration.get[Int]("claim-amounts.transport.public-transport.conductors-drivers")
    lazy val buildersRepairersWagonLifters: Int = configuration.get[Int]("claim-amounts.transport.vehicle-trade.builders-repairers-wagon-lifters")
    lazy val paintersLetterersAssistants: Int = configuration.get[Int]("claim-amounts.transport.vehicle-trade.painters-letterers-assistants")
    lazy val passengerLiners: Int = configuration.get[Int]("claim-amounts.transport.seaman.passenger-liners")
    lazy val cargoTankersCoastersFerries: Int = configuration.get[Int]("claim-amounts.transport.seaman.cargo-tankers-coasters-ferries")
    lazy val default: Int = configuration.get[Int]("claim-amounts.transport.default")
  }

  object Police {
    lazy val communitySupportOfficer: Int = configuration.get[Int]("claim-amounts.police.community-support-officer")
    lazy val policeOfficer: Int = configuration.get[Int]("claim-amounts.police.police-officer")
  }

  object Security {
    lazy val nhsSecurity: Int = configuration.get[Int]("claim-amounts.security.nhs-security")
  }

  object Clothing {
    lazy val clothingList: Int = configuration.get[Int]("claim-amounts.clothing.clothing-list")
  }

  object Construction {
    lazy val joinersCarpenters: Int = configuration.get[Int]("claim-amounts.construction.joiners-carpenters")
    lazy val stoneMasons: Int = configuration.get[Int]("claim-amounts.construction.stone-masons")
    lazy val list1: Int = configuration.get[Int]("claim-amounts.construction.list-1")
    lazy val list2: Int = configuration.get[Int]("claim-amounts.construction.list-2")
    lazy val buildingMaterials: Int = configuration.get[Int]("claim-amounts.construction.building-materials")
    lazy val allOther: Int = configuration.get[Int]("claim-amounts.construction.all-other")
  }

  object Electrical {
    lazy val onlyLaundry: Int = configuration.get[Int]("claim-amounts.electrical.only-laundry")
    lazy val allOther: Int = configuration.get[Int]("claim-amounts.electrical.all-other")
  }

  object Generic {
    lazy val agriculture: Int = configuration.get[Int]("claim-amounts.agriculture")
    lazy val fireService: Int = configuration.get[Int]("claim-amounts.fire-service")
    lazy val leisure: Int = configuration.get[Int]("claim-amounts.default-rate")
    lazy val prisons: Int = configuration.get[Int]("claim-amounts.prisons")
  }

  object Heating {
    lazy val list = configuration.get[Int]("claim-amounts.heating.list")
    lazy val allOther = configuration.get[Int]("claim-amounts.heating.all-other")
  }

}
