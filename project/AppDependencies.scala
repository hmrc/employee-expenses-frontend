import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"           %% "simple-reactivemongo"           % "7.31.0-play-27",
    "uk.gov.hmrc"           %% "logback-json-logger"            % "5.1.0",
    "uk.gov.hmrc"           %% "govuk-template"                 % "5.65.0-play-27",
    "uk.gov.hmrc"           %% "play-health"                    % "3.16.0-play-27",
    "uk.gov.hmrc"           %% "play-ui"                        % "9.1.0-play-27",
    "uk.gov.hmrc"           %% "http-caching-client"            % "9.2.0-play-27",
    "uk.gov.hmrc"           %% "play-conditional-form-mapping"  % "1.6.0-play-27",
    "uk.gov.hmrc"           %% "bootstrap-frontend-play-27"     % "3.4.0",
    "uk.gov.hmrc"           %% "tax-year"                       % "1.1.0",
    "uk.gov.hmrc"           %% "play-partials"                  % "7.1.0-play-27",
    "uk.gov.hmrc"           %% "local-template-renderer"        % "2.10.0-play-26",
    "com.typesafe.play"     %% "play-json-joda"                 % "2.9.0",
    "org.scalatra.scalate"  %% "play-scalate"                   % "0.6.0",
    "org.scalatra.scalate"  %% "scalate-core"                   % "1.9.6"
  )


  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"   % "4.0.3",
    "org.pegdown"             %  "pegdown"              % "1.6.0",
    "org.jsoup"               %  "jsoup"                % "1.13.1",
    "com.typesafe.play"       %% "play-test"            % PlayVersion.current,
    "org.mockito"             %  "mockito-all"          % "1.10.19",
    "org.scalacheck"          %% "scalacheck"           % "1.14.3",
    "com.github.tomakehurst"  %  "wiremock-standalone"  % "2.26.3"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
