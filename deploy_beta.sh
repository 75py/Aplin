#!/bin/bash

echo $DEPLOY_BETA

if [ $DEPLOY_BETA = 1 ]; then
  echo "Deploy Beta"
  ./gradlew crashlyticsUploadDistributionDebug -PdisablePreDex --stacktrace --info
else
  echo "DEPLOY_BETA is not 1"
fi

