#!/bin/bash

echo ""
echo "Applying migration FactoryEngineeringList2"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /factoryEngineeringList2                        controllers.FactoryEngineeringList2Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /factoryEngineeringList2                        controllers.FactoryEngineeringList2Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeFactoryEngineeringList2                  controllers.FactoryEngineeringList2Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeFactoryEngineeringList2                  controllers.FactoryEngineeringList2Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "factoryEngineeringList2.title = factoryEngineeringList2" >> ../conf/messages.en
echo "factoryEngineeringList2.heading = factoryEngineeringList2" >> ../conf/messages.en
echo "factoryEngineeringList2.checkYourAnswersLabel = factoryEngineeringList2" >> ../conf/messages.en
echo "factoryEngineeringList2.error.required = Select yes if factoryEngineeringList2" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFactoryEngineeringList2UserAnswersEntry: Arbitrary[(FactoryEngineeringList2Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[FactoryEngineeringList2Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFactoryEngineeringList2Page: Arbitrary[FactoryEngineeringList2Page.type] =";\
    print "    Arbitrary(FactoryEngineeringList2Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(FactoryEngineeringList2Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def factoryEngineeringList2: Option[AnswerRow] = userAnswers.get(FactoryEngineeringList2Page) map {";\
     print "    x => AnswerRow(\"factoryEngineeringList2.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.FactoryEngineeringList2Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration FactoryEngineeringList2 completed"
