#!/bin/bash

echo ""
echo "Applying migration HealthcareList1"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /healthcareList1                        controllers.healthcare.HealthcareList1Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /healthcareList1                        controllers.healthcare.HealthcareList1Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHealthcareList1                  controllers.healthcare.HealthcareList1Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHealthcareList1                  controllers.healthcare.HealthcareList1Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "healthcareList1.title = healthcareList1" >> ../conf/messages.en
echo "healthcareList1.heading = healthcareList1" >> ../conf/messages.en
echo "healthcareList1.checkYourAnswersLabel = healthcareList1" >> ../conf/messages.en
echo "healthcareList1.error.required = Select yes if healthcareList1" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHealthcareList1UserAnswersEntry: Arbitrary[(HealthcareList1Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HealthcareList1Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHealthcareList1Page: Arbitrary[HealthcareList1Page.type] =";\
    print "    Arbitrary(HealthcareList1Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HealthcareList1Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def healthcareList1: Option[AnswerRow] = userAnswers.get(HealthcareList1Page) map {";\
     print "    x => AnswerRow(\"healthcareList1.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.HealthcareList1Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HealthcareList1 completed"
