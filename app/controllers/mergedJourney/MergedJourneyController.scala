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
import connectors.EmployeeWfhExpensesConnector
import controllers.actions.{Authed, MergedJourneyIdentifierAction}
import controllers.mergedJourney.MergedJourneyController._
import controllers.routes
import models.mergedJourney._
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off cyclomatic.complexity
@Singleton
class MergedJourneyController @Inject()(identify: MergedJourneyIdentifierAction,
                                        sessionRepository: SessionRepository,
                                        val controllerComponents: MessagesControllerComponents,
                                        employeeWfhExpensesConnector: EmployeeWfhExpensesConnector,
                                        appConfig: FrontendAppConfig
                                       )(implicit val ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport with Logging {

  // This route is used by the eligibility checker to initiate a journey to claim multiple expenses
  def setupMergedJourney(wfh: Boolean, psubs: Boolean, fre: Boolean): Action[AnyContent] = identify.async { implicit request =>
    if (appConfig.mergedJourneyEnabled) {
      request.identifier match {
        case id: Authed if List(wfh, psubs, fre).count(_ == true) > 1 =>
          for {
            wfhClaimedAlready <- if (wfh) employeeWfhExpensesConnector.checkIfAllYearsClaimed(hc) else Future.successful(false)
            _ <- if (!wfhClaimedAlready) {
              sessionRepository.setMergedJourney(
                MergedJourney(
                  internalId = id.internalId,
                  wfh = if (wfh) ClaimPending else ClaimSkipped,
                  psubs = if (psubs) ClaimPending else ClaimSkipped,
                  fre = if (fre) ClaimPending else ClaimSkipped
                )
              )
            } else {
              Future.successful()
            }
            url = if (wfhClaimedAlready) appConfig.p87ClaimOnlineUrl else controllers.mergedJourney.routes.ClaimYourExpensesController.show.url
          } yield Redirect(url)
        case _ =>
          logger.warn(s"[MergedJourneyController][setupMergedJourney] Invalid journey flags provided," +
            s" returning to Eligibility Checker. Session: ${hc.sessionId.toString}")
          Future.successful(Redirect(appConfig.eligibilityCheckerUrl))
      }
    } else {
      Future.successful(NotImplemented)
    }
  }

  //This route is used at the end of each individual claim within the merged journey or on kickouts
  def mergedJourneyContinue(journey: String, state: ClaimStatus): Action[AnyContent] = identify.async { implicit request =>
    if (appConfig.mergedJourneyEnabled) {
      request.identifier match {
        case id: Authed =>
          sessionRepository.getMergedJourney(id.internalId).flatMap {
            case Some(journeyConfig) =>
              val updatedJourney = journey match {
                case `wfhJourney` if journeyConfig.wfh != ClaimSkipped => journeyConfig.copy(wfh = state)
                case `psubsJourney` if journeyConfig.psubs != ClaimSkipped => journeyConfig.copy(psubs = state)
                case `freJourney` if journeyConfig.fre != ClaimSkipped => journeyConfig.copy(fre = state)
                case _ => throw new InternalServerException("[MergedJourneyController][mergedJourneyContinue] Unsupported journey continue call")
              }
              sessionRepository.setMergedJourney(updatedJourney)
                .map(_ => Redirect(controllers.mergedJourney.routes.ClaimYourExpensesController.show))
            case None =>
              Future.successful(Redirect(appConfig.eligibilityCheckerUrl))
          }
        case _ => //Should never happen
          Future.successful(Redirect(routes.TechnicalDifficultiesController.onPageLoad))
      }
    } else {
      Future.successful(NotImplemented)
    }
  }

  def mergedJourneyRefreshSession: Action[AnyContent] = identify.async { implicit request =>
    request.identifier match {
      case id: Authed => sessionRepository.updateMergedJourneyTimeToLive(id).map {
        case true => Ok("OK")
        case _ => NotFound
      }
      case _ => Future.successful(InternalServerError)
    }
  }
}

object MergedJourneyController {
  val wfhJourney = "wfh"
  val psubsJourney = "psubs"
  val freJourney = "fre"
}