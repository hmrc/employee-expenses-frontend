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

package handlers

import play.api.Logging
import play.api.http.Status.FORBIDDEN
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Forbidden
import play.api.mvc.{RequestHeader, Result}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import views.html.ErrorTemplate

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ErrorHandler @Inject() (
    val messagesApi: MessagesApi,
    view: ErrorTemplate
)(implicit val ec: ExecutionContext)
    extends FrontendErrorHandler
    with I18nSupport
    with Logging {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(
      implicit request: RequestHeader
  ): Future[Html] =
    Future.successful(view(pageTitle, heading, message))

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =

    if (statusCode == FORBIDDEN) {

      implicit val implicitRequest: RequestHeader = request

      logger.info(s"Forbidden request with message: $message")

      standardErrorTemplate(
        "technicalDifficulties.pageTitle",
        "technicalDifficulties.heading",
        "technicalDifficulties.message"
      ).map(Forbidden(_))
    } else {
      super.onClientError(request, statusCode, message)
    }

}
