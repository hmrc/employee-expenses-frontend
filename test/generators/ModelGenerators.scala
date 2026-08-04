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

import models._
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  given arbitraryConstructionOccupations: Arbitrary[ConstructionOccupations] =
    Arbitrary {
      Gen.oneOf(ConstructionOccupations.values.toSeq)
    }

  given arbitraryTaxCodeStatus: Arbitrary[TaxCodeStatus] =
    Arbitrary {
      Gen.oneOf(TaxCodeStatus.values)
    }

  given arbitraryEmployerContribution: Arbitrary[EmployerContribution] =
    Arbitrary {
      Gen.oneOf(EmployerContribution.values)
    }

  given arbitraryFifthIndustryOptions: Arbitrary[FifthIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FifthIndustryOptions.values)
    }

  given arbitraryMultipleEmployments: Arbitrary[MultipleEmployments] =
    Arbitrary {
      Gen.oneOf(MultipleEmployments.values)
    }

  given arbitraryAlreadyClaimingFREDifferentAmounts: Arbitrary[AlreadyClaimingFREDifferentAmounts] =
    Arbitrary {
      Gen.oneOf(AlreadyClaimingFREDifferentAmounts.values)
    }

  given arbitraryAlreadyClaimingFRESameAmount: Arbitrary[AlreadyClaimingFRESameAmount] =
    Arbitrary {
      Gen.oneOf(AlreadyClaimingFRESameAmount.values)
    }

  given arbitraryTaxYearSelection: Arbitrary[TaxYearSelection] =
    Arbitrary {
      Gen.oneOf(TaxYearSelection.values)
    }

  given arbitraryFourthIndustryOptions: Arbitrary[FourthIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FourthIndustryOptions.values)
    }

  given arbitraryThirdIndustryOptions: Arbitrary[ThirdIndustryOptions] =
    Arbitrary {
      Gen.oneOf(ThirdIndustryOptions.values)
    }

  given arbitraryTypeOfManufacturing: Arbitrary[TypeOfManufacturing] =
    Arbitrary {
      Gen.oneOf(TypeOfManufacturing.values)
    }

  given arbitraryTransportVehicleTrade: Arbitrary[TransportVehicleTrade] =
    Arbitrary {
      Gen.oneOf(TransportVehicleTrade.values)
    }

  given arbitrarySecondIndustryOptions: Arbitrary[SecondIndustryOptions] =
    Arbitrary {
      Gen.oneOf(SecondIndustryOptions.values)
    }

  given arbitraryWhichRailwayTrade: Arbitrary[WhichRailwayTrade] =
    Arbitrary {
      Gen.oneOf(WhichRailwayTrade.values)
    }

  given arbitraryAncillaryEngineeringWhichTrade: Arbitrary[AncillaryEngineeringWhichTrade] =
    Arbitrary {
      Gen.oneOf(AncillaryEngineeringWhichTrade.values)
    }

  given arbitraryTypeOfTransport: Arbitrary[TypeOfTransport] =
    Arbitrary {
      Gen.oneOf(TypeOfTransport.values)
    }

  given arbitraryTypeOfEngineering: Arbitrary[TypeOfEngineering] =
    Arbitrary {
      Gen.oneOf(TypeOfEngineering.values)
    }

  given arbitraryFirstIndustryOptions: Arbitrary[FirstIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FirstIndustryOptions.values)
    }

}
