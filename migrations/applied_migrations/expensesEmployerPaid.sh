#!/bin/bash

echo ""
echo "Applying migration expensesEmployerPaid"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /expensesEmployerPaid                  controllers.expensesEmployerPaidController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /expensesEmployerPaid                  controllers.expensesEmployerPaidController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeexpensesEmployerPaid                        controllers.expensesEmployerPaidController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeexpensesEmployerPaid                        controllers.expensesEmployerPaidController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "expensesEmployerPaid.title = expensesEmployerPaid" >> ../conf/messages.en
echo "expensesEmployerPaid.heading = expensesEmployerPaid" >> ../conf/messages.en
echo "expensesEmployerPaid.checkYourAnswersLabel = expensesEmployerPaid" >> ../conf/messages.en
echo "expensesEmployerPaid.error.nonNumeric = Enter your expensesEmployerPaid using numbers" >> ../conf/messages.en
echo "expensesEmployerPaid.error.required = Enter your expensesEmployerPaid" >> ../conf/messages.en
echo "expensesEmployerPaid.error.wholeNumber = Enter your expensesEmployerPaid using whole numbers" >> ../conf/messages.en
echo "expensesEmployerPaid.error.outOfRange = expensesEmployerPaid must be between {0} and {1}" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryexpensesEmployerPaidUserAnswersEntry: Arbitrary[(expensesEmployerPaidPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[expensesEmployerPaidPage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryexpensesEmployerPaidPage: Arbitrary[expensesEmployerPaidPage.type] =";\
    print "    Arbitrary(expensesEmployerPaidPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(expensesEmployerPaidPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def expensesEmployerPaid: Option[AnswerRow] = userAnswers.get(expensesEmployerPaidPage) map {";\
     print "    x => AnswerRow(\"expensesEmployerPaid.checkYourAnswersLabel\", s\"$x\", false, routes.expensesEmployerPaidController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration expensesEmployerPaid completed"
