#!/bin/bash

echo ""
echo "Applying migration TypeOfManufacturing"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /typeOfManufacturing                        controllers.manufacturing.TypeOfManufacturingController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /typeOfManufacturing                        controllers.manufacturing.TypeOfManufacturingController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeTypeOfManufacturing                  controllers.manufacturing.TypeOfManufacturingController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeTypeOfManufacturing                  controllers.manufacturing.TypeOfManufacturingController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "typeOfManufacturing.title = What type of manufacturing do you work in?" >> ../conf/messages.en
echo "typeOfManufacturing.heading = What type of manufacturing do you work in?" >> ../conf/messages.en
echo "typeOfManufacturing.aluminium = Aluminium" >> ../conf/messages.en
echo "typeOfManufacturing.brassCopper = Brass and copper" >> ../conf/messages.en
echo "typeOfManufacturing.checkYourAnswersLabel = What type of manufacturing do you work in?" >> ../conf/messages.en
echo "typeOfManufacturing.error.required = Select typeOfManufacturing" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfManufacturingUserAnswersEntry: Arbitrary[(TypeOfManufacturingPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[TypeOfManufacturingPage.type]";\
    print "        value <- arbitrary[TypeOfManufacturing].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfManufacturingPage: Arbitrary[TypeOfManufacturingPage.type] =";\
    print "    Arbitrary(TypeOfManufacturingPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryTypeOfManufacturing: Arbitrary[TypeOfManufacturing] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(TypeOfManufacturing.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(TypeOfManufacturingPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def typeOfManufacturing: Option[AnswerRow] = userAnswers.get(TypeOfManufacturingPage) map {";\
     print "    x => AnswerRow(\"typeOfManufacturing.checkYourAnswersLabel\", s\"typeOfManufacturing.$x\", true, routes.TypeOfManufacturingController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration TypeOfManufacturing completed"
