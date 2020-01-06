/*
 * Copyright 2020 HM Revenue & Customs
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

package filters

import java.util.UUID

import akka.actor
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import base.SpecBase
import com.google.inject.Inject
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc._
import play.api.routing._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderNames, SessionKeys}

import scala.concurrent.ExecutionContext

object SessionIdFilterSpec {

  val sessionId = "28836767-a008-46be-ac18-695ab140e705"

  class TestSessionIdFilter @Inject()(
                                       override val mat: Materializer,
                                       sessionCookieBaker: SessionCookieBaker,
                                       ec: ExecutionContext
                                     ) extends SessionIdFilter(mat, UUID.fromString(sessionId), sessionCookieBaker, ec)

}

class SessionIdFilterSpec extends SpecBase {

  import scala.concurrent.ExecutionContext.Implicits.global
  import SessionIdFilterSpec._

  private val ActorSystem: ActorSystem = actor.ActorSystem("unit-testing")
  implicit val Mat: ActorMaterializer = ActorMaterializer()(ActorSystem)
  private val Action: DefaultActionBuilder = DefaultActionBuilder(PlayBodyParsers().anyContent)

  val router: Router = {

    import play.api.routing.sird._

    Router.from {
      case GET(p"/test") => Action {
        request =>
          val fromHeader = request.headers.get(HeaderNames.xSessionId).getOrElse("")
          val fromSession = request.session.get(SessionKeys.sessionId).getOrElse("")
          Results.Ok(
            Json.obj(
              "fromHeader" -> fromHeader,
              "fromSession" -> fromSession
            )
          )
      }
      case GET(p"/test2") => Action {
        implicit request =>
          Results.Ok.addingToSession("foo" -> "bar")
      }
    }
  }

  override lazy val app: Application = {

    import play.api.inject._

    new GuiceApplicationBuilder()
      .overrides(
        bind[SessionIdFilter].to[TestSessionIdFilter]
      )
      .configure(
        "play.filters.disabled" -> List("uk.gov.hmrc.play.bootstrap.filters.frontend.crypto.SessionCookieCryptoFilter")
      )
      .router(router)
      .build()
  }

  "session id filter" must {

    "add a sessionId if one doesn't already exist" in {

      val result = route(app, FakeRequest(GET, "/test")).value

      val body = contentAsJson(result)

      (body \ "fromHeader").as[String] mustEqual s"session-$sessionId"
      (body \ "fromSession").as[String] mustEqual s"session-$sessionId"

      session(result).data.get(SessionKeys.sessionId) mustBe defined
    }

    "not override a sessionId if one doesn't already exist" in {

      val result = route(app, FakeRequest(GET, "/test").withSession(SessionKeys.sessionId -> "foo")).value

      val body = contentAsJson(result)

      (body \ "fromHeader").as[String] mustEqual ""
      (body \ "fromSession").as[String] mustEqual "foo"
    }

    "not override other session values from the response" in {

      val result = route(app, FakeRequest(GET, "/test2")).value

      session(result).data must contain("foo" -> "bar")
    }

    "not override other session values from the request" in {

      val result = route(app, FakeRequest(GET, "/test").withSession("foo" -> "bar")).value
      session(result).data must contain("foo" -> "bar")
    }
  }
}
