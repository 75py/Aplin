#!/bin/bash

echo "TRAVIS_BRANCH: ${TRAVIS_BRANCH}"
echo "TRAVIS_PULL_REQUEST: ${TRAVIS_PULL_REQUEST}"

if [ ${TRAVIS_BRANCH} = "master" -a ${TRAVIS_PULL_REQUEST} = "false" ]; then
  echo "Deploy Beta"
  MGS=``
  ./gradlew crashlyticsUploadDistributionProductionDebug -PdisablePreDex --stacktrace --info
else
  echo "Skip deployment"
fi
