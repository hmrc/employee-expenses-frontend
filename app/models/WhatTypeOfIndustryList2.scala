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

package models

import play.api.libs.json._
import viewmodels.RadioOption

sealed trait WhatTypeOfIndustryList2

object WhatTypeOfIndustryList2 extends Enumerable.Implicits {

  case object Manufacturing extends WithName("manufacturing") with WhatTypeOfIndustryList2
  case object Council extends WithName("council") with WhatTypeOfIndustryList2
  case object Police extends WithName("police") with WhatTypeOfIndustryList2
  case object Clothing extends WithName("clothing") with WhatTypeOfIndustryList2
  case object Construction extends WithName("construction") with WhatTypeOfIndustryList2
  case object None extends WithName("none") with WhatTypeOfIndustryList2

  val values: Seq[WhatTypeOfIndustryList2] = Seq(
    Manufacturing, Council, Police, Clothing, Construction, None
  )

  val options: Seq[RadioOption] = values.map {
    value =>
      RadioOption("whatTypeOfIndustryList2", value.toString)
  }

  implicit val enumerable: Enumerable[WhatTypeOfIndustryList2] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
