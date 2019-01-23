#!/bin/bash

echo ""
echo "Applying migration FactoryEngineeringApprentice"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /factoryEngineeringApprentice                        controllers.FactoryEngineeringApprenticeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /factoryEngineeringApprentice                        controllers.FactoryEngineeringApprenticeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeFactoryEngineeringApprentice                  controllers.FactoryEngineeringApprenticeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeFactoryEngineeringApprentice                  controllers.FactoryEngineeringApprenticeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "factoryEngineeringApprentice.title = factoryEngineeringApprentice" >> ../conf/messages.en
echo "factoryEngineeringApprentice.heading = factoryEngineeringApprentice" >> ../conf/messages.en
echo "factoryEngineeringApprentice.checkYourAnswersLabel = factoryEngineeringApprentice" >> ../conf/messages.en
echo "factoryEngineeringApprentice.error.required = Select yes if factoryEngineeringApprentice" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFactoryEngineeringApprenticeUserAnswersEntry: Arbitrary[(FactoryEngineeringApprenticePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[FactoryEngineeringApprenticePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFactoryEngineeringApprenticePage: Arbitrary[FactoryEngineeringApprenticePage.type] =";\
    print "    Arbitrary(FactoryEngineeringApprenticePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(FactoryEngineeringApprenticePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def factoryEngineeringApprentice: Option[AnswerRow] = userAnswers.get(FactoryEngineeringApprenticePage) map {";\
     print "    x => AnswerRow(\"factoryEngineeringApprentice.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.FactoryEngineeringApprenticeController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration FactoryEngineeringApprentice completed"
