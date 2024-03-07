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

package views.mergedJourney

import config.FrontendAppConfig
import models.mergedJourney._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{Tag, Text}
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTag
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.timeline.Event
import uk.gov.hmrc.http.InternalServerException
import views.html.playComponents
import views.mergedJourney.ClaimYourExpensesBuilder._

import javax.inject.{Inject, Singleton}

@Singleton
class ClaimYourExpensesBuilder @Inject()(govukTag: GovukTag,
                                         p: playComponents.p,
                                         appConfig: FrontendAppConfig) {

  def continueUrl(journeyConfig: MergedJourney): String =
    journeyConfig match {
      case journeyConfig if journeyConfig.wfh == ClaimPending =>
        appConfig.startUrlWfh
      case journeyConfig if journeyConfig.psubs == ClaimPending =>
        appConfig.startUrlPsubs
      case journeyConfig if journeyConfig.fre == ClaimPending =>
        appConfig.startUrlFre
      case _ =>
        controllers.mergedJourney.routes.ClaimsCompleteController.show.url
    }

  def events(journeyConfig: MergedJourney)(implicit messages: Messages): Seq[Event] = {
    val journeys = Seq((journeyConfig.wfh, wfhJourney), (journeyConfig.psubs, psubsJourney), (journeyConfig.fre, freJourney))
      .filterNot(_._1 == ClaimSkipped)

    journeys.zipWithIndex.map { case ((status, journey), index) =>
      Event(
        title = messages(s"claimYourExpenses.eventTitle.$journey", index + 1),
        time = "",
        content = HtmlFormat.fill(List(
          eventTag(status, isFirstPending = index == journeys.indexWhere(_._1 == ClaimPending)),
          eventDescription(status, journey, isFirstPending = index == journeys.indexWhere(_._1 == ClaimPending))
        )).body,
      )
    }
  }

  private def eventTag(status: ClaimStatus, isFirstPending: Boolean)(implicit messages: Messages): HtmlFormat.Appendable =
    status match {
      case ClaimPending if isFirstPending =>
        govukTag(Tag(Text(messages("claimYourExpenses.tag.startYourClaim")), classes = "govuk-tag--turquoise govuk-!-margin-bottom-4"))
      case ClaimPending =>
        govukTag(Tag(Text(messages("claimYourExpenses.tag.cannotStartYet")), classes = "govuk-tag--grey govuk-!-margin-bottom-4"))
      case _: ClaimComplete =>
        govukTag(Tag(Text(messages("claimYourExpenses.tag.claimComplete")), classes = "govuk-tag--blue govuk-!-margin-bottom-4"))
      case ClaimStopped =>
        govukTag(Tag(Text(messages("claimYourExpenses.tag.claimStopped")), classes = "govuk-tag--red govuk-!-margin-bottom-4"))
      case ClaimNotChanged =>
        govukTag(Tag(Text(messages("claimYourExpenses.tag.claimNotChanged")), classes = "govuk-tag--red govuk-!-margin-bottom-4"))
      case ClaimUnsuccessful =>
        govukTag(Tag(Text(messages("claimYourExpenses.tag.claimUnsuccessful")), classes = "govuk-tag--red govuk-!-margin-bottom-4"))
      case _ =>
        throw new InternalServerException("[ClaimEventBuilder][eventTag] Attempted to build an impossible case")
    }

  private def eventDescription(status: ClaimStatus, journey: String, isFirstPending: Boolean)(implicit messages: Messages): Html =
    (status, journey) match {
      case (ClaimPending, _) if isFirstPending =>
        p()(Html(messages("claimYourExpenses.status.clickContinue")))
      case (ClaimPending, `psubsJourney`) =>
        p()(Html(messages("claimYourExpenses.status.pending.psubs")))
      case (ClaimPending, `freJourney`) =>
        p()(Html(messages("claimYourExpenses.status.pending.fre")))
      case (_: ClaimComplete, _) =>
        p()(Html(messages("claimYourExpenses.status.claimComplete")))
      case (ClaimStopped, _) =>
        p()(Html(messages("claimYourExpenses.status.claimStopped")))
      case (ClaimNotChanged, `psubsJourney`) =>
        p()(Html(messages("claimYourExpenses.status.claimNotChanged.psubs")))
      case (ClaimNotChanged, `freJourney`) =>
        p()(Html(messages("claimYourExpenses.status.claimNotChanged.fre")))
      case (ClaimUnsuccessful, _) =>
        p()(Html(messages("claimYourExpenses.status.claimUnsuccessful")))
      case _ =>
        throw new InternalServerException("[ClaimEventBuilder][eventDescription] Attempted to build an impossible case")
    }
}

object ClaimYourExpensesBuilder {
  val wfhJourney = "wfh"
  val psubsJourney = "psubs"
  val freJourney = "fre"
}