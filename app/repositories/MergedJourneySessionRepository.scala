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

import models.mergedJourney.MergedJourney
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import play.api.Configuration
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.SECONDS
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MergedJourneySessionRepository @Inject()(config: Configuration,
                                               mongo: MongoComponent)
                                              (implicit ec: ExecutionContext)
  extends PlayMongoRepository[MergedJourney](
    collectionName = "merged-journey-user-answers",
    mongoComponent = mongo,
    domainFormat = MergedJourney.format,
    indexes = Seq(
      IndexModel(
        ascending("lastUpdated"),
        IndexOptions()
          .name("TTL")
          .expireAfter(config.get[Int]("mongodb.mergedJourneyTimeToLiveInSeconds"), SECONDS)
      ),
      IndexModel(
        keys = ascending("internalId"),
        indexOptions = IndexOptions()
          .name("intId")
          .unique(true)
      )
    ),
    replaceIndexes = true
  ) {

  def get(id: String): Future[Option[MergedJourney]] =
    collection.find[MergedJourney](and(equal("internalId", id))).headOption()

  def set(mergedJourney: MergedJourney): Future[Boolean] =
    collection.replaceOne(
      filter = equal("internalId", mergedJourney.internalId),
      replacement = mergedJourney.copy(lastUpdated = Instant.now()),
      options = ReplaceOptions().upsert(true)
    ).toFuture().map(_.wasAcknowledged())

}