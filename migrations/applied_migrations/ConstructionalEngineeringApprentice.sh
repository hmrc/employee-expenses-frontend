#!/bin/bash

echo ""
echo "Applying migration ConstructionalEngineeringApprentice"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /constructionalEngineeringApprentice                        controllers.engineering.ConstructionalEngineeringApprenticeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /constructionalEngineeringApprentice                        controllers.engineering.ConstructionalEngineeringApprenticeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeConstructionalEngineeringApprentice                  controllers.engineering.ConstructionalEngineeringApprenticeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeConstructionalEngineeringApprentice                  controllers.engineering.ConstructionalEngineeringApprenticeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "constructionalEngineeringApprentice.title = constructionalEngineeringApprentice" >> ../conf/messages.en
echo "constructionalEngineeringApprentice.heading = constructionalEngineeringApprentice" >> ../conf/messages.en
echo "constructionalEngineeringApprentice.checkYourAnswersLabel = constructionalEngineeringApprentice" >> ../conf/messages.en
echo "constructionalEngineeringApprentice.error.required = Select yes if constructionalEngineeringApprentice" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConstructionalEngineeringApprenticeUserAnswersEntry: Arbitrary[(ConstructionalEngineeringApprenticePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ConstructionalEngineeringApprenticePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConstructionalEngineeringApprenticePage: Arbitrary[ConstructionalEngineeringApprenticePage.type] =";\
    print "    Arbitrary(ConstructionalEngineeringApprenticePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ConstructionalEngineeringApprenticePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def constructionalEngineeringApprentice: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringApprenticePage) map {";\
     print "    x => AnswerRow(\"constructionalEngineeringApprentice.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ConstructionalEngineeringApprenticeController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ConstructionalEngineeringApprentice completed"
