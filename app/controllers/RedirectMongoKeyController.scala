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

package controllers

import com.google.inject.{Inject, Singleton}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

@Singleton
class RedirectMongoKeyController @Inject()(
                                            identify: IdentifierAction,
                                            val controllerComponents: MessagesControllerComponents
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController {

  def onPageLoad(key: String, journeyId: Option[String]): Action[AnyContent] = identify {
    implicit request =>
      println(s"\n\n\n\n\nBEFORE: ${request.session.data}\n\n\n\n\n")
      println(s"\n\n\n\n\nKEY: $key\n\n\n\n\n")
      println(s"\n\n\n\n\nURI: ${request.uri}\n\n\n\n\n")

      Redirect(routes.CheckYourAnswersController.onPageLoad())
        .withSession(request.session + ("mongoKey"-> key))

  }
}
