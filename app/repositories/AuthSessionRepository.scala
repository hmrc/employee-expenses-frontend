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

import com.mongodb.client.model.Indexes.ascending

import java.time.LocalDateTime
import javax.inject.Inject
import models.{DatedCacheMap, UserAnswers}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{IndexModel, IndexOptions, UpdateOptions, Updates}
import play.api.Configuration
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.MongoComponent

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthSessionRepository @Inject()(config: Configuration, mongo: MongoComponent)
  extends PlayMongoRepository[DatedCacheMap](
    mongoComponent = mongo,
    collectionName = "auth-user-answers",
    domainFormat = DatedCacheMap.formats,
    indexes = Seq(
      IndexModel(
        ascending("_id"),
        IndexOptions()
          .name("_id_")
      ),
      IndexModel(
        ascending("lastUpdated"),
        IndexOptions()
          .name("user-answers-last-updated-index")
          .expireAfter(config.get[Int]("mongodb.timeToLiveInSeconds"), TimeUnit.SECONDS)
      )
    )
  ) {

  def get(id: String): Future[Option[DatedCacheMap]] = collection.find(equal("id", id)).headOption()

  def set(userAnswers: UserAnswers): Future[Boolean] = {
    val selector = equal("_id", userAnswers.id)
    val modifier = Updates.set("lastUpdated", LocalDateTime.now)

    collection.updateOne(
      filter = selector,
      update = Updates.combine(modifier),
      UpdateOptions()
        .upsert(true)
    ).toFuture().map(_.wasAcknowledged())
  }

  def remove(id: String): Future[Option[DatedCacheMap]] = collection.findOneAndDelete(equal("_id", id)).headOption()

}
