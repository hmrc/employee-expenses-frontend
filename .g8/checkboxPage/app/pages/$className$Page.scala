package pages

import models.$className$
import play.api.libs.json.JsPath

case object $className$Page extends QuestionPage[Seq[$className$]] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$className;format="decap"$"
}
