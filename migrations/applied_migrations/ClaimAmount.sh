#!/bin/bash

echo ""
echo "Applying migration ClaimAmount"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /claimAmount                       controllers.ClaimAmountController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimAmount.title = claimAmount" >> ../conf/messages.en
echo "claimAmount.heading = claimAmount" >> ../conf/messages.en

echo "Migration ClaimAmount completed"
