import sbt.*

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapVersion = "7.22.0"


  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-play-28"             % "1.3.0",
    "uk.gov.hmrc"           %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"           %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "uk.gov.hmrc"           %% "tax-year"                       % "3.3.0",
    "com.typesafe.play"     %% "play-json-joda"                 % "2.9.4",
    "uk.gov.hmrc"           %% "sca-wrapper"                    % "1.0.45"
  )


  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play"             % "5.1.0",
    "org.scalatestplus"       %% "scalatestplus-mockito"          % "1.0.0-M2",
    "org.scalatestplus"       %% "scalatestplus-scalacheck"       % "3.1.0.0-RC2",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"        % "1.3.0",
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"         % bootstrapVersion,
    "com.vladsch.flexmark"    %  "flexmark-all"                   % "0.35.10",
    "org.pegdown"             %  "pegdown"                        % "1.6.0",
    "org.jsoup"               %  "jsoup"                          % "1.16.1",
    "com.typesafe.play"       %% "play-test"                      % PlayVersion.current,
    "org.mockito"             %  "mockito-all"                    % "1.10.19",
    "org.scalacheck"          %% "scalacheck"                     % "1.17.0",
    "com.github.tomakehurst"  %  "wiremock-standalone"            % "2.27.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
