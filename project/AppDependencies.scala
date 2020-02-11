import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"           %% "simple-reactivemongo"           % "7.23.0-play-26",
    "uk.gov.hmrc"           %% "logback-json-logger"            % "4.6.0",
    "uk.gov.hmrc"           %% "govuk-template"                 % "5.52.0-play-26",
    "uk.gov.hmrc"           %% "play-health"                    % "3.14.0-play-26",
    "uk.gov.hmrc"           %% "play-ui"                        % "8.8.0-play-26",
    "uk.gov.hmrc"           %% "http-caching-client"            % "9.0.0-play-26",
    "uk.gov.hmrc"           %% "play-conditional-form-mapping"  % "1.2.0-play-26",
    "uk.gov.hmrc"           %% "bootstrap-play-26"              % "1.3.0",
    "uk.gov.hmrc"           %% "play-whitelist-filter"          % "3.1.0-play-26",
    "uk.gov.hmrc"           %% "tax-year"                       % "1.0.0",
    "uk.gov.hmrc"           %% "play-partials"                  % "6.9.0-play-26",
    "com.typesafe.play"     %% "play-json-joda"                 % "2.8.1",
    "org.scalatra.scalate"  %% "play-scalate"                   % "0.6.0",
    "org.scalatra.scalate"  %% "scalate-core"                   % "1.9.5"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"            % "3.0.8",
    "org.scalatestplus.play"  %% "scalatestplus-play"   % "3.1.3",
    "org.pegdown"             %  "pegdown"              % "1.6.0",
    "org.jsoup"               %  "jsoup"                % "1.12.2",
    "com.typesafe.play"       %% "play-test"            % PlayVersion.current,
    "org.mockito"             %  "mockito-all"          % "1.10.19",
    "org.scalacheck"          %% "scalacheck"           % "1.14.3",
    "com.github.tomakehurst"  %  "wiremock-standalone"  % "2.26.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
