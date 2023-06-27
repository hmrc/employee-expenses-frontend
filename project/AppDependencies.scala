import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-play-28"             % "1.1.0",
    "uk.gov.hmrc"           %% "http-caching-client"            % "10.0.0-play-28",
    "uk.gov.hmrc"           %% "play-conditional-form-mapping"  % "1.12.0-play-28",
    "uk.gov.hmrc"           %% "bootstrap-frontend-play-28"     % "7.14.0",
    "uk.gov.hmrc"           %% "tax-year"                       % "3.0.0",
    "com.typesafe.play"     %% "play-json-joda"                 % "2.9.4",
    "uk.gov.hmrc"           %% "sca-wrapper"                    % "1.0.37"
  )


  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"             % "5.1.0",
    "org.scalatestplus"       %% "scalatestplus-mockito"          % "1.0.0-M2",
    "org.scalatestplus"       %% "scalatestplus-scalacheck"       % "3.1.0.0-RC2",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"        % "1.1.0",
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"         % "7.14.0",
    "com.vladsch.flexmark"    %  "flexmark-all"                   % "0.35.10",
    "org.pegdown"             %  "pegdown"                        % "1.6.0",
    "org.jsoup"               %  "jsoup"                          % "1.13.1",
    "com.typesafe.play"       %% "play-test"                      % PlayVersion.current,
    "org.mockito"             %  "mockito-all"                    % "1.10.19",
    "org.scalacheck"          %% "scalacheck"                     % "1.14.3",
    "com.github.tomakehurst"  %  "wiremock-standalone"            % "2.26.3"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
