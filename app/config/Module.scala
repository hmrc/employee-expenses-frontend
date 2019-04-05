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

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import controllers.actions._
import navigation._
import repositories._
import scalate.ScalateEngineBoot

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()

    // For session based storage instead of cred based, change to SessionIdentifierAction
    bind(classOf[AuthenticatedIdentifierAction]).to(classOf[AuthenticatedIdentifierActionImpl]).asEagerSingleton()
    bind(classOf[UnauthenticatedIdentifierAction]).to(classOf[UnauthenticatedIdentifierActionImpl]).asEagerSingleton()

    bind(classOf[SessionRepository]).to(classOf[DefaultSessionRepository]).asEagerSingleton()
    bind(classOf[ScalateEngineBoot]).asEagerSingleton()

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

  }
}
