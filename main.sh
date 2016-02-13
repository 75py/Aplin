#!/bin/bash

echo 'build & test'
./gradlew assembleDebug testDebug connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=small -PdisablePreDex ${BUILD_OPTIONS}

if [ ${RUN_MEDIUM_TEST} = "1" ]; then
    echo 'connectedDebugAndroidTest medium'
    ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=medium -PdisablePreDex ${BUILD_OPTIONS}
fi

if [ ${RUN_LARGE_TEST} = "1" ]; then
    echo 'connectedDebugAndroidTest large'
    ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=large -PdisablePreDex ${BUILD_OPTIONS}
fi
