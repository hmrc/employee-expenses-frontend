import sbt.*

object AppDependencies {

  private val mongoVersion = "2.2.0"
  private val bootstrapVersion = "9.1.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % mongoVersion,
    "uk.gov.hmrc" %% "play-conditional-form-mapping-play-30" % "3.1.0",
    "uk.gov.hmrc" %% "tax-year" % "5.0.0",
    "uk.gov.hmrc" %% "sca-wrapper-play-30" % "1.10.0"
  )


  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoVersion,
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
