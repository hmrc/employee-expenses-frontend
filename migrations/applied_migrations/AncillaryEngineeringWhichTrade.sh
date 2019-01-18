#!/bin/bash

echo ""
echo "Applying migration AncillaryEngineeringWhichTrade"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /ancillaryEngineeringWhichTrade                        controllers.AncillaryEngineeringWhichTradeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /ancillaryEngineeringWhichTrade                        controllers.AncillaryEngineeringWhichTradeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAncillaryEngineeringWhichTrade                  controllers.AncillaryEngineeringWhichTradeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAncillaryEngineeringWhichTrade                  controllers.AncillaryEngineeringWhichTradeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "ancillaryEngineeringWhichTrade.title = Which trade do you work in?" >> ../conf/messages.en
echo "ancillaryEngineeringWhichTrade.heading = Which trade do you work in?" >> ../conf/messages.en
echo "ancillaryEngineeringWhichTrade.patternMaker = Pattern maker" >> ../conf/messages.en
echo "ancillaryEngineeringWhichTrade.labourerSupervisorOrUnskilledWorker = Labourer, supervisor or unskilled worker" >> ../conf/messages.en
echo "ancillaryEngineeringWhichTrade.checkYourAnswersLabel = Which trade do you work in?" >> ../conf/messages.en
echo "ancillaryEngineeringWhichTrade.error.required = Select ancillaryEngineeringWhichTrade" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAncillaryEngineeringWhichTradeUserAnswersEntry: Arbitrary[(AncillaryEngineeringWhichTradePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AncillaryEngineeringWhichTradePage.type]";\
    print "        value <- arbitrary[AncillaryEngineeringWhichTrade].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAncillaryEngineeringWhichTradePage: Arbitrary[AncillaryEngineeringWhichTradePage.type] =";\
    print "    Arbitrary(AncillaryEngineeringWhichTradePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAncillaryEngineeringWhichTrade: Arbitrary[AncillaryEngineeringWhichTrade] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(AncillaryEngineeringWhichTrade.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AncillaryEngineeringWhichTradePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def ancillaryEngineeringWhichTrade: Option[AnswerRow] = userAnswers.get(AncillaryEngineeringWhichTradePage) map {";\
     print "    x => AnswerRow(\"ancillaryEngineeringWhichTrade.checkYourAnswersLabel\", s\"ancillaryEngineeringWhichTrade.$x\", true, routes.AncillaryEngineeringWhichTradeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AncillaryEngineeringWhichTrade completed"
