import sbt.*

object AppDependencies {
  import play.core.PlayVersion

  private val mongoVersion = "1.6.0"


  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-play-28"             % mongoVersion,
    "uk.gov.hmrc"           %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"           %% "tax-year"                       % "3.3.0",
    "com.typesafe.play"     %% "play-json-joda"                 % "2.9.4",
    "uk.gov.hmrc"           %% "sca-wrapper-play-28"            % "1.1.0"
  )


  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"             % "5.1.0",
    "org.scalatestplus"       %% "scalatestplus-mockito"          % "1.0.0-M2",
    "org.scalatestplus"       %% "scalatestplus-scalacheck"       % "3.1.0.0-RC2",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"        % "1.6.0",
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"         % "7.23.0",
    "com.vladsch.flexmark"    %  "flexmark-all"                   % "0.35.10",
    "org.pegdown"             %  "pegdown"                        % mongoVersion,
    "org.jsoup"               %  "jsoup"                          % "1.17.1",
    "com.typesafe.play"       %% "play-test"                      % PlayVersion.current,
    "org.mockito"             %  "mockito-all"                    % "1.10.19",
    "org.scalacheck"          %% "scalacheck"                     % "1.17.0",
    "org.wiremock"            %  "wiremock-standalone"            % "3.0.1",
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
