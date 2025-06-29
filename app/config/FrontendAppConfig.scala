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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (val configuration: Configuration, val servicesConfig: ServicesConfig) {

  lazy val serviceTitle = "Claim for your work uniform and tools - GOV.UK"

  lazy val contactHost = configuration.get[Service]("microservice.services.contact-frontend").baseUrl

  lazy val taiHost: String                 = configuration.get[Service]("microservice.services.tai").baseUrl
  lazy val citizenDetailsUrl: String       = configuration.get[Service]("microservice.services.citizen-details").baseUrl
  lazy val employeeWfhExpensesHost: String = servicesConfig.baseUrl("employee-wfh-expenses-frontend")

  lazy val authUrl: String          = configuration.get[Service]("microservice.services.auth").baseUrl
  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val ivUpliftUrl: String = configuration.get[String]("identity-verification-uplift.host")

  lazy val authorisedCallback: String =
    configuration.get[String]("identity-verification-uplift.authorised-callback.url")

  lazy val unauthorisedCallback: String =
    configuration.get[String]("identity-verification-uplift.unauthorised-callback.url")

  lazy val p87Url: String            = configuration.get[String]("p87.url")
  lazy val p87ClaimOnlineUrl: String = configuration.get[String]("p87.claimOnlineUrl")
  lazy val claimByIformUrl: String   = configuration.get[String]("p87.claimByIformUrl")
  lazy val p87ClaimByPostUrl: String = configuration.get[String]("p87.claimByPostUrl")
  lazy val claimOnlineUrl: String    = configuration.get[String]("claimOnline.url")
  lazy val govUkUrl: String          = configuration.get[String]("govUk.url")
  lazy val jobExpensesLink: String   = configuration.get[String]("jobExpenses.url")
  lazy val contactHMRC: String       = configuration.get[String]("contactHMRC.url")
  lazy val incomeTaxSummary: String  = configuration.get[String]("incomeTaxSummary.url")

  lazy val ptaBaseUrl: String = servicesConfig.baseUrl("pertax-frontend")

  lazy val ptaHomeUrl: String =
    s"$ptaBaseUrl${servicesConfig.getConfString("pertax-frontend.urls.home", "/personal-account")}"

  lazy val messagesUrl: String =
    s"$ptaBaseUrl${servicesConfig.getConfString("pertax-frontend.urls.messages", "/messages")}"

  lazy val yourProfileUrl: String =
    s"$ptaBaseUrl${servicesConfig.getConfString("pertax-frontend.urls.yourProfile", "/your-profile")}"

  lazy val trackBaseUrl: String = servicesConfig.baseUrl("tracking-frontend")
  lazy val trackingHomeUrl = s"$trackBaseUrl${servicesConfig.getConfString("tracking-frontend.urls.home", "/track")}"

  lazy val incomeSummary: String   = configuration.get[String]("incomeSummary.url")
  lazy val personalDetails: String = configuration.get[String]("personalDetails.url")

  lazy val optimizelyProjectId: String = configuration.get[String]("optimizely.projectId")

  lazy val updateAddressInfoUrl: String  = configuration.get[String]("updateAddressInfo.url")
  lazy val updateEmployerInfoUrl: String = configuration.get[String]("updateEmployerInfo.url")

  lazy val keepAliveUrl: String = configuration.get[String]("keepAlive.url")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val taxPercentageBasicRate: Int           = configuration.get[Int]("tax-percentage.basicRate")
  lazy val taxPercentageHigherRate: Int          = configuration.get[Int]("tax-percentage.higherRate")
  lazy val taxPercentageScotlandStarterRate: Int = configuration.get[Int]("scottish-tax-percentage.starterRate")
  lazy val taxPercentageScotlandBasicRate: Int   = configuration.get[Int]("scottish-tax-percentage.basicRate")

  lazy val taxPercentageScotlandIntermediateRate: Int =
    configuration.get[Int]("scottish-tax-percentage.intermediateRate")

  lazy val taxPercentageScotlandHigherRate: Int   = configuration.get[Int]("scottish-tax-percentage.higherRate")
  lazy val taxPercentageScotlandAdvancedRate: Int = configuration.get[Int]("scottish-tax-percentage.advancedRate")
  lazy val taxPercentageScotlandTopRate: Int      = configuration.get[Int]("scottish-tax-percentage.topRate")

  lazy val flatRateExpenseId: Int = configuration.get[Int]("flatRateExpenseId")

  lazy val scaWrapperEnabled: Boolean =
    configuration.getOptional[Boolean]("feature-switch.sca-wrapper").getOrElse(false)

  lazy val mergedJourneyEnabled: Boolean =
    configuration.getOptional[Boolean]("feature-switch.merged-journey").getOrElse(false)

  lazy val eligibilityCheckerUrl: String = configuration.get[String]("mergedJourney.eligibilityCheckerFallbackUrl")
  lazy val startUrlWfh: String           = configuration.get[String]("mergedJourney.workingFromHomeExpensesUrl")
  lazy val startUrlPsubs: String         = configuration.get[String]("mergedJourney.professionalSubscriptionsUrl")
  lazy val startUrlFre: String           = configuration.get[String]("mergedJourney.employeeExpensesUrl")
  lazy val employeeExpensesClaimByIformUrl: String = configuration.get[String]("urls.employeeExpensesClaimByIformUrl")

  lazy val feedbackSurveyUrl: String            = configuration.get[String]("feedbackSurvey.url")
  private lazy val basGatewaySignOutUrl: String = servicesConfig.getString("bas-gateway-frontend.sign-out-url")

  lazy val signOutUrl: String = s"$basGatewaySignOutUrl?continue=$feedbackSurveyUrl"

}
