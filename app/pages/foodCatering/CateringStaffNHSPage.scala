package pages.foodCatering

import pages.QuestionPage
import play.api.libs.json.JsPath

case object CateringStaffNHSPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "cateringStaffNHS"
}
