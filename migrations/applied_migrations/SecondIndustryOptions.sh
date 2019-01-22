#!/bin/bash

echo ""
echo "Applying migration SecondIndustryOptions"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /secondIndustryOptions                        controllers.SecondIndustryOptionsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /secondIndustryOptions                        controllers.SecondIndustryOptionsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSecondIndustryOptions                  controllers.SecondIndustryOptionsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSecondIndustryOptions                  controllers.SecondIndustryOptionsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "secondIndustryOptions.title = What type of industry do you work in?" >> ../conf/messages.en
echo "secondIndustryOptions.heading = What type of industry do you work in?" >> ../conf/messages.en
echo "secondIndustryOptions.manufacturingWarehousing = Manufacturing and warehousing (including wood, furniture and metals)" >> ../conf/messages.en
echo "secondIndustryOptions.council = Council" >> ../conf/messages.en
echo "secondIndustryOptions.checkYourAnswersLabel = What type of industry do you work in?" >> ../conf/messages.en
echo "secondIndustryOptions.error.required = Select secondIndustryOptions" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondIndustryOptionsUserAnswersEntry: Arbitrary[(SecondIndustryOptionsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SecondIndustryOptionsPage.type]";\
    print "        value <- arbitrary[SecondIndustryOptions].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondIndustryOptionsPage: Arbitrary[SecondIndustryOptionsPage.type] =";\
    print "    Arbitrary(SecondIndustryOptionsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySecondIndustryOptions: Arbitrary[SecondIndustryOptions] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SecondIndustryOptions.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SecondIndustryOptionsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def secondIndustryOptions: Option[AnswerRow] = userAnswers.get(SecondIndustryOptionsPage) map {";\
     print "    x => AnswerRow(\"secondIndustryOptions.checkYourAnswersLabel\", s\"secondIndustryOptions.$x\", true, routes.SecondIndustryOptionsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration SecondIndustryOptions completed"
