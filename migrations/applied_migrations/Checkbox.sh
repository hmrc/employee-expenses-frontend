#!/bin/bash

echo ""
echo "Applying migration Checkbox"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /checkbox                        controllers.CheckboxController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /checkbox                        controllers.CheckboxController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCheckbox                  controllers.CheckboxController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCheckbox                  controllers.CheckboxController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "checkbox.title = checkbox" >> ../conf/messages.en
echo "checkbox.heading = checkbox" >> ../conf/messages.en
echo "checkbox.option1 = option1" >> ../conf/messages.en
echo "checkbox.option2 = option2" >> ../conf/messages.en
echo "checkbox.option3 = option3" >> ../conf/messages.en
echo "checkbox.checkYourAnswersLabel = checkbox" >> ../conf/messages.en
echo "checkbox.error.required = Select checkbox" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCheckboxUserAnswersEntry: Arbitrary[(CheckboxPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CheckboxPage.type]";\
    print "        value <- arbitrary[Checkbox].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCheckboxPage: Arbitrary[CheckboxPage.type] =";\
    print "    Arbitrary(CheckboxPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCheckbox: Arbitrary[Checkbox] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(Checkbox.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserDataGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CheckboxPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserDataGenerator.scala > tmp && mv tmp ../test/generators/UserDataGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def checkbox: Option[AnswerRow] = userAnswers.get(CheckboxPage) map {";\
     print "    x => AnswerRow(";\
     print "        \"checkbox.checkYourAnswersLabel\",";\
     print "        s\"checkbox.$x\",";\
     print "        x.map(value => s\"${messages(s\"checkbox.$value\")}\").mkString(\", <br>\"),";\
     print "        true,";\
     print "        routes.CheckboxController.onPageLoad(CheckMode).url";\
     print "    )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration Checkbox completed"
