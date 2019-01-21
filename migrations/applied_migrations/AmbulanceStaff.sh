#!/bin/bash

echo ""
echo "Applying migration AmbulanceStaff"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /ambulanceStaff                        controllers.healthcare.AmbulanceStaffController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /ambulanceStaff                        controllers.healthcare.AmbulanceStaffController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAmbulanceStaff                  controllers.healthcare.AmbulanceStaffController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAmbulanceStaff                  controllers.healthcare.AmbulanceStaffController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "ambulanceStaff.title = ambulanceStaff" >> ../conf/messages.en
echo "ambulanceStaff.heading = ambulanceStaff" >> ../conf/messages.en
echo "ambulanceStaff.checkYourAnswersLabel = ambulanceStaff" >> ../conf/messages.en
echo "ambulanceStaff.error.required = Select yes if ambulanceStaff" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAmbulanceStaffUserAnswersEntry: Arbitrary[(AmbulanceStaffPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AmbulanceStaffPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAmbulanceStaffPage: Arbitrary[AmbulanceStaffPage.type] =";\
    print "    Arbitrary(AmbulanceStaffPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AmbulanceStaffPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def ambulanceStaff: Option[AnswerRow] = userAnswers.get(AmbulanceStaffPage) map {";\
     print "    x => AnswerRow(\"ambulanceStaff.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.AmbulanceStaffController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AmbulanceStaff completed"
