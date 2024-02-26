/*
 * Copyright 2024 HM Revenue & Customs
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
import connectors.CitizenDetailsConnector
import controllers.actions.{Authed, MergedJourneyIdentifierAction}
import controllers.routes
import models.Address
import models.mergedJourney._
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.mergedJourney.ClaimsCompleteView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off cyclomatic.complexity
@Singleton
class ClaimsCompleteController @Inject()(identify: MergedJourneyIdentifierAction,
                                         sessionRepository: SessionRepository,
                                         val controllerComponents: MessagesControllerComponents,
                                         claimsCompleteView: ClaimsCompleteView,
                                         citizenDetailsConnector: CitizenDetailsConnector
                                        )(implicit val ec: ExecutionContext,
                                          appConfig: FrontendAppConfig)
  extends FrontendBaseController with I18nSupport with Logging {

  def show: Action[AnyContent] = identify.async { implicit request =>
    if (appConfig.mergedJourneyEnabled) {
      request.identifier match {
        case id: Authed =>
          sessionRepository.getMergedJourney(id.internalId).flatMap {
            case Some(journeyConfig) if !journeyConfig.claimList.contains(ClaimPending) &&
              journeyConfig.claimList.exists(Seq(ClaimCompleteCurrent, ClaimCompleteCurrentPrevious, ClaimCompletePrevious).contains) =>
              //This handles the case where at least one of the journeys has been complete and none are pending
              citizenDetailsConnector.getAddress(request.nino.get).map { response =>
                Ok(claimsCompleteView(journeyConfig, Json.parse(response.body).validate[Address].asOpt))
              }.recoverWith { //Should never happen as this same address check is made in all 3 journeys and failure would prevent users reaching this
                case e =>
                  Future.successful(Ok(claimsCompleteView(journeyConfig, None)))
              }
            case Some(journeyConfig) if !journeyConfig.claimList.contains(ClaimPending) =>
              //TODO: This case should handle merged journey that is finished but did not result in claims being made
              ???
            case Some(_) =>
              logger.warn(s"[ClaimsCompleteController][claimsComplete] Some claims are still pending," +
                s" returning to Claim Expenses page. Session: ${hc.sessionId.toString}")
              Future.successful(Redirect(controllers.mergedJourney.routes.ClaimYourExpensesController.show))
            case _ =>
              logger.warn(s"[ClaimsCompleteController][claimsComplete] Missing journey config," +
                s" returning to Eligibility Checker. Session: ${hc.sessionId.toString}")
              Future.successful(Redirect(appConfig.eligibilityCheckerUrl))
          }
        case _ => //Should never happen
          Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad))
      }
    } else {
      Future.successful(NotImplemented)
    }
  }

}
