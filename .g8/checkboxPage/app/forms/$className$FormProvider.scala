package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms.seq
import models.$className$

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[Seq[$className$]] =
    Form(
      "value" -> seq(enumerable[$className$]("$className;format="decap"$.error.required")).verifying(nonEmptySeq("$className;format="decap"$.error.required"))
    )
}
