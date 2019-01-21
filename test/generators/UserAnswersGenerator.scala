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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.engineering._
import pages.healthcare._
import pages.transport._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(GarageHandOrCleanerPage.type, JsValue)] ::
    arbitrary[(FactoryEngineeringList1Page.type, JsValue)] ::
    arbitrary[(AncillaryEngineeringWhichTradePage.type, JsValue)] ::
    arbitrary[(HealthcareList2Page.type, JsValue)] ::
    arbitrary[(ConstructionalEngineeringApprenticePage.type, JsValue)] ::
    arbitrary[(ConstructionalEngineeringList2Page.type, JsValue)] ::
    arbitrary[(HealthcareList1Page.type, JsValue)] ::
    arbitrary[(AirlineJobListPage.type, JsValue)] ::
    arbitrary[(TypeOfTransportPage.type, JsValue)] ::
    arbitrary[(ConstructionalEngineeringList1Page.type, JsValue)] ::
    arbitrary[(TypeOfEngineeringPage.type, JsValue)] ::
    arbitrary[(AmbulanceStaffPage.type, JsValue)] ::
    arbitrary[(EmployerContributionPage.type, JsValue)] ::
    arbitrary[(MultipleEmploymentsPage.type, JsValue)] ::
    arbitrary[(ExpensesEmployerPaidPage.type, JsValue)] ::
    arbitrary[(FirstIndustryOptionsPage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id      <- nonEmptyString
        data    <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers (
        id = id,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }
}
