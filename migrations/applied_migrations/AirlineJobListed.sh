#!/bin/bash

echo ""
echo "Applying migration AirlineJobList"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /airlineJobListed                        controllers.transport.AirlineJobListController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /airlineJobListed                        controllers.transport.AirlineJobListController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAirlineJobList                  controllers.transport.AirlineJobListController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAirlineJobList                  controllers.transport.AirlineJobListController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "airlineJobListed.title = airlineJobListed" >> ../conf/messages.en
echo "airlineJobListed.heading = airlineJobListed" >> ../conf/messages.en
echo "airlineJobListed.checkYourAnswersLabel = airlineJobListed" >> ../conf/messages.en
echo "airlineJobListed.error.required = Select yes if airlineJobListed" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAirlineJobListUserAnswersEntry: Arbitrary[(AirlineJobListPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AirlineJobListPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAirlineJobListPage: Arbitrary[AirlineJobListPage.type] =";\
    print "    Arbitrary(AirlineJobListPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AirlineJobListPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def airlineJobListed: Option[AnswerRow] = userAnswers.get(AirlineJobListPage) map {";\
     print "    x => AnswerRow(\"airlineJobListed.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.AirlineJobListController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AirlineJobList completed"
