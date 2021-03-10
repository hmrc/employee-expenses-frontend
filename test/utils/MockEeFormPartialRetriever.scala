/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import com.google.inject.Inject
import config.EeFormPartialRetriever
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.filters.frontend.crypto.SessionCookieCrypto
import uk.gov.hmrc.play.bootstrap.http.HttpClient

class MockEeFormPartialRetriever @Inject()(httpGet: HttpClient, sessionCookieCrypto: SessionCookieCrypto)
  extends EeFormPartialRetriever(httpGet, sessionCookieCrypto) with MockitoSugar {

  override def getPartialContent(url: String, templateParameters: Map[String, String], errorMessage: Html)(implicit request: RequestHeader): Html = {
    Html("")
  }
}
