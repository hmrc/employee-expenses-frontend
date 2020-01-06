/*
 * Copyright 2020 HM Revenue & Customs
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
import config.{FrontendAppConfig, NavConstant}
import controllers.actions._
import models.AlreadyClaimingFRESameAmount.Remove
import models.FirstIndustryOptions.{Healthcare, Retail}
import models.FlatRateExpenseOptions.FRENoYears
import models.TaxYearSelection.{CurrentYear, CurrentYearMinus1}
import models._
import navigation.{FakeNavigator, Navigator}
import org.scalatest.TryValues
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import pages._
import pages.authenticated._
import pages.healthcare.HealthcareList1Page
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.partials.FormPartialRetriever
import utils.{MockEeFormPartialRetriever, MockScalate}

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with TryValues {

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

  lazy val emptyAddressJson: JsValue = Json.parse(
    s"""
       |{
       |  "address":{
       |    "line1":"",
       |    "line2":"",
       |    "line3":"",
       |    "line4":"",
       |    "line5":"",
       |    "postcode":"",
       |    "country":""
       |  }
       |}
     """.stripMargin
  )

  lazy val etag: Int = 123

  lazy val validEtagJson: JsValue = Json.parse(
    s"""
       |{
       |   "etag":"$etag"
       |}
    """.stripMargin)

  lazy val taiEmployment: Seq[Employment] = Seq(Employment(
    name = "HMRC LongBenton"
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

  lazy val checkYourAnswersTextNoFre: CheckYourAnswersText =
    CheckYourAnswersText(heading = "title", disclaimerHeading = "claimExpenses", disclaimer = "confirmInformationNoFre", button = "acceptClaimExpenses")
  lazy val checkYourAnswersTextStopFre: CheckYourAnswersText =
    CheckYourAnswersText(heading = "heading", disclaimerHeading = "stopClaim", disclaimer = "confirmInformationChangeFre", button = "acceptStopClaim")
  lazy val checkYourAnswersTextChangeFre: CheckYourAnswersText =
    CheckYourAnswersText(heading = "title", disclaimerHeading = "changeClaim", disclaimer = "confirmInformationChangeFre", button = "acceptChangeClaim")

  def currentYearFullUserAnswers: UserAnswers = emptyUserAnswers
    .set(FirstIndustryOptionsPage, Healthcare).success.value
    .set(HealthcareList1Page , true).success.value
    .set(EmployerContributionPage, EmployerContribution.YesEmployerContribution).success.value
    .set(ExpensesEmployerPaidPage, 123).success.value
    .set(SameEmployerContributionAllYearsPage, true).success.value
    .set(TaxYearSelectionPage, Seq(CurrentYear)).success.value
    .set(YourAddressPage, true).success.value
    .set(YourEmployerPage, true).success.value
    .set(CitizenDetailsAddress, address).success.value
    .set(ClaimAmount, 200).success.value
    .set(ClaimAmountAndAnyDeductions, 80).success.value
    .set(FREResponse, FRENoYears).success.value

  val currentYearMinus1UserAnswers = emptyUserAnswers
    .set(EmployerContributionPage,  EmployerContribution.NoEmployerContribution).success.value
    .set(TaxYearSelectionPage, Seq(CurrentYearMinus1)).success.value
    .set(YourAddressPage, true).success.value
    .set(YourEmployerPage, true).success.value
    .set(ClaimAmount, 100).success.value
    .set(ClaimAmountAndAnyDeductions, 80).success.value
    .set(FREResponse, FRENoYears).success.value

  def yearsUserAnswers(years: Seq[TaxYearSelection]) = emptyUserAnswers
    .set(EmployerContributionPage,  EmployerContribution.NoEmployerContribution).success.value
    .set(TaxYearSelectionPage, years).success.value
    .set(YourAddressPage, true).success.value
    .set(YourEmployerPage, true).success.value
    .set(ClaimAmount, 100).success.value
    .set(ClaimAmountAndAnyDeductions, 80).success.value
    .set(FREResponse, FRENoYears).success.value
    .set(FREAmounts, Seq(FlatRateExpenseAmounts(Some(FlatRateExpense(100)), TaiTaxYear()))).success.value


  def minimumUserAnswers: UserAnswers = emptyUserAnswers
    .set(FirstIndustryOptionsPage, Retail).success.value
    .set(EmployerContributionPage, EmployerContribution.NoEmployerContribution).success.value
    .set(ClaimAmountAndAnyDeductions, 60).success.value
    .set(TaxYearSelectionPage, Seq(CurrentYear)).success.value
    .set(AlreadyClaimingFRESameAmountPage, Remove).success.value
    .set(RemoveFRECodePage, CurrentYear).success.value

  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  implicit val hc: HeaderCarrier = HeaderCarrier()

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest = FakeRequest("", "")

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                                   onwardRoute: Option[Call] = None): GuiceApplicationBuilder = {

    val default = new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[AuthenticatedIdentifierAction].to[FakeAuthedIdentifierAction],
        bind[UnauthenticatedIdentifierAction].to[FakeUnauthenticatedIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[Scalate].to[MockScalate],
        bind[FormPartialRetriever].to[MockEeFormPartialRetriever]
      )

    onwardRoute match {
      case Some(onward) =>
        default.overrides(bind[Navigator].qualifiedWith(NavConstant.authenticated).toInstance(new FakeNavigator(onward)))
      case None => default
    }
  }
}
