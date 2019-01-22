#!/bin/bash

echo ""
echo "Applying migration TypeOfEngineering"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /typeOfEngineering                        controllers.engineering.TypeOfEngineeringController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /typeOfEngineering                        controllers.engineering.TypeOfEngineeringController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTypeOfEngineering                  controllers.engineering.TypeOfEngineeringController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTypeOfEngineering                  controllers.engineering.TypeOfEngineeringController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "typeOfEngineering.title = What type of engineering do you work in?" >> ../conf/messages.en
echo "typeOfEngineering.heading = What type of engineering do you work in?" >> ../conf/messages.en
echo "typeOfEngineering.constructionalEngineering = Constructional engineering (includes building, shipyards, bridges and roads)" >> ../conf/messages.en
echo "typeOfEngineering.tradesRelatingToEngineering = Trades relating to engineering" >> ../conf/messages.en
echo "typeOfEngineering.checkYourAnswersLabel = What type of engineering do you work in?" >> ../conf/messages.en
echo "typeOfEngineering.error.required = Select typeOfEngineering" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfEngineeringUserAnswersEntry: Arbitrary[(TypeOfEngineeringPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TypeOfEngineeringPage.type]";\
    print "        value <- arbitrary[TypeOfEngineering].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfEngineeringPage: Arbitrary[TypeOfEngineeringPage.type] =";\
    print "    Arbitrary(TypeOfEngineeringPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfEngineering: Arbitrary[TypeOfEngineering] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TypeOfEngineering.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TypeOfEngineeringPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def typeOfEngineering: Option[AnswerRow] = userAnswers.get(TypeOfEngineeringPage) map {";\
     print "    x => AnswerRow(\"typeOfEngineering.checkYourAnswersLabel\", s\"typeOfEngineering.$x\", true, routes.TypeOfEngineeringController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TypeOfEngineering completed"
