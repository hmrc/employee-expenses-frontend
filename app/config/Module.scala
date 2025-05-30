/*
 * Copyright 2023 HM Revenue & Customs
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

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import controllers.actions._
import navigation._
import play.api.{Configuration, Environment}
import views.{LayoutProvider, NewLayoutProvider, OldLayoutProvider}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  val scaWrapperEnabled: Boolean = configuration.getOptional[Boolean]("feature-switch.sca-wrapper").getOrElse(false)

  override def configure(): Unit = {

    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()

    bind(classOf[AuthenticatedIdentifierAction]).to(classOf[AuthenticatedIdentifierActionImpl]).asEagerSingleton()
    bind(classOf[MergedJourneyIdentifierAction]).to(classOf[MergedJourneyIdentifierActionImpl]).asEagerSingleton()
    bind(classOf[UnauthenticatedIdentifierAction]).to(classOf[UnauthenticatedIdentifierActionImpl]).asEagerSingleton()

    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.generic)).to(classOf[GenericNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.engineering)).to(classOf[EngineeringNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.healthcare)).to(classOf[HealthcareNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.transport)).to(classOf[TransportNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.manufacturing)).to(classOf[ManufacturingNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.police)).to(classOf[PoliceNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.foodCatering)).to(classOf[FoodCateringNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.clothing)).to(classOf[ClothingNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.security)).to(classOf[SecurityNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.printing)).to(classOf[PrintingNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.electrical)).to(classOf[ElectricalNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.construction)).to(classOf[ConstructionNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.authenticated)).to(classOf[AuthenticatedNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.heating)).to(classOf[HeatingNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.shipyard)).to(classOf[ShipyardNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.docks)).to(classOf[DocksNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named(NavConstant.textiles)).to(classOf[TextilesNavigator])

    if (scaWrapperEnabled) {
      bind(classOf[LayoutProvider]).to(classOf[NewLayoutProvider]).asEagerSingleton()
    } else {
      bind(classOf[LayoutProvider]).to(classOf[OldLayoutProvider]).asEagerSingleton()
    }
  }

}
