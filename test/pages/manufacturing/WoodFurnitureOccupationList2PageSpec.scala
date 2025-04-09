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

package pages.manufacturing

import pages.behaviours.PageBehaviours

class WoodFurnitureOccupationList2PageSpec extends PageBehaviours {

  "WoodFurnitureOccupationList2Page.scala" must {

    beRetrievable[Boolean](WoodFurnitureOccupationList2Page)

    beSettable[Boolean](WoodFurnitureOccupationList2Page)

    beRemovable[Boolean](WoodFurnitureOccupationList2Page)
  }

}
