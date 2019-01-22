#!/bin/bash

echo ""
echo "Applying migration TransportCarpenter"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /transportCarpenter                        controllers.transport.TransportCarpenterController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /transportCarpenter                        controllers.transport.TransportCarpenterController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTransportCarpenter                  controllers.transport.TransportCarpenterController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTransportCarpenter                  controllers.transport.TransportCarpenterController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "transportCarpenter.title = transportCarpenter" >> ../conf/messages.en
echo "transportCarpenter.heading = transportCarpenter" >> ../conf/messages.en
echo "transportCarpenter.checkYourAnswersLabel = transportCarpenter" >> ../conf/messages.en
echo "transportCarpenter.error.required = Select yes if transportCarpenter" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTransportCarpenterUserAnswersEntry: Arbitrary[(TransportCarpenterPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TransportCarpenterPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTransportCarpenterPage: Arbitrary[TransportCarpenterPage.type] =";\
    print "    Arbitrary(TransportCarpenterPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TransportCarpenterPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def transportCarpenter: Option[AnswerRow] = userAnswers.get(TransportCarpenterPage) map {";\
     print "    x => AnswerRow(\"transportCarpenter.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.TransportCarpenterController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TransportCarpenter completed"
