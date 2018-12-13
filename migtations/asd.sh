#!/bin/bash

echo ""
echo "Applying migration asd"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /asd                        controllers.asdController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /asd                        controllers.asdController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeasd                  controllers.asdController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeasd                  controllers.asdController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "asd.title = asdf" >> ../conf/messages.en
echo "asd.heading = asdf" >> ../conf/messages.en
echo "asd.asdsf = adf" >> ../conf/messages.en
echo "asd.asdsfs = adf" >> ../conf/messages.en
echo "asd.checkYourAnswersLabel = asdf" >> ../conf/messages.en
echo "asd.error.required = Select asd" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryasdUserAnswersEntry: Arbitrary[(asdPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[asdPage.type]";\
    print "        value <- arbitrary[asd].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryasdPage: Arbitrary[asdPage.type] =";\
    print "    Arbitrary(asdPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryasd: Arbitrary[asd] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(asd.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserDataGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(asdPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserDataGenerator.scala > tmp && mv tmp ../test/generators/UserDataGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def asd: Option[AnswerRow] = userAnswers.get(asdPage) map {";\
     print "    x => AnswerRow(\"asd.checkYourAnswersLabel\", s\"asd.$x\", true, routes.asdController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration asd completed"
