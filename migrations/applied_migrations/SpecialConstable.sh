#!/bin/bash

echo ""
echo "Applying migration SpecialConstable"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /specialConstable                        controllers.police.SpecialConstableController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /specialConstable                        controllers.police.SpecialConstableController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSpecialConstable                  controllers.police.SpecialConstableController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSpecialConstable                  controllers.police.SpecialConstableController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "specialConstable.title = specialConstable" >> ../conf/messages.en
echo "specialConstable.heading = specialConstable" >> ../conf/messages.en
echo "specialConstable.checkYourAnswersLabel = specialConstable" >> ../conf/messages.en
echo "specialConstable.error.required = Select yes if specialConstable" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySpecialConstableUserAnswersEntry: Arbitrary[(SpecialConstablePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SpecialConstablePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySpecialConstablePage: Arbitrary[SpecialConstablePage.type] =";\
    print "    Arbitrary(SpecialConstablePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SpecialConstablePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def specialConstable: Option[AnswerRow] = userAnswers.get(SpecialConstablePage) map {";\
     print "    x => AnswerRow(\"specialConstable.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.SpecialConstableController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SpecialConstable completed"
