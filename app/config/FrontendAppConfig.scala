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

  private val contactHost = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "play26frontend"

  val assetsPath: String = configuration.get[String]("assets.url") + configuration.get[String]("assets.version") + "/"
  val govukTemplatePath: String = "/templates/mustache/production/"
  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")
  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  lazy val taiUrl: String = configuration.get[Service]("microservice.services.tai").baseUrl
  lazy val citizenDetailsUrl: String = configuration.get[Service]("microservice.services.citizenDetails").baseUrl

  lazy val authUrl: String = configuration.get[Service]("microservice.services.auth").baseUrl
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val ivUpliftUrl: String = configuration.get[String]("identity-verification-uplift.host")
  lazy val authorisedCallback: String = configuration.get[String]("identity-verification-uplift.authorised-callback.url")
  lazy val unauthorisedCallback: String = configuration.get[String]("identity-verification-uplift.unauthorised-callback.url")

  lazy val p87Url: String = configuration.get[String]("p87.url")
  lazy val govUkUrl: String = configuration.get[String]("govUk.url")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val taxPercentageBand1: Int = configuration.get[Int]("tax-percentage.band-1")
  lazy val taxPercentageBand2: Int = configuration.get[Int]("tax-percentage.band-2")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}
