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

package generators

import models.*
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  given Arbitrary[ConstructionOccupations] =
    Arbitrary {
      Gen.oneOf(ConstructionOccupations.values.toSeq)
    }

  given Arbitrary[TaxCodeStatus] =
    Arbitrary {
      Gen.oneOf(TaxCodeStatus.values)
    }

  given Arbitrary[EmployerContribution] =
    Arbitrary {
      Gen.oneOf(EmployerContribution.values)
    }

  given Arbitrary[FifthIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FifthIndustryOptions.values)
    }

  given Arbitrary[MultipleEmployments] =
    Arbitrary {
      Gen.oneOf(MultipleEmployments.values)
    }

  given Arbitrary[AlreadyClaimingFREDifferentAmounts] =
    Arbitrary {
      Gen.oneOf(AlreadyClaimingFREDifferentAmounts.values)
    }

  given Arbitrary[AlreadyClaimingFRESameAmount] =
    Arbitrary {
      Gen.oneOf(AlreadyClaimingFRESameAmount.values)
    }

  given Arbitrary[TaxYearSelection] =
    Arbitrary {
      Gen.oneOf(TaxYearSelection.values)
    }

  given Arbitrary[FourthIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FourthIndustryOptions.values)
    }

  given Arbitrary[ThirdIndustryOptions] =
    Arbitrary {
      Gen.oneOf(ThirdIndustryOptions.values)
    }

  given Arbitrary[TypeOfManufacturing] =
    Arbitrary {
      Gen.oneOf(TypeOfManufacturing.values)
    }

  given Arbitrary[TransportVehicleTrade] =
    Arbitrary {
      Gen.oneOf(TransportVehicleTrade.values)
    }

  given Arbitrary[SecondIndustryOptions] =
    Arbitrary {
      Gen.oneOf(SecondIndustryOptions.values)
    }

  given Arbitrary[WhichRailwayTrade] =
    Arbitrary {
      Gen.oneOf(WhichRailwayTrade.values)
    }

  given Arbitrary[AncillaryEngineeringWhichTrade] =
    Arbitrary {
      Gen.oneOf(AncillaryEngineeringWhichTrade.values)
    }

  given Arbitrary[TypeOfTransport] =
    Arbitrary {
      Gen.oneOf(TypeOfTransport.values)
    }

  given Arbitrary[TypeOfEngineering] =
    Arbitrary {
      Gen.oneOf(TypeOfEngineering.values)
    }

  given Arbitrary[FirstIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FirstIndustryOptions.values)
    }

}
