# Copyright © 2025 CCP ehf.
cmake_minimum_required(VERSION 3.16)
project(CcpTrinityAudioAPI)

include(cmake/CcpBuildConfigurations.cmake)
include(cmake/CcpTargetConfigurations.cmake)

add_library(CcpTrinityAudioAPI INTERFACE)

target_sources(CcpTrinityAudioAPI INTERFACE
    FILE_SET HEADERS
        BASE_DIRS include
        FILES
        include/IAudioInputMgr.h
        include/IStretchAudio.h
        include/ITr2AudEmitter.h
        include/ITr2AudGeometry.h
        include/ITr2BoxVolume.h
        include/ITr2Audio.h
)

# Provide an install target if this is the top level project only
if(CMAKE_PROJECT_NAME STREQUAL CcpTrinityAudioAPI)
    install(
            TARGETS CcpTrinityAudioAPI
            EXPORT TrinityAudioAPITarget
            CONFIGURATIONS ${CMAKE_CONFIGURATION_TYPES}
            FILE_SET HEADERS
    )

    install(
            EXPORT TrinityAudioAPITarget
            DESTINATION share/carbon-trinityaudioapi
            FILE carbon-trinityaudioapiConfig.cmake
    )
endif()

# DEMO ONLY: intentionally crashing test, do not merge
enable_testing()
add_executable(DemoCrashTest demo/crash_test.cpp)
add_test(NAME DemoCrashTest COMMAND DemoCrashTest)
