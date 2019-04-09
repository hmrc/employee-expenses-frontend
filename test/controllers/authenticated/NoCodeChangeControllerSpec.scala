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

package controllers.authenticated

import base.SpecBase
import models.auditing.AuditData
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito.{times, verify}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.JsObject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import views.html.authenticated.NoCodeChangeView

class NoCodeChangeControllerSpec extends SpecBase with MockitoSugar with ScalaFutures{

  "NoCodeChange Controller" must {

    "return OK and the correct view for a GET" in {

      val mockAuditConnector = mock[AuditConnector]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AuditConnector].toInstance(mockAuditConnector))
        .build()

      val request = FakeRequest(GET, routes.NoCodeChangeController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NoCodeChangeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      whenReady(result) {
        _ =>

          val captor = ArgumentCaptor.forClass(classOf[AuditData])

          verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("noCodeChange"), captor.capture())(any(), any(), any())

          val auditData = captor.getValue

          auditData.nino mustEqual fakeNino
          auditData.userAnswers mustEqual emptyUserAnswers.data
          auditData.userAnswers mustBe a[JsObject]
      }

      application.stop()
    }

  }
}
