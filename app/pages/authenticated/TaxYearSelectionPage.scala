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

package pages.authenticated

import models.{TaxYearSelection, UserAnswers}
import pages.QuestionPage
import play.api.libs.json.JsPath

import scala.util.Try

object TaxYearSelectionPage extends QuestionPage[Seq[TaxYearSelection]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "taxYearSelection"

  override def cleanup(value: Option[Seq[TaxYearSelection]], userAnswers: UserAnswers): Try[UserAnswers] = {

    value match {
      case Some(ty) =>
        val data =
          userAnswers.data - AlreadyClaimingFREDifferentAmountsPage - AlreadyClaimingFRESameAmountPage - ChangeWhichTaxYearsPage - RemoveFRECodePage

        Try(
          UserAnswers(
            id = userAnswers.id,
            data = if (ty.contains(TaxYearSelection.CurrentYear)) data else data - YourEmployerPage
          )
        )
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}
