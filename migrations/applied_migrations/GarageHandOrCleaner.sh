#!/bin/bash

echo ""
echo "Applying migration GarageHandOrCleaner"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /garageHandOrCleaner                        controllers.GarageHandOrCleanerController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /garageHandOrCleaner                        controllers.GarageHandOrCleanerController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeGarageHandOrCleaner                  controllers.GarageHandOrCleanerController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeGarageHandOrCleaner                  controllers.GarageHandOrCleanerController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "garageHandOrCleaner.title = garageHandOrCleaner" >> ../conf/messages.en
echo "garageHandOrCleaner.heading = garageHandOrCleaner" >> ../conf/messages.en
echo "garageHandOrCleaner.checkYourAnswersLabel = garageHandOrCleaner" >> ../conf/messages.en
echo "garageHandOrCleaner.error.required = Select yes if garageHandOrCleaner" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryGarageHandOrCleanerUserAnswersEntry: Arbitrary[(GarageHandOrCleanerPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[GarageHandOrCleanerPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryGarageHandOrCleanerPage: Arbitrary[GarageHandOrCleanerPage.type] =";\
    print "    Arbitrary(GarageHandOrCleanerPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(GarageHandOrCleanerPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def garageHandOrCleaner: Option[AnswerRow] = userAnswers.get(GarageHandOrCleanerPage) map {";\
     print "    x => AnswerRow(\"garageHandOrCleaner.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.GarageHandOrCleanerController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration GarageHandOrCleaner completed"
