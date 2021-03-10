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

package controllers.authenticated

import controllers.actions._
import javax.inject.Inject
import models.auditing.AuditData
import models.auditing.AuditEventType.NoCodeChange
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.authenticated.NoCodeChangeView

import scala.concurrent.ExecutionContext

class NoCodeChangeController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        identify: AuthenticatedIdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        auditConnector: AuditConnector,
                                        view: NoCodeChangeView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      auditConnector.sendExplicitAudit(
        NoCodeChange.toString,
        AuditData(nino = request.nino.get, userAnswers = request.userAnswers.data)
      )

      Ok(view())
  }
}
