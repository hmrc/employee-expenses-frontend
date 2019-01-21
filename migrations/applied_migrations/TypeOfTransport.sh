#!/bin/bash

echo ""
echo "Applying migration TypeOfTransport"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /typeOfTransport                        controllers.transport.TypeOfTransportController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /typeOfTransport                        controllers.transport.TypeOfTransportController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTypeOfTransport                  controllers.transport.TypeOfTransportController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTypeOfTransport                  controllers.transport.TypeOfTransportController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "typeOfTransport.title = What type of transport and distribution do you work in?" >> ../conf/messages.en
echo "typeOfTransport.heading = What type of transport and distribution do you work in?" >> ../conf/messages.en
echo "typeOfTransport.airlines = Airlines" >> ../conf/messages.en
echo "typeOfTransport.public Transport = PublicTransport" >> ../conf/messages.en
echo "typeOfTransport.checkYourAnswersLabel = What type of transport and distribution do you work in?" >> ../conf/messages.en
echo "typeOfTransport.error.required = Select typeOfTransport" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfTransportUserAnswersEntry: Arbitrary[(TypeOfTransportPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TypeOfTransportPage.type]";\
    print "        value <- arbitrary[TypeOfTransport].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfTransportPage: Arbitrary[TypeOfTransportPage.type] =";\
    print "    Arbitrary(TypeOfTransportPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfTransport: Arbitrary[TypeOfTransport] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TypeOfTransport.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TypeOfTransportPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def typeOfTransport: Option[AnswerRow] = userAnswers.get(TypeOfTransportPage) map {";\
     print "    x => AnswerRow(\"typeOfTransport.checkYourAnswersLabel\", s\"typeOfTransport.$x\", true, routes.TypeOfTransportController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TypeOfTransport completed"
