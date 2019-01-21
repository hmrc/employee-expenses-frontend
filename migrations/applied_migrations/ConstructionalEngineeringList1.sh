#!/bin/bash

echo ""
echo "Applying migration ConstructionalEngineeringList1"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /constructionalEngineeringList1                        controllers.engineering.ConstructionalEngineeringList1Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /constructionalEngineeringList1                        controllers.engineering.ConstructionalEngineeringList1Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeConstructionalEngineeringList1                  controllers.engineering.ConstructionalEngineeringList1Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeConstructionalEngineeringList1                  controllers.engineering.ConstructionalEngineeringList1Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "constructionalEngineeringList1.title = constructionalEngineeringList1" >> ../conf/messages.en
echo "constructionalEngineeringList1.heading = constructionalEngineeringList1" >> ../conf/messages.en
echo "constructionalEngineeringList1.checkYourAnswersLabel = constructionalEngineeringList1" >> ../conf/messages.en
echo "constructionalEngineeringList1.error.required = Select yes if constructionalEngineeringList1" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConstructionalEngineeringList1UserAnswersEntry: Arbitrary[(ConstructionalEngineeringList1Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ConstructionalEngineeringList1Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConstructionalEngineeringList1Page: Arbitrary[ConstructionalEngineeringList1Page.type] =";\
    print "    Arbitrary(ConstructionalEngineeringList1Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ConstructionalEngineeringList1Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def constructionalEngineeringList1: Option[AnswerRow] = userAnswers.get(ConstructionalEngineeringList1Page) map {";\
     print "    x => AnswerRow(\"constructionalEngineeringList1.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.ConstructionalEngineeringList1Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ConstructionalEngineeringList1 completed"
