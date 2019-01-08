#!/bin/bash

echo ""
echo "Applying migration CannotClaim"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /cannotClaim                       controllers.CannotClaimController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cannotClaim.title = cannotClaim" >> ../conf/messages.en
echo "cannotClaim.heading = cannotClaim" >> ../conf/messages.en

echo "Migration CannotClaim completed"
