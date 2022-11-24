/*
 * Copyright 2022 HM Revenue & Customs
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

import javax.inject.Inject
import models.UserAnswers
import play.api.Configuration
import play.api.libs.json.Format
import uk.gov.hmrc.mongo.{MongoComponent, TimestampSupport}
import uk.gov.hmrc.mongo.cache.{CacheIdType, EntityCache, MongoCacheRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, SECONDS}

abstract class UserAnswersCache @Inject()(collectionName: String, config: Configuration, mongo: MongoComponent, timestampSupport: TimestampSupport)
  extends EntityCache[String, UserAnswers]{

  lazy val format: Format[UserAnswers] = UserAnswers.format
  lazy val cacheRepo: MongoCacheRepository[String] = new MongoCacheRepository(
    mongoComponent   = mongo,
    collectionName   = collectionName,
    ttl              = Duration(config.get[Int]("mongodb.timeToLiveInSeconds"), SECONDS),
    timestampSupport = timestampSupport,
    cacheIdType      = CacheIdType.SimpleCacheId,
    replaceIndexes   = false
  )

  def get(id: String): Future[Option[UserAnswers]] =
    getFromCache(id)

  def set(id: String, userAnswers: UserAnswers): Future[Boolean] = {
    putCache(id)(userAnswers).map(_ => true)
  }

  def remove(id: String): Future[Unit] =
    deleteFromCache(id)
}
