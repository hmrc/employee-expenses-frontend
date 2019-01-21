#!/bin/bash

echo ""
echo "Applying migration HealhcareList1"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /healhcareList1                        controllers.HealhcareList1Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /healhcareList1                        controllers.HealhcareList1Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHealhcareList1                  controllers.HealhcareList1Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHealhcareList1                  controllers.HealhcareList1Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "healhcareList1.title = healhcareList1" >> ../conf/messages.en
echo "healhcareList1.heading = healhcareList1" >> ../conf/messages.en
echo "healhcareList1.checkYourAnswersLabel = healhcareList1" >> ../conf/messages.en
echo "healhcareList1.error.required = Select yes if healhcareList1" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHealhcareList1UserAnswersEntry: Arbitrary[(HealhcareList1Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HealhcareList1Page.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHealhcareList1Page: Arbitrary[HealhcareList1Page.type] =";\
    print "    Arbitrary(HealhcareList1Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HealhcareList1Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def healhcareList1: Option[AnswerRow] = userAnswers.get(HealhcareList1Page) map {";\
     print "    x => AnswerRow(\"healhcareList1.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.HealhcareList1Controller.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration HealhcareList1 completed"
