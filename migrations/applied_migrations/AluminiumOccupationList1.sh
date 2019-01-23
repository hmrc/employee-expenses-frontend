#!/bin/bash

echo ""
echo "Applying migration AluminiumOccupationList1"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /aluminiumOccupationList1                        controllers.manufacturing.AluminiumOccupationList1Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /aluminiumOccupationList1                        controllers.manufacturing.AluminiumOccupationList1Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAluminiumOccupationList1                  controllers.manufacturing.AluminiumOccupationList1Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAluminiumOccupationList1                  controllers.manufacturing.AluminiumOccupationList1Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "aluminiumOccupationList1.title = aluminiumOccupationList1" >> ../conf/messages.en
echo "aluminiumOccupationList1.heading = aluminiumOccupationList1" >> ../conf/messages.en
echo "aluminiumOccupationList1.checkYourAnswersLabel = aluminiumOccupationList1" >> ../conf/messages.en
echo "aluminiumOccupationList1.error.required = Select yes if aluminiumOccupationList1" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAluminiumOccupationList1UserAnswersEntry: Arbitrary[(AluminiumOccupationList1Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AluminiumOccupationList1Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAluminiumOccupationList1Page: Arbitrary[AluminiumOccupationList1Page.type] =";\
    print "    Arbitrary(AluminiumOccupationList1Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AluminiumOccupationList1Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def aluminiumOccupationList1: Option[AnswerRow] = userAnswers.get(AluminiumOccupationList1Page) map {";\
     print "    x => AnswerRow(\"aluminiumOccupationList1.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.AluminiumOccupationList1Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AluminiumOccupationList1 completed"
