#!/bin/bash

echo "TRAVIS_BRANCH: ${TRAVIS_BRANCH}"
echo "TRAVIS_PULL_REQUEST: ${TRAVIS_PULL_REQUEST}"
echo "TRAVIS_TAG: ${TRAVIS_TAG}"


if [ -n "${TRAVIS_TAG}" ]; then
  echo "Deploy Beta"
  MSG=`git show ${TRAVIS_TAG}`
  echo "${MSG}"
  ./gradlew crashlyticsUploadDistributionProductionDebug -PdisablePreDex --stacktrace --info -DbetaDistributionReleaseNotes="${MSG}"
else
  echo "Skip deployment"
fi
