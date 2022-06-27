import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"           %% "simple-reactivemongo"           % "8.1.0-play-28",
    "uk.gov.hmrc"           %% "logback-json-logger"            % "5.2.0",
    "uk.gov.hmrc"           %% "govuk-template"                 % "5.77.0-play-28",
    "uk.gov.hmrc"           %% "play-ui"                        % "9.10.0-play-28",
    "uk.gov.hmrc"           %% "http-caching-client"            % "9.6.0-play-28",
    "uk.gov.hmrc"           %% "play-conditional-form-mapping"  % "1.11.0-play-28",
    "uk.gov.hmrc"           %% "bootstrap-frontend-play-28"     % "5.24.0",
    "uk.gov.hmrc"           %% "tax-year"                       % "1.3.0",
    "uk.gov.hmrc"           %% "play-partials"                  % "8.3.0-play-28",
    "uk.gov.hmrc"           %% "local-template-renderer"        % "2.17.0-play-28" excludeAll(ExclusionRule(organization="org.scalactic")),
    "com.typesafe.play"     %% "play-json-joda"                 % "2.9.2",
    "org.scalatra.scalate"  %% "play-scalate"                   % "0.6.0",
    "org.scalatra.scalate"  %% "scalate-core"                   % "1.9.8"
  )


  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"   % "5.1.0",
    "org.scalatestplus"       %% "scalatestplus-mockito"      % "1.0.0-M2",
    "org.scalatestplus"       %% "scalatestplus-scalacheck"   % "3.1.0.0-RC2",
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.35.10",
    "org.pegdown"             %  "pegdown"              % "1.6.0",
    "org.jsoup"               %  "jsoup"                % "1.13.1",
    "com.typesafe.play"       %% "play-test"            % PlayVersion.current,
    "org.mockito"             %  "mockito-all"          % "1.10.19",
    "org.scalacheck"          %% "scalacheck"           % "1.14.3",
    "com.github.tomakehurst"  %  "wiremock-standalone"  % "2.26.3"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
