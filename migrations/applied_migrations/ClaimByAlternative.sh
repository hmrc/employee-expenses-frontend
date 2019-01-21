#!/bin/bash

echo ""
echo "Applying migration ClaimByAlternative"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /claimByAlternative                       controllers.ClaimByAlternativeController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimByAlternative.title = claimByAlternative" >> ../conf/messages.en
echo "claimByAlternative.heading = claimByAlternative" >> ../conf/messages.en

echo "Migration ClaimByAlternative completed"
