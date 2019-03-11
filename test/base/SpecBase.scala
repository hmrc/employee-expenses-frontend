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

package base

import com.github.tototoshi.play2.scalate.Scalate
import config.FrontendAppConfig
import controllers.actions._
import models.EmployerContribution.{NoContribution, SomeContribution}
import models.FirstIndustryOptions.{Healthcare, Retail}
import models.FlatRateExpenseOptions.FRENoYears
import models.TaxYearSelection.CurrentYear
import models._
import org.joda.time.LocalDate
import org.scalatest.TryValues
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import pages.authenticated.{AlreadyClaimingFRESameAmountPage, RemoveFRECodePage, TaxYearSelectionPage, YourAddressPage}
import pages._
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import utils.MockScalate

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with TryValues {

  override lazy val app: Application = {

    import play.api.inject._

    new GuiceApplicationBuilder()
      .overrides(
        bind[Scalate].to[MockScalate]
      ).build()
  }

  lazy val userAnswersId = "id"

  lazy val fakeNino = "AB123456A"

  lazy val address = Address(
    Some("6 Howsell Road"),
    Some("Llanddew"),
    Some("Line 3"),
    Some("Line 4"),
    Some("Line 5"),
    Some("DN16 3FB"),
    Some("GREAT BRITAIN")
  )

  lazy val validAddressJson: JsValue = Json.parse(
    s"""
       |{
       |  "address":{
       |    "line1":"6 Howsell Road",
       |    "line2":"Llanddew",
       |    "line3":"Line 3",
       |    "line4":"Line 4",
       |    "line5":"Line 5",
       |    "postcode":"DN16 3FB",
       |    "country":"GREAT BRITAIN"
       |  }
       |}
     """.stripMargin
  )

  lazy val taiEmployment: Seq[Employment] = Seq(Employment(
    name = "HMRC LongBenton",
    startDate = LocalDate.parse("2018-06-27"),
    endDate = None
  ))

  lazy val emptyAddress = Address(
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  def fullUserAnswers: UserAnswers = emptyUserAnswers
    .set(FirstIndustryOptionsPage, Healthcare).success.value
    .set(EmployerContributionPage, SomeContribution).success.value
    .set(ExpensesEmployerPaidPage, 123).success.value
    .set(TaxYearSelectionPage, Seq(CurrentYear)).success.value
    .set(YourAddressPage, true).success.value
    .set(CitizenDetailsAddress, address).success.value
    .set(ClaimAmount, 100).success.value
    .set(ClaimAmountAndAnyDeductions, 80).success.value
    .set(FREResponse, FRENoYears).success.value
    .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))).success.value

  def minimumUserAnswers: UserAnswers = emptyUserAnswers
    .set(FirstIndustryOptionsPage, Retail).success.value
    .set(EmployerContributionPage, NoContribution).success.value
    .set(ClaimAmountAndAnyDeductions, 60).success.value
    .set(TaxYearSelectionPage, Seq(CurrentYear)).success.value
    .set(AlreadyClaimingFRESameAmountPage, false).success.value
    .set(RemoveFRECodePage, CurrentYear).success.value

  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  implicit val hc: HeaderCarrier = HeaderCarrier()

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest = FakeRequest("", "")

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[AuthenticatedIdentifierAction].to[FakeAuthedIdentifierAction],
        bind[UnauthenticatedIdentifierAction].to[FakeUnauthenticatedIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )
}
