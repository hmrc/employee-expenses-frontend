#!/bin/bash

echo ""
echo "Applying migration TransportVehicleTrade"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /transportVehicleTrade                        controllers.transport.TransportVehicleTradeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /transportVehicleTrade                        controllers.transport.TransportVehicleTradeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTransportVehicleTrade                  controllers.transport.TransportVehicleTradeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTransportVehicleTrade                  controllers.transport.TransportVehicleTradeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "transportVehicleTrade.title = Which vehicle trade do you work in?" >> ../conf/messages.en
echo "transportVehicleTrade.heading = Which vehicle trade do you work in?" >> ../conf/messages.en
echo "transportVehicleTrade.builder = Builder" >> ../conf/messages.en
echo "transportVehicleTrade.vehicleRepairerWagonLifter = VehicleRepairerWagonLifter" >> ../conf/messages.en
echo "transportVehicleTrade.checkYourAnswersLabel = Which vehicle trade do you work in?" >> ../conf/messages.en
echo "transportVehicleTrade.error.required = Select transportVehicleTrade" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTransportVehicleTradeUserAnswersEntry: Arbitrary[(TransportVehicleTradePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TransportVehicleTradePage.type]";\
    print "        value <- arbitrary[TransportVehicleTrade].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTransportVehicleTradePage: Arbitrary[TransportVehicleTradePage.type] =";\
    print "    Arbitrary(TransportVehicleTradePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTransportVehicleTrade: Arbitrary[TransportVehicleTrade] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TransportVehicleTrade.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TransportVehicleTradePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def transportVehicleTrade: Option[AnswerRow] = userAnswers.get(TransportVehicleTradePage) map {";\
     print "    x => AnswerRow(\"transportVehicleTrade.checkYourAnswersLabel\", s\"transportVehicleTrade.$x\", true, routes.TransportVehicleTradeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TransportVehicleTrade completed"
