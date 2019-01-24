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
  object Healthcare {
    lazy val ambulanceStaff: Int = configuration.get[Int]("claim-amounts.healthcare.ambulance-staff")
    lazy val list1: Int = configuration.get[Int]("claim-amounts.healthcare.list-1")
    lazy val list2: Int = configuration.get[Int]("claim-amounts.healthcare.list-2")
    lazy val allOther: Int = configuration.get[Int]("claim-amounts.healthcare.all-other")
  }
}
