#!/bin/bash

echo 'assembleProductionDebug'
./gradlew assembleProductionDebug -PdisablePreDex ${BUILD_OPTIONS}

echo 'testProductionDebug'
./gradlew testProductionDebug -PdisablePreDex ${BUILD_OPTIONS}

echo 'connectedProductionDebugAndroidTest small'
./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=small -PdisablePreDex ${BUILD_OPTIONS}

echo 'connectedProductionDebugAndroidTest medium'
./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=medium -PdisablePreDex ${BUILD_OPTIONS}

if [ ${RUN_LARGE_TEST} = "1" ]; then
    echo 'connectedProductionDebugAndroidTest large'
    ./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=large -PdisablePreDex ${BUILD_OPTIONS}
fi
