#!/bin/bash

echo ""
echo "Applying migration ConstructionalEngineeringList2"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /constructionalEngineeringList2                        controllers.ConstructionalEngineeringList2Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /constructionalEngineeringList2                        controllers.ConstructionalEngineeringList2Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeConstructionalEngineeringList2                  controllers.ConstructionalEngineeringList2Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeConstructionalEngineeringList2                  controllers.ConstructionalEngineeringList2Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "constructionalEngineeringList2.title = constructionalEngineeringList2" >> ../conf/messages.en
echo "constructionalEngineeringList2.heading = constructionalEngineeringList2" >> ../conf/messages.en
echo "constructionalEngineeringList2.checkYourAnswersLabel = constructionalEngineeringList2" >> ../conf/messages.en
echo "constructionalEngineeringList2.error.required = Select yes if constructionalEngineeringList2" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConstructionalEngineeringList2UserAnswersEntry: Arbitrary[(ConstructionalEngineeringList2Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ConstructionalEngineeringList2Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConstructionalEngineeringList2Page: Arbitrary[ConstructionalEngineeringList2Page.type] =";\
    print "    Arbitrary(ConstructionalEngineeringList2Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ConstructionalEngineeringList2Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def constructionalEngineeringList2: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList2Page) map {";\
     print "    x => AnswerRow(\"constructionalEngineeringList2.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ConstructionalEngineeringList2Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ConstructionalEngineeringList2 completed"
