#!/bin/bash

echo 'build & test'
./gradlew assembleProductionDebug testProductionDebug connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=small -PdisablePreDex ${BUILD_OPTIONS}

if [ ${RUN_MEDIUM_TEST} = "1" ]; then
    echo 'connectedProductionDebugAndroidTest medium'
    ./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=medium -PdisablePreDex ${BUILD_OPTIONS}
fi

if [ ${RUN_LARGE_TEST} = "1" ]; then
    echo 'connectedProductionDebugAndroidTest large'
    ./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=large -PdisablePreDex ${BUILD_OPTIONS}
fi
