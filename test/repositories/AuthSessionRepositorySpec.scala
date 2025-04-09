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
import controllers.actions.Authed
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.{Application, Configuration}
import uk.gov.hmrc.mongo.CurrentTimestampSupport
import uk.gov.hmrc.mongo.cache.{CacheItem, MongoCacheRepository}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext

class AuthSessionRepositorySpec
    extends SpecBase
    with Matchers
    with MockitoSugar
    with ScalaFutures
    with DefaultPlayMongoRepositorySupport[CacheItem] {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  val authSessionRepo = new AuthSessionRepository(
    config = Configuration.from(Map("mongodb.timeToLiveInSeconds" -> 60)),
    mongo = mongoComponent,
    timestampSupport = new CurrentTimestampSupport()
  )

  override lazy val repository: MongoCacheRepository[String] = authSessionRepo.cacheRepo

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[AuthSessionRepository].toInstance(authSessionRepo)
    )
    .build()

  val authId           = Authed("auth-id")
  val otherAuthId      = Authed("other-auth-id")
  val userAnswers      = currentYearMinus1UserAnswers
  val otherUserAnswers = currentYearFullUserAnswers

  "AuthSessionRepository" must {

    "retrieve None when user answers don't exist" in {
      val futureResult = authSessionRepo.get(authId.internalId)
      whenReady(futureResult)(result => result mustBe None)
    }

    "retrieve None when user answers don't exist for the specified user id" in {
      await(authSessionRepo.set(otherAuthId.internalId, otherUserAnswers))
      val futureResult = authSessionRepo.get(authId.internalId)
      whenReady(futureResult)(result => result mustBe None)
    }

    "retrieve user answers if they're present" in {
      await(authSessionRepo.set(authId.internalId, userAnswers))
      val futureResult = authSessionRepo.get(authId.internalId)
      whenReady(futureResult)(result => result mustBe Some(userAnswers))
    }

    "retrieve correct user answers among other entries" in {
      await(authSessionRepo.set(otherAuthId.internalId, otherUserAnswers))
      await(authSessionRepo.set(authId.internalId, userAnswers))
      val futureResult = authSessionRepo.get(authId.internalId)
      whenReady(futureResult)(result => result mustBe Some(userAnswers))

    }

  }

}
