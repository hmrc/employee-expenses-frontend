#!/bin/bash

echo ""
echo "Applying migration WhichRailwayTrade"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whichRailwayTrade                        controllers.transport.WhichRailwayTradeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whichRailwayTrade                        controllers.transport.WhichRailwayTradeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhichRailwayTrade                  controllers.transport.WhichRailwayTradeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhichRailwayTrade                  controllers.transport.WhichRailwayTradeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whichRailwayTrade.title = Which railway trade do you work in?" >> ../conf/messages.en
echo "whichRailwayTrade.heading = Which railway trade do you work in?" >> ../conf/messages.en
echo "whichRailwayTrade.option1 = Railway vehicle repairers or railway wagon lifters" >> ../conf/messages.en
echo "whichRailwayTrade.option2 = Railway vehicle painters" >> ../conf/messages.en
echo "whichRailwayTrade.checkYourAnswersLabel = Which railway trade do you work in?" >> ../conf/messages.en
echo "whichRailwayTrade.error.required = Select whichRailwayTrade" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichRailwayTradeUserAnswersEntry: Arbitrary[(WhichRailwayTradePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhichRailwayTradePage.type]";\
    print "        value <- arbitrary[WhichRailwayTrade].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichRailwayTradePage: Arbitrary[WhichRailwayTradePage.type] =";\
    print "    Arbitrary(WhichRailwayTradePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhichRailwayTrade: Arbitrary[WhichRailwayTrade] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhichRailwayTrade.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhichRailwayTradePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whichRailwayTrade: Option[AnswerRow] = userAnswers.get(WhichRailwayTradePage) map {";\
     print "    x => AnswerRow(\"whichRailwayTrade.checkYourAnswersLabel\", s\"whichRailwayTrade.$x\", true, routes.WhichRailwayTradeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhichRailwayTrade completed"
