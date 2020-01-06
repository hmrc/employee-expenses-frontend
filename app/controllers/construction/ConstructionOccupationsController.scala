/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.construction

import config.{ClaimAmounts, NavConstant}
import controllers.actions._
import forms.construction.ConstructionOccupationsFormProvider
import javax.inject.{Inject, Named}
import models.ConstructionOccupations.{AsphaltOrCement, BuildingMaterials, JoinerOrCarpenter, LabourerOrNavvy, NoneOfAbove, RoofingFelt, StoneMason, Tilemaker}
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.ClaimAmount
import pages.construction.ConstructionOccupationsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.construction.ConstructionOccupationsView

import scala.concurrent.{ExecutionContext, Future}

class ConstructionOccupationsController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   sessionRepository: SessionRepository,
                                                   @Named(NavConstant.construction) navigator: Navigator,
                                                   identify: UnauthenticatedIdentifierAction,
                                                   getData: DataRetrievalAction,
                                                   requireData: DataRequiredAction,
                                                   formProvider: ConstructionOccupationsFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: ConstructionOccupationsView
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ConstructionOccupationsPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ConstructionOccupationsPage, value)
              .flatMap {
                ua =>
                  value match {
                    case JoinerOrCarpenter => ua.set(ClaimAmount, ClaimAmounts.Construction.joinersCarpenters)
                    case StoneMason => ua.set(ClaimAmount, ClaimAmounts.Construction.stoneMasons)
                    case AsphaltOrCement => ua.set(ClaimAmount, ClaimAmounts.Construction.asphaltCement)
                    case RoofingFelt => ua.set(ClaimAmount, ClaimAmounts.Construction.roofingFelt)
                    case LabourerOrNavvy => ua.set(ClaimAmount, ClaimAmounts.Construction.labourerNavvy)
                    case Tilemaker => ua.set(ClaimAmount, ClaimAmounts.Construction.tileMaker)
                    case BuildingMaterials => ua.set(ClaimAmount, ClaimAmounts.Construction.buildingMaterials)
                    case NoneOfAbove => ua.set(ClaimAmount, ClaimAmounts.Construction.allOther)
                  }
              })
            _ <- sessionRepository.set(request.identifier, updatedAnswers)
          } yield Redirect(navigator.nextPage(ConstructionOccupationsPage, mode)(updatedAnswers))
        }
      )
  }
}
