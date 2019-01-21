#!/bin/bash

echo ""
echo "Applying migration WhatTypeOfIndustryList2"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whatTypeOfIndustryList2                        controllers.WhatTypeOfIndustryList2Controller.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whatTypeOfIndustryList2                        controllers.WhatTypeOfIndustryList2Controller.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhatTypeOfIndustryList2                  controllers.WhatTypeOfIndustryList2Controller.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhatTypeOfIndustryList2                  controllers.WhatTypeOfIndustryList2Controller.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whatTypeOfIndustryList2.title = What type of industry do you work in?" >> ../conf/messages.en
echo "whatTypeOfIndustryList2.heading = What type of industry do you work in?" >> ../conf/messages.en
echo "whatTypeOfIndustryList2.manufacturing = Manufacturing and warehousing (including wood, furniture and metals)" >> ../conf/messages.en
echo "whatTypeOfIndustryList2.council = Council" >> ../conf/messages.en
echo "whatTypeOfIndustryList2.checkYourAnswersLabel = What type of industry do you work in?" >> ../conf/messages.en
echo "whatTypeOfIndustryList2.error.required = Select whatTypeOfIndustryList2" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatTypeOfIndustryList2UserAnswersEntry: Arbitrary[(WhatTypeOfIndustryList2Page.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhatTypeOfIndustryList2Page.type]";\
    print "        value <- arbitrary[WhatTypeOfIndustryList2].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatTypeOfIndustryList2Page: Arbitrary[WhatTypeOfIndustryList2Page.type] =";\
    print "    Arbitrary(WhatTypeOfIndustryList2Page)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhatTypeOfIndustryList2: Arbitrary[WhatTypeOfIndustryList2] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhatTypeOfIndustryList2.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhatTypeOfIndustryList2Page.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def whatTypeOfIndustryList2: Option[AnswerRow] = userAnswers.get(WhatTypeOfIndustryList2Page) map {";\
     print "    x => AnswerRow(\"whatTypeOfIndustryList2.checkYourAnswersLabel\", s\"whatTypeOfIndustryList2.$x\", true, routes.WhatTypeOfIndustryList2Controller.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration WhatTypeOfIndustryList2 completed"
