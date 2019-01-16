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

package controllers

import base.SpecBase
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class RedirectMongoKeyControllerSpec extends SpecBase with ScalaFutures {

  "RedirectMongoKey Controller" must {

    "return OK and redirect to CheckYourAnswers" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.RedirectMongoKeyController.onPageLoad("key", None).url)

      val result = route(application, request).value

      status(result) mustEqual 303

      redirectLocation(result).get mustBe routes.CheckYourAnswersController.onPageLoad().url

      result.map(
        x =>
          x.session(request).get("mongoKey") mustBe "sadfg"
      )

      application.stop()
    }

    "add mongoKey to session" in {

      val application = applicationBuilder(userAnswers = None).build()

      val controller = application.injector.instanceOf[RedirectMongoKeyController]

      val session = await(controller.onPageLoad("key", None)(fakeRequest)).newSession.get

      session.get("mongoKey").get mustBe "key"

      application.stop()
    }
  }

}