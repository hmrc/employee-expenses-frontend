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

package repositories

import base.SpecBase
import models.mergedJourney.{ClaimPending, MergedJourney}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.test.Helpers._
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext

class MergedJourneySessionRepositorySpec
    extends SpecBase
    with Matchers
    with MockitoSugar
    with ScalaFutures
    with DefaultPlayMongoRepositorySupport[MergedJourney] {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = 30.seconds, interval = 100.millis)

  lazy val repository: MergedJourneySessionRepository = new MergedJourneySessionRepository(
    config = Configuration.from(Map("mongodb.mergedJourneyTimeToLiveInSeconds" -> 60)),
    mongo = mongoComponent
  )

  val testId          = "testId"
  val testDifferentId = "testDifferentId"

  val testData: MergedJourney = MergedJourney(
    testId,
    ClaimPending,
    ClaimPending,
    ClaimPending
  )

  val testDifferentData: MergedJourney = MergedJourney(
    testDifferentId,
    ClaimPending,
    ClaimPending,
    ClaimPending
  )

  "MergedJourneySessionRepository" must {
    "retrieve None when data doesn't exist" in {
      val futureResult = repository.get(testId)
      whenReady(futureResult)(result => result mustBe None)
    }

    "retrieve None when data doesn't exist for the specified id" in {
      await(repository.set(testData))
      val futureResult = repository.get(testDifferentId)
      whenReady(futureResult)(result => result mustBe None)
    }

    "retrieve data with a new timestamp if it's present" in {
      await(repository.set(testData))
      val futureResult = repository.get(testId)
      whenReady(futureResult) { result =>
        result.map(_.internalId) mustBe Some(testId)
        result.map(_.lastUpdated) mustNot be(Some(testData.lastUpdated))
      }
    }

    "retrieve correct data with a nwe timestamp among other entries" in {
      await(repository.set(testData))
      await(repository.set(testDifferentData))
      val futureResult = repository.get(testDifferentId)
      whenReady(futureResult) { result =>
        result.map(_.internalId) mustBe Some(testDifferentId)
        result.map(_.lastUpdated) mustNot be(Some(testDifferentData.lastUpdated))
      }
    }
  }

}
