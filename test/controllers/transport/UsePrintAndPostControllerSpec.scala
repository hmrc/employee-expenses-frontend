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

package controllers.transport

import base.SpecBase
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.transport.UseIformFreOnlyView
import play.api.i18n.Messages
import play.api.mvc.Request
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatestplus.mockito.MockitoSugar.mock
import scala.concurrent.ExecutionContext.Implicits.global



class UsePrintAndPostControllerSpec extends SpecBase with BeforeAndAfterEach with BeforeAndAfterAll {

  private val useIformFreOnlyView = mock[UseIformFreOnlyView]


  private  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[UseIformFreOnlyView].toInstance(useIformFreOnlyView)
        )
        .build()

      override def beforeEach(): Unit = {
        super.beforeEach()

        reset(
          useIformFreOnlyView
        )
        when(useIformFreOnlyView.apply()(any[Request[_]], any[Messages])).thenReturn(HtmlFormat.empty)
      }

  override def afterAll(): Unit = {
        application.stop()
        super.afterAll()
      }
  "UsePrintAndPostController" must {

      def usePrintAndPostRoute = routes.UsePrintAndPostController.onPageLoad().url

      def testRequest = FakeRequest(GET, usePrintAndPostRoute)

      "return OK and the correct view for a GET" in  {

        for {
          result <- route(application, testRequest).value
          _ = result.header.status mustBe OK
          _ = verify(useIformFreOnlyView).apply()(any[Request[_]], any[Messages])
        } yield ()
      }

    }
  }
