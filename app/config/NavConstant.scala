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

import javax.inject.Singleton

sealed trait NavConstant

@Singleton
object NavConstant {

  final val authenticated = "Authenticated"
  final val clothing = "Clothing"
  final val construction = "Construction"
  final val electrical = "Electrical"
  final val engineering = "Engineering"
  final val foodCatering = "FoodCatering"
  final val healthcare = "Healthcare"
  final val heating = "Heating"
  final val manufacturing = "Manufacturing"
  final val police = "Police"
  final val printing = "Printing"
  final val security = "Security"
  final val transport = "Transport"

}
