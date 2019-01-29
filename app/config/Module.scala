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
import repositories.{DefaultSessionRepository, SessionRepository}
import scalate.ScalateEngineBoot

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()

    // For session based storage instead of cred based, change to SessionIdentifierAction
    bind(classOf[IdentifierAction]).to(classOf[AuthenticatedIdentifierAction]).asEagerSingleton()

    bind(classOf[SessionRepository]).to(classOf[DefaultSessionRepository]).asEagerSingleton()
    bind(classOf[ScalateEngineBoot]).asEagerSingleton()

    bind(classOf[Navigator]).annotatedWith(Names.named("Generic")).to(classOf[GenericNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Engineering")).to(classOf[EngineeringNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Healthcare")).to(classOf[HealthcareNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Transport")).to(classOf[TransportNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Manufacturing")).to(classOf[TransportNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Police")).to(classOf[PoliceNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("FoodCatering")).to(classOf[FoodCateringNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Clothing")).to(classOf[ClothingNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Security")).to(classOf[SecurityNavigator])
    bind(classOf[Navigator]).annotatedWith(Names.named("Printing")).to(classOf[PrintingNavigator])

  }
}
