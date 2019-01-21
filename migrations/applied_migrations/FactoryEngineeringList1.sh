#!/bin/bash

echo ""
echo "Applying migration FactoryEngineeringList1"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /factoryEngineeringList1                        controllers.engineering.FactoryEngineeringList1Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /factoryEngineeringList1                        controllers.engineering.FactoryEngineeringList1Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeFactoryEngineeringList1                  controllers.engineering.FactoryEngineeringList1Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeFactoryEngineeringList1                  controllers.engineering.FactoryEngineeringList1Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "factoryEngineeringList1.title = factoryEngineeringList1" >> ../conf/messages.en
echo "factoryEngineeringList1.heading = factoryEngineeringList1" >> ../conf/messages.en
echo "factoryEngineeringList1.checkYourAnswersLabel = factoryEngineeringList1" >> ../conf/messages.en
echo "factoryEngineeringList1.error.required = Select yes if factoryEngineeringList1" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFactoryEngineeringList1UserAnswersEntry: Arbitrary[(FactoryEngineeringList1Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[FactoryEngineeringList1Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFactoryEngineeringList1Page: Arbitrary[FactoryEngineeringList1Page.type] =";\
    print "    Arbitrary(FactoryEngineeringList1Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(FactoryEngineeringList1Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def factoryEngineeringList1: Option[AnswerRow] = userAnswers.get(FactoryEngineeringList1Page) map {";\
     print "    x => AnswerRow(\"factoryEngineeringList1.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.FactoryEngineeringList1Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration FactoryEngineeringList1 completed"
