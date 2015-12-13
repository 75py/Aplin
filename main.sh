#!/bin/bash

./gradlew assembleProductionDebug -PdisablePreDex ${BUILD_OPTIONS}
./gradlew testProductionDebug -PdisablePreDex ${BUILD_OPTIONS}
./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=small -PdisablePreDex ${BUILD_OPTIONS}
./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=medium -PdisablePreDex ${BUILD_OPTIONS}

if [ ${RUN_LARGE_TEST} = "1" ]; then
    ./gradlew connectedProductionDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.size=large -PdisablePreDex ${BUILD_OPTIONS}
fi
