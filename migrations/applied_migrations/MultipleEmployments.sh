#!/bin/bash

echo ""
echo "Applying migration MultipleEmployments"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /multipleEmployments                        controllers.MultipleEmploymentsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /multipleEmployments                        controllers.MultipleEmploymentsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeMultipleEmployments                  controllers.MultipleEmploymentsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeMultipleEmployments                  controllers.MultipleEmploymentsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "multipleEmployments.title = multipleEmployments" >> ../conf/messages.en
echo "multipleEmployments.heading = multipleEmployments" >> ../conf/messages.en
echo "multipleEmployments.checkYourAnswersLabel = multipleEmployments" >> ../conf/messages.en
echo "multipleEmployments.error.required = Select yes if multipleEmployments" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMultipleEmploymentsUserAnswersEntry: Arbitrary[(MultipleEmploymentsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[MultipleEmploymentsPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryMultipleEmploymentsPage: Arbitrary[MultipleEmploymentsPage.type] =";\
    print "    Arbitrary(MultipleEmploymentsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(MultipleEmploymentsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def multipleEmployments: Option[AnswerRow] = userAnswers.get(MultipleEmploymentsPage) map {";\
     print "    x => AnswerRow(\"multipleEmployments.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.MultipleEmploymentsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration MultipleEmployments completed"
