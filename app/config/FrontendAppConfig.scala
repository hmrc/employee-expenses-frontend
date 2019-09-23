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

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  lazy val serviceTitle = "Claim for your work uniform and tools - GOV.UK"

  private val contactHost = configuration.get[Service]("microservice.services.contact-frontend").baseUrl
  private val contactFormServiceIdentifier = "employeeExpensesFrontend"

  val assetsPath: String = configuration.get[String]("assets.url") + configuration.get[String]("assets.version") + "/"
  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  lazy val taiHost: String = configuration.get[Service]("microservice.services.tai").baseUrl
  lazy val citizenDetailsUrl: String = configuration.get[Service]("microservice.services.citizen-details").baseUrl
  lazy val employeeExpensesFrontendUrl: String = configuration.get[String]("employee-expenses.url")

  lazy val authUrl: String = configuration.get[Service]("microservice.services.auth").baseUrl
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val ivUpliftUrl: String = configuration.get[String]("identity-verification-uplift.host")
  lazy val authorisedCallback: String = configuration.get[String]("identity-verification-uplift.authorised-callback.url")
  lazy val unauthorisedCallback: String = configuration.get[String]("identity-verification-uplift.unauthorised-callback.url")

  lazy val p87Url: String = configuration.get[String]("p87.url")
  lazy val p87ClaimOnlineUrl: String = configuration.get[String]("p87.claimOnlineUrl")
  lazy val p87ClaimByPostUrl: String = configuration.get[String]("p87.claimByPostUrl")
  lazy val claimOnlineUrl: String = configuration.get[String]("claimOnline.url")
  lazy val govUkUrl: String = configuration.get[String]("govUk.url")
  lazy val jobExpensesLink: String = configuration.get[String]("jobExpenses.url")
  lazy val contactHMRC: String = configuration.get[String]("contactHMRC.url")
  lazy val incomeTaxSummary: String = configuration.get[String]("incomeTaxSummary.url")

  lazy val incomeSummary: String = configuration.get[String]("incomeSummary.url")
  lazy val personalDetails: String = configuration.get[String]("personalDetails.url")

  lazy val optimizelyProjectId: String = configuration.get[String]("optimizely.projectId")

  lazy val updateAddressInfoUrl: String = configuration.get[String]("updateAddressInfo.url")
  lazy val updateEmployerInfoUrl: String = configuration.get[String]("updateEmployerInfo.url")

  lazy val feedbackSurveyUrl: String = configuration.get[String]("feedbackSurvey.url")
  lazy val signOutUrl: String = employeeExpensesFrontendUrl + "/sign-out"
  lazy val keepAliveUrl: String = configuration.get[String]("keepAlive.url")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val taxPercentageBasicRate: Int = configuration.get[Int]("tax-percentage.basicRate")
  lazy val taxPercentageHigherRate: Int = configuration.get[Int]("tax-percentage.higherRate")
  lazy val taxPercentageScotlandStarterRate: Int = configuration.get[Int]("scottish-tax-percentage.starterRate")
  lazy val taxPercentageScotlandBasicRate: Int = configuration.get[Int]("scottish-tax-percentage.basicRate")
  lazy val taxPercentageScotlandIntermediateRate: Int = configuration.get[Int]("scottish-tax-percentage.intermediateRate")
  lazy val taxPercentageScotlandHigherRate: Int = configuration.get[Int]("scottish-tax-percentage.higherRate")

  lazy val flatRateExpenseId: Int = configuration.get[Int]("flatRateExpenseId")

  lazy val accessibilityStatementUrl: String = configuration.get[String]("accessibilityStatement.govAccessibilityStatementUrl")
  lazy val abilityNettUrl: String = configuration.get[String]("accessibilityStatement.abilityNetUrl")
  lazy val w3StandardsUrl: String = configuration.get[String]("accessibilityStatement.w3StandardsUrl")
  lazy val equalityAdvisoryServiceUrl: String = configuration.get[String]("accessibilityStatement.equalityAdvisoryServiceUrl")
  lazy val equalityNIUrl: String = configuration.get[String]("accessibilityStatement.equalityNIUrl")
  lazy val dealingHmrcAdditionalNeedsUrl: String = configuration.get[String]("accessibilityStatement.dealingHmrcAdditionalNeedsUrl")
  lazy val dacUrl: String = configuration.get[String]("accessibilityStatement.dacUrl")

  val accessibilityStatementLastTested: String = configuration.get[String]("accessibilityStatement.lastTested")
  val accessibilityStatementFirstPublished: String = configuration.get[String]("accessibilityStatement.firstPublished")
  lazy val accessibilityStatementEnabled: Boolean = configuration.get[Boolean]("accessibilityStatement.enabled")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}
