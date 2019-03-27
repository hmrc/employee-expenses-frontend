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

package generators

import models._
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryMultipleEmployments: Arbitrary[MultipleEmployments] =
    Arbitrary {
      Gen.oneOf(MultipleEmployments.values)
    }

  implicit lazy val arbitraryAlreadyClaimingFREDifferentAmounts: Arbitrary[AlreadyClaimingFREDifferentAmounts] =
    Arbitrary {
      Gen.oneOf(AlreadyClaimingFREDifferentAmounts.values)
    }

  implicit lazy val arbitraryAlreadyClaimingFRESameAmount: Arbitrary[AlreadyClaimingFRESameAmount] =
    Arbitrary {
      Gen.oneOf(AlreadyClaimingFRESameAmount.values)
    }

  implicit lazy val arbitraryTaxYearSelection: Arbitrary[TaxYearSelection] =
    Arbitrary {
      Gen.oneOf(TaxYearSelection.values)
    }

  implicit lazy val arbitraryFourthIndustryOptions: Arbitrary[FourthIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FourthIndustryOptions.values)
    }

  implicit lazy val arbitraryThirdIndustryOptions: Arbitrary[ThirdIndustryOptions] =
    Arbitrary {
      Gen.oneOf(ThirdIndustryOptions.values)
    }

  implicit lazy val arbitraryTypeOfManufacturing: Arbitrary[TypeOfManufacturing] =
    Arbitrary {
      Gen.oneOf(TypeOfManufacturing.values)
    }

  implicit lazy val arbitraryTransportVehicleTrade: Arbitrary[TransportVehicleTrade] =
    Arbitrary {
      Gen.oneOf(TransportVehicleTrade.values)
    }

  implicit lazy val arbitrarySecondIndustryOptions: Arbitrary[SecondIndustryOptions] =
    Arbitrary {
      Gen.oneOf(SecondIndustryOptions.values)
    }

  implicit lazy val arbitraryWhichRailwayTrade: Arbitrary[WhichRailwayTrade] =
    Arbitrary {
      Gen.oneOf(WhichRailwayTrade.values)
    }

  implicit lazy val arbitraryAncillaryEngineeringWhichTrade: Arbitrary[AncillaryEngineeringWhichTrade] =
    Arbitrary {
      Gen.oneOf(AncillaryEngineeringWhichTrade.values)
    }

  implicit lazy val arbitraryTypeOfTransport: Arbitrary[TypeOfTransport] =
    Arbitrary {
      Gen.oneOf(TypeOfTransport.values)
    }

  implicit lazy val arbitraryTypeOfEngineering: Arbitrary[TypeOfEngineering] =
    Arbitrary {
      Gen.oneOf(TypeOfEngineering.values)
    }

  implicit lazy val arbitraryEmployerContribution: Arbitrary[EmployerContribution] =
    Arbitrary {
      Gen.oneOf(EmployerContribution.values)
    }

  implicit lazy val arbitraryFirstIndustryOptions: Arbitrary[FirstIndustryOptions] =
    Arbitrary {
      Gen.oneOf(FirstIndustryOptions.values)
    }
}
