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
import models.NormalMode
import controllers.routes._
import controllers.authenticated.routes._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class RedirectMongoKeyControllerSpec extends SpecBase with ScalaFutures with IntegrationPatience {

  "RedirectMongoKey Controller" must {

    "redirect to CheckYourAnswers" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, RedirectMongoKeyController.onPageLoad("key", None).url)

      val result = route(application, request).value

      status(result) mustEqual 303

      redirectLocation(result).get mustBe TaxYearSelectionController.onPageLoad(NormalMode).url

      application.stop()
    }

    "add mongoKey to session" in {

      val application = applicationBuilder(userAnswers = None).build()

      val controller = application.injector.instanceOf[RedirectMongoKeyController]

      val session = await(controller.onPageLoad("key", None)(fakeRequest)).newSession.get

      session.get(frontendAppConfig.mongoKey).get mustBe "key"

      application.stop()
    }
  }

}
