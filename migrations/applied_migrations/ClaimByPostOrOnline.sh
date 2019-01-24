#!/bin/bash

echo ""
echo "Applying migration ClaimByPostOrOnline"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /claimByPostOrOnline                       controllers.ClaimByPostOrOnlineController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "claimByPostOrOnline.title = claimByPostOrOnline" >> ../conf/messages.en
echo "claimByPostOrOnline.heading = claimByPostOrOnline" >> ../conf/messages.en

echo "Migration ClaimByPostOrOnline completed"
