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

package controllers.mergedJourney

import config.FrontendAppConfig
import controllers.actions.{Authed, MergedJourneyIdentifierAction}
import controllers.routes
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.mergedJourney.ClaimYourExpensesView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off cyclomatic.complexity
@Singleton
class ClaimYourExpensesController @Inject() (
    identify: MergedJourneyIdentifierAction,
    sessionRepository: SessionRepository,
    val controllerComponents: MessagesControllerComponents,
    claimYourExpensesView: ClaimYourExpensesView,
    appConfig: FrontendAppConfig
)(implicit val ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = identify.async { implicit request =>
    if (appConfig.mergedJourneyEnabled) {
      request.identifier match {
        case id: Authed =>
          sessionRepository.getMergedJourney(id.internalId).map {
            case Some(journeyConfig) =>
              Ok(claimYourExpensesView(journeyConfig))
            case _ =>
              logger.warn(
                s"[ClaimYourExpensesController][claimYourExpenses] Missing journey config," +
                  s" returning to Eligibility Checker. Session: ${hc.sessionId.toString}"
              )
              Redirect(appConfig.eligibilityCheckerUrl)
          }
        case _ => // Should never happen
          Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad))
      }
    } else {
      Future.successful(NotImplemented)
    }
  }

}
