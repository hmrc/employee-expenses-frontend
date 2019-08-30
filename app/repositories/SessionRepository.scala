/*
 * Copyright 2019 HM Revenue & Customs
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

import java.time.LocalDateTime

import akka.stream.Materializer
import controllers.actions.{Authed, IdentifierType, UnAuthed}
import javax.inject.Inject
import models.UserAnswers
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

class SessionRepository @Inject()(
                                   mongo: ReactiveMongoApi,
                                   config: Configuration
                                 )(implicit ec: ExecutionContext, m: Materializer) {


  private val unauthCollectionName: String = "unauth-user-answers"
  private val authCollectionName: String = "auth-user-answers"

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private def collection(collectionName: String): Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val lastUpdatedIndex = Index(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = Some("user-answers-last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  val started: Future[Unit] =
    collection(unauthCollectionName).flatMap {
      _.indexesManager.ensure(lastUpdatedIndex)
    }.flatMap {
      _ =>
        collection(authCollectionName).flatMap {
          _.indexesManager.ensure(lastUpdatedIndex)
        }
    }.map(_ => ())

  def get(identifierType: IdentifierType): Future[Option[UserAnswers]] = {
    identifierType match {
      case id: Authed => collection(authCollectionName).flatMap(_.find(Json.obj("_id" -> id.internalId), None).one[UserAnswers])
      case id: UnAuthed => collection(unauthCollectionName).flatMap(_.find(Json.obj("_id" -> id.sessionId), None).one[UserAnswers])
    }
  }

  def set(identifierType: IdentifierType, userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now))
    )

    identifierType match {
      case _: Authed =>
        collection(authCollectionName).flatMap {
          _.update(selector, modifier, upsert = true).map {
            lastError =>
              lastError.ok
          }
        }
      case _: UnAuthed =>
        collection(unauthCollectionName).flatMap {
          _.update(selector, modifier, upsert = true).map {
            lastError =>
              lastError.ok
          }
        }
    }
  }

  def remove(identifierType: IdentifierType): Future[Option[UserAnswers]] = {
    identifierType match {
      case id: Authed => collection(authCollectionName).flatMap(_.findAndRemove(Json.obj("_id" -> id.internalId)).map(_.result[UserAnswers]))
      case id: UnAuthed => collection(unauthCollectionName).flatMap(_.findAndRemove(Json.obj("_id" -> id.sessionId)).map(_.result[UserAnswers]))
    }
  }

  def updateTimeToLive(id: Authed): Future[Boolean] = {
    get(id).flatMap {
      case Some(ua) => set(id, ua)
      case _ => throw new Exception(s"UserAnswers not found")
    }
  }

}
