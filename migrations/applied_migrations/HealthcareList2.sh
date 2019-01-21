#!/bin/bash

echo ""
echo "Applying migration HealthcareList2"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /healthcareList2                        controllers.healthcare.HealthcareList2Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /healthcareList2                        controllers.healthcare.HealthcareList2Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHealthcareList2                  controllers.healthcare.HealthcareList2Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHealthcareList2                  controllers.healthcare.HealthcareList2Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "healthcareList2.title = healthcareList2" >> ../conf/messages.en
echo "healthcareList2.heading = healthcareList2" >> ../conf/messages.en
echo "healthcareList2.checkYourAnswersLabel = healthcareList2" >> ../conf/messages.en
echo "healthcareList2.error.required = Select yes if healthcareList2" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHealthcareList2UserAnswersEntry: Arbitrary[(HealthcareList2Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HealthcareList2Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHealthcareList2Page: Arbitrary[HealthcareList2Page.type] =";\
    print "    Arbitrary(HealthcareList2Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HealthcareList2Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def healthcareList2: Option[AnswerRow] = userAnswers.get(HealthcareList2Page) map {";\
     print "    x => AnswerRow(\"healthcareList2.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.HealthcareList2Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HealthcareList2 completed"
